package com.duoshouji.server.internal.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

	private static final long BASE_NOTE_ID = 1000000000000l;
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
		NoteDetailDto noteDto = mysqlDataSource.query("select * from duoshouji.v_square_notes where note_id = " + noteId
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
		noteDto.productName = rs.getString("product_name");
	}

	private void mapNoteBriefDto(BasicNoteDto noteDto, ResultSet rs) throws SQLException {
		noteDto.commentCount = rs.getInt("comment_number");
		noteDto.likeCount = rs.getInt("like_number");
		noteDto.mainImage = new Image(rs.getInt("main_image_width"), rs.getInt("main_image_height"), rs.getString("main_image_url"));
		noteDto.noteId = rs.getLong("note_id");
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
		return findNotes(range, new PublishedNotesSqlQueryBuilder(cutoff, userId));
	}
	
	@Override
	public List<BasicNoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter, MobileNumber userId) {
		return findNotes(range, new SquareNoteSqlQueryBuilder(cutoff, userId, filter));
	}
	
	private List<BasicNoteDto> findNotes(IndexRange range, NoteListSqlQuery sqlQuery) {
		List<BasicNoteDto> returnValue = mysqlDataSource.query(
				sqlQuery.buildSqlQuery()
				, sqlQuery
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

	private static abstract class NoteListSqlQuery implements PreparedStatementSetter {
		private long cutoff;
		
		public NoteListSqlQuery(long cutoff) {
			this.cutoff = cutoff;
		}

		final String buildSqlQuery() {
			StringBuilder sqlBuilder = new StringBuilder("select * from duoshouji.v_square_notes where create_time < ?");
			appendWhereConditions(sqlBuilder);
			sqlBuilder.append(" order by create_time desc");
			return sqlBuilder.toString();
		}
		
		abstract void appendWhereConditions(StringBuilder sqlBuilder);

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setLong(1, cutoff);
		}
	}
	
	private static class PublishedNotesSqlQueryBuilder extends NoteListSqlQuery {
		private MobileNumber userId;

		public PublishedNotesSqlQueryBuilder(long cutoff, MobileNumber userId) {
			super(cutoff);
			this.userId = userId;
		}

		@Override
		void appendWhereConditions(StringBuilder sqlBuilder) {
			sqlBuilder.append(" and mobile = ?");
		}

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			super.setValues(ps);
			ps.setLong(2, userId.toLong());
		}
		
	}
	
	private static class SquareNoteSqlQueryBuilder extends NoteListSqlQuery {
		private MobileNumber userId;
		private NoteFilter noteFilter;
		
		public SquareNoteSqlQueryBuilder(long cutoff, MobileNumber userId, NoteFilter noteFilter) {
			super(cutoff);
			this.userId = userId;
			this.noteFilter = noteFilter;
		}

		@Override
		void appendWhereConditions(StringBuilder sqlBuilder) {
			if (noteFilter != null) {
				if (noteFilter.isTagSet()) {
					sqlBuilder.append(" and " + buildContainsTagIdClause(noteFilter.getTag().getIdentifier()));
				}
			}
			if (userId != null) {
				sqlBuilder.append(" and mobile in (select user_id from duoshouji.follow where is_activated = 1 and fan_user_id = ?)");
			}
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
		public void setValues(PreparedStatement ps) throws SQLException {
			super.setValues(ps);
			if (userId != null) {
				ps.setLong(2, userId.toLong());
			}
		}
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
		final long time = System.currentTimeMillis();
		final long noteId = BASE_NOTE_ID + mysqlDataSource.queryForObject("select count(*) from duoshouji.note", Integer.class);
		mysqlDataSource.update(buildInsertNoteClause(noteAttributes.getTagCount())
				, new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setBigDecimal(1, BigDecimal.valueOf(noteId));
						ps.setBigDecimal(2, BigDecimal.valueOf(noteAttributes.getCategory().getIdentifier()));
						ps.setBigDecimal(3, BigDecimal.valueOf(noteAttributes.getBrand().getIdentifier()));
						ps.setString(4, noteAttributes.getProductName());
						ps.setBigDecimal(5, noteAttributes.getPrice());
						ps.setBigDecimal(6, BigDecimal.valueOf(noteAttributes.getDistrict().getIdentifier()));
						ps.setBigDecimal(7, noteAttributes.getLocation().getLongitude());
						ps.setBigDecimal(8, noteAttributes.getLocation().getLatitude());
						ps.setString(9, noteAttributes.getTitle());
						ps.setString(10, noteAttributes.getContent());
						ps.setLong(11, time);
						ps.setBigDecimal(12, BigDecimal.valueOf(mobileNumber.toLong()));
						ps.setLong(13, time);
						ps.setInt(14, noteAttributes.getRating());
						ps.setString(15, noteAttributes.getProductName());
						int parameterIndex = 16;
						for (Tag tag : noteAttributes.getTags()) {
							ps.setBigDecimal(parameterIndex++, BigDecimal.valueOf(tag.getIdentifier()));
						}
					}
				});
		mysqlDataSource.update(
				"insert into duoshouji.note_keyword values(?,?,?)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setBigDecimal(1, BigDecimal.valueOf(noteId));
						ps.setString(2, noteAttributes.getProductName());
						ps.setInt(3, 1);
					}
			
		});
		mysqlDataSource.update("update duoshouji.user_extend set note_number = note_number + 1 where user_id = " + mobileNumber);
		mysqlDataSource.update("insert into duoshouji.note_extend (note_id) values ("+noteId+")");
		return noteId;
	}
	
	private String buildInsertNoteClause(int tagCount) {
		StringBuilder sqlBuilder = new StringBuilder(
				"insert into duoshouji.note (note_id, category_id, brand_id, product_name, price, district_id, longitude, latitude, title, content, create_time, user_id, last_update_time, rating, keyword");
		for (int i = 0; i < tagCount; ++i) {
			sqlBuilder.append(", tag_id");
			sqlBuilder.append(i + 1);
		}
		sqlBuilder.append(") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
		for (int i = 0; i < tagCount; ++i) {
			sqlBuilder.append(",?");
		}
		sqlBuilder.append(")");
		return sqlBuilder.toString();
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
	public void saveNoteImages(final long noteId, final Image[] noteImages) {
		mysqlDataSource.update(
				"update duoshouji.note set main_image_url = ?, main_image_width = ?, main_image_height = ? where note_id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, noteImages[0].getUrl());
						ps.setInt(2, noteImages[0].getWidth());
						ps.setInt(3, noteImages[0].getHeight());
						ps.setLong(4, noteId);
					}
				});
		if (noteImages.length > 1) {
			mysqlDataSource.batchUpdate("insert into duoshouji.note_image values (?,?,?,?)"
					, new BatchPreparedStatementSetter() {
						
				@Override
				public void setValues(PreparedStatement ps, int i)
						throws SQLException {
					ps.setLong(1, noteId);
					ps.setString(2, noteImages[i + 1].getUrl());
					ps.setInt(3, noteImages[i + 1].getWidth());
					ps.setInt(4, noteImages[i + 1].getHeight());
				}
	
				@Override
				public int getBatchSize() {
					return noteImages.length - 1;
				}
				
			});
		}
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
	public void insertFollowConnection(MobileNumber follerId, MobileNumber[] followedIds) {
		insertFollowConnection(follerId, followedIds, true);
	}
	
	@Override
	public void insertFollowConnection(final MobileNumber followerId, final MobileNumber[] followedIds, final boolean isActivated) {
		mysqlDataSource.batchUpdate(
				"insert into duoshouji.follow values(?,?,?,?)"
				, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ps.setLong(1, followedIds[i].toLong());
				ps.setLong(2, followerId.toLong());
				ps.setLong(3, System.currentTimeMillis());
				ps.setBoolean(4, isActivated);
			}

			@Override
			public int getBatchSize() {
				return followedIds.length;
			}
			
		});
	}

	@Override
	public void activateFollows(MobileNumber userId) {
		final int fanCount = mysqlDataSource.update("update duoshouji.follow set is_activated = 1 where user_id = " + userId);
		mysqlDataSource.update("update duoshouji.user_extend set followed_number = " + fanCount + " where user_id = " + userId);
		mysqlDataSource.update("update duoshouji.user_extend set follow_number = follow_number + 1 where user_id in (select fan_user_id from duoshouji.follow where user_id = " + userId + ")");
	}

	@Override
	public List<MobileNumber> findFollowerIds(final MobileNumber followedId) {
		return mysqlDataSource.query(
				"select fan_user_id from duoshouji.follow where user_id = " + followedId
				, new RowMapper<MobileNumber>() {

			@Override
			public MobileNumber mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				return MobileNumber.valueOf(rs.getLong("fan_user_id"));
			}
			
		});
	}
}
