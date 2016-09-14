package com.duoshouji.server.internal.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.dao.BasicNoteDto;
import com.duoshouji.server.service.dao.BasicUserDto;
import com.duoshouji.server.service.dao.NoteDetailDto;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.note.CommentPublishAttributes;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MobileNumber;

@Service
public class MysqlUserNoteDao implements UserNoteDao {

	private JdbcTemplate mysqlDataSource;

	@Autowired
	public MysqlUserNoteDao(JdbcTemplate mysqlDataSource) {
		this.mysqlDataSource = mysqlDataSource;
	}

	@Override
	public RegisteredUserDto findUser(MobileNumber mobileNumber) {
		return queryUser("select * from duoshouji.v_user where mobile = " + mobileNumber);
	}
	
	private RegisteredUserDto queryUser(String sql) {
		return mysqlDataSource.query(sql
				, new ResultSetExtractor<RegisteredUserDto>(){
					@Override
					public RegisteredUserDto extractData(ResultSet rs)
						throws SQLException, DataAccessException {
						RegisteredUserDto userDto = null;
						if (rs.next()) {
							userDto = new RegisteredUserDto();
							mapBasicUserDto(userDto, rs);
							userDto.passwordDigest = rs.getString("password");
							userDto.totalRevenue = rs.getBigDecimal("balance");
							userDto.publishedNoteCount = rs.getInt("note_number");
							userDto.transactionCount = rs.getInt("order_number");
							userDto.watchCount = rs.getInt("follow_number");
							userDto.fanCount = rs.getInt("followed_number");
						}
						return userDto;
				}
		});
	}

	private void mapBasicUserDto(BasicUserDto basicUserDto, ResultSet rs) throws SQLException {
		basicUserDto.mobileNumber = new MobileNumber(Long.toString(rs.getLong("mobile")));
		basicUserDto.nickname = rs.getString("user_name");
		if (rs.getString("avatar_url") != null) {
			basicUserDto.portrait = new Image(
					rs.getInt("avatar_width")
					, rs.getInt("avatar_height")
					, rs.getString("avatar_url"));
		}
		if (rs.getString("gender") != null) {
			basicUserDto.gender = Gender.valueOf(rs.getString("gender").trim());
		}
	}
	
	@Override
	public NoteDetailDto findNote(long noteId) {
		NoteDetailDto noteDto = mysqlDataSource.query("select * from duoshouji.v_square_notes where id = " + noteId
				, new ResultSetExtractor<NoteDetailDto>() {

					@Override
					public NoteDetailDto extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						NoteDetailDto result = null;
						if (rs.next()) {
							result = new NoteDetailDto();
							mapNoteDetailDto(result, rs);
						}
						return result;
					}
			
				});
		if (noteDto != null) {
			noteDto.images = mysqlDataSource.query("select * from duoshouji.note_image where note_id = " + noteId, new RowMapper<Image>() {

				@Override
				public Image mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return new Image(
							rs.getInt("image_width")
							, rs.getInt("image_height")
							, rs.getString("image_url")
							);
				}
				
			});
		}
		return noteDto;
	}
	
	private void mapNoteDetailDto(NoteDetailDto noteDto, ResultSet rs) throws SQLException {
		mapNoteBriefDto(noteDto, rs);
		noteDto.content = rs.getString("content");
		noteDto.tagIds = new long[9];
		int i;
		for (i = 0; i < 9; ++i) {
			final String column = "tag_id" + (i + 1);
			BigDecimal tagId = rs.getBigDecimal(column);
			if (rs.wasNull()) {
				break;
			}
			noteDto.tagIds[i] = tagId.longValue();
		}
		noteDto.tagIds = Arrays.copyOf(noteDto.tagIds, i);
	}

	private void mapNoteBriefDto(BasicNoteDto noteDto, ResultSet rs) throws SQLException {
		noteDto.commentCount = rs.getInt("comment_number");
		noteDto.likeCount = rs.getInt("like_number");
		noteDto.mainImage = new Image(rs.getInt("main_image_width"), rs.getInt("main_image_height"), rs.getString("main_image_url"));
		noteDto.noteId = rs.getLong("id");
		noteDto.publishedTime = rs.getLong("create_time");
		noteDto.commentRatingSum = rs.getInt("comment_rating");
		noteDto.ownerRating = rs.getInt("owner_rating");
		noteDto.title = rs.getString("title");
		noteDto.transactionCount = rs.getInt("order_number");
		BasicUserDto userDto = new BasicUserDto();
		mapBasicUserDto(userDto, rs);
		noteDto.owner = userDto;
	}
	
	@Override
	public List<BasicNoteDto> findNotes(long cutoff, IndexRange range, MobileNumber userId) {
		return findNotes(cutoff, range, (Object) userId);
	}
	
	@Override
	public List<BasicNoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter) {
		return findNotes(cutoff, range, (Object) filter);
	}
	
	private List<BasicNoteDto> findNotes(long cutoff, IndexRange range, Object filter) {
		StringBuilder sqlBuilder = new StringBuilder("select * from duoshouji.v_square_notes where create_time < " + cutoff);
		if (filter != null) {
			if (filter instanceof NoteFilter) {
				final NoteFilter that = (NoteFilter) filter;
				if (that.isTagSet()) {
					sqlBuilder.append(" and " + buildContainsTagIdClause(that.getTag().getIdentifier()));
				}
			}
			if (filter instanceof MobileNumber) {
				final MobileNumber that = (MobileNumber) filter;
				sqlBuilder.append(" and mobile = " + that);
			}
		}
		sqlBuilder.append(" order by create_time desc");
		List<BasicNoteDto> returnValue = mysqlDataSource.query(sqlBuilder.toString()
				, new RowMapper<BasicNoteDto>(){
					@Override
					public BasicNoteDto mapRow(ResultSet rs, int rowNum) throws SQLException {
						BasicNoteDto noteDto = new BasicNoteDto();
						mapNoteBriefDto(noteDto, rs);
						return noteDto;
					}
				});
		if (range != null) {
			returnValue = returnValue.subList(
					Math.max(range.getStartIndex(), 0)
					, Math.min(range.getEndIndex(), returnValue.size()));
		}
		return returnValue;
	}

	
	private String buildContainsTagIdClause(long tagId) {
		StringBuilder sqlBuilder = new StringBuilder("(");
		sqlBuilder.append(" tag_id1 = " + tagId);
		for (int i = 2; i <= 9; ++i) {
			sqlBuilder.append(" or tag_id"+i+" = " + tagId);
		}
		sqlBuilder.append(")");
		return sqlBuilder.toString();
	}
	
	@Override
	public void createUser(final MobileNumber mobileNumber) {
		mysqlDataSource.update("insert into duoshouji.user (mobile, user_name) values(?, ?)"
				, new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, mobileNumber.toLong());
						ps.setString(2, mobileNumber.toString());
					}
		});
		mysqlDataSource.update("insert into duoshouji.user_extend (user_id) values(?)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, mobileNumber.toLong());
					}
					
				});
	}

	@Override
	public void saveNickname(final MobileNumber mobileNumber, final String nickname) {
		mysqlDataSource.update("update duoshouji.user set user_name = ? where mobile = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, nickname);
						ps.setLong(2, mobileNumber.toLong());
					}
				});		
	}

	@Override
	public void saveGender(final MobileNumber mobileNumber, final Gender gender) {
		mysqlDataSource.update("update duoshouji.user set gender = ? where mobile = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, gender.toString());
						ps.setLong(2, mobileNumber.toLong());
					}
				});				
	}

	@Override
	public void savePasswordDigest(final MobileNumber mobileNumber, final String passwordDigest) {
		mysqlDataSource.update("update duoshouji.user set password = ? where mobile = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, passwordDigest);
						ps.setLong(2, Long.valueOf(mobileNumber.toString()));
					}
				});	
	}

	@Override
	public long createNote(final MobileNumber mobileNumber,
			final NotePublishAttributes noteAttributes) {
		final long userId = getUserId(mobileNumber);
		final long time = System.currentTimeMillis();
		mysqlDataSource.update(buildInsertNoteClause(noteAttributes.getTagCount())
				, new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setBigDecimal(1, BigDecimal.valueOf(noteAttributes.getCategory().getIdentifier()));
						ps.setBigDecimal(2, BigDecimal.valueOf(noteAttributes.getBrand().getIdentifier()));
						ps.setString(3, noteAttributes.getProductName());
						ps.setBigDecimal(4, noteAttributes.getPrice());
						ps.setBigDecimal(5, BigDecimal.valueOf(noteAttributes.getDistrict().getIdentifier()));
						ps.setBigDecimal(6, noteAttributes.getLocation().getLongitude());
						ps.setBigDecimal(7, noteAttributes.getLocation().getLatitude());
						ps.setString(8, noteAttributes.getTitle());
						ps.setString(9, noteAttributes.getContent());
						ps.setLong(10, time);
						ps.setBigDecimal(11, BigDecimal.valueOf(mobileNumber.toLong()));
						ps.setLong(12, time);
						ps.setInt(13, noteAttributes.getRating());
						int parameterIndex = 14;
						for (Tag tag : noteAttributes.getTags()) {
							ps.setBigDecimal(parameterIndex++, BigDecimal.valueOf(tag.getIdentifier()));
						}
					}
				});
		mysqlDataSource.update("update duoshouji.user_extend set note_number = note_number + 1 where user_id = " + mobileNumber);
		final Long noteId = mysqlDataSource.query("select max(id) note_id from duoshouji.note where user_id = " + userId
				, new ResultSetExtractor<Long>(){
					@Override
					public Long extractData(ResultSet rs) throws SQLException,
							DataAccessException {
						rs.next();
						return rs.getLong("note_id");
					}
				});
		mysqlDataSource.update("insert into duoshouji.note_extend (note_id) values ("+noteId+")");
		return noteId.longValue();
	}
	
	private String buildInsertNoteClause(int tagCount) {
		StringBuilder sqlBuilder = new StringBuilder(
				"insert into duoshouji.note (category_id, brand_id, product_name, price, district_id, longitude, latitude, title, content, create_time, user_id, last_update_time, rating");
		for (int i = 0; i < tagCount; ++i) {
			sqlBuilder.append(", tag_id");
			sqlBuilder.append(i + 1);
		}
		sqlBuilder.append(") values(?,?,?,?,?,?,?,?,?,?,?,?");
		for (int i = 0; i < tagCount; ++i) {
			sqlBuilder.append(",?");
		}
		sqlBuilder.append(")");
		return sqlBuilder.toString();
	}
	
	private long getUserId(MobileNumber mobileNumber) {
		return mysqlDataSource.query("select id from duoshouji.user where mobile = " + mobileNumber
				, new ResultSetExtractor<Long>(){
					@Override
					public Long extractData(ResultSet rs) throws SQLException,
							DataAccessException {
						rs.next();
						return rs.getLong("id");
					}
					
				}).longValue();
	}

	@Override
	public void savePortrait(final MobileNumber mobileNumber, final Image portrait) {
		mysqlDataSource.update(
				"update duoshouji.user set avatar_url = ?, avatar_width = ?, avatar_height = ? where mobile = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, portrait.getUrl());
						ps.setInt(2, portrait.getWidth());
						ps.setInt(3, portrait.getHeight());
						ps.setLong(4, Long.valueOf(mobileNumber.toString()));
					}
				});
	}

	@Override
	public void saveNoteImage(final long noteId, final Image noteImage) {
		mysqlDataSource.update(
				"update duoshouji.note set image1 = ?, image1_width = ?, image1_height = ? where id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, noteImage.getUrl());
						ps.setInt(2, noteImage.getWidth());
						ps.setInt(3, noteImage.getHeight());
						ps.setLong(4, noteId);
					}
				});		
	}

	@Override
	public MobileNumber findNoteOwner(long noteId) {
		return mysqlDataSource.query("select mobile from duoshouji.v_square_notes where id = " + noteId
				, new ResultSetExtractor<MobileNumber>(){

					@Override
					public MobileNumber extractData(ResultSet rs) throws SQLException, DataAccessException {
						MobileNumber accountId = null;
						if (rs.next()) {
							accountId = MobileNumber.valueOf(rs.getLong("mobile"));
						}
						return accountId;
					}
				});
	}

	@Override
	public void createComment(final long noteId, final CommentPublishAttributes commentAttributes, final MobileNumber userId) {
		mysqlDataSource.update(
				"insert into doushouji.comment (note_id, user_id, content, created_time, rating, longitude, latitude) values (?,?,?,?,?,?,?)"
				, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, (int) noteId);
				ps.setLong(2, userId.toLong());
				ps.setString(3, commentAttributes.getComment());
				ps.setLong(4, System.currentTimeMillis());
				ps.setInt(5, commentAttributes.getRating());
				ps.setBigDecimal(6, commentAttributes.getLocation().getLongitude());
				ps.setBigDecimal(7, commentAttributes.getLocation().getLatitude());
			}
			
		});
		mysqlDataSource.update("update duoshouji.note_extend set comment_number = comment_number + 1, rating_sum = rating_sum + ? where note_id = ?"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, commentAttributes.getRating());
						ps.setInt(2, (int) noteId);
					}
					
				});
	}

	@Override
	public void saveUserLikeNote(final long noteId, final MobileNumber userId) {
		mysqlDataSource.update("insert into duoshouji.likes (user_id, note_id) values (?,?)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, userId.toLong());
						ps.setInt(2, (int) noteId);
					}
					
				});
		mysqlDataSource.update("update duoshouji.note_extend set like_number = like_number + 1 where note_id = " + noteId);
	}

	@Override
	public void addWatchConnection(final MobileNumber fanId, final MobileNumber watchedUserId) {
		mysqlDataSource.update("insert into duoshouji.follow (user_id, fan_user_id, created_time) values (?,?,?)"
				, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, watchedUserId.toLong());
				ps.setLong(2, fanId.toLong());
				ps.setLong(3, System.currentTimeMillis());
			}
			
		});
	}
}
