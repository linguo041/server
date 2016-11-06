package com.duoshouji.core.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.core.NoteDao;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.core.UserDao;
import com.duoshouji.core.dao.dto.BasicNoteDto;
import com.duoshouji.core.dao.dto.BasicUserDto;
import com.duoshouji.core.dao.dto.NoteCommentDto;
import com.duoshouji.core.dao.dto.NoteDetailDto;
import com.duoshouji.core.dao.dto.UserDto;
import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

@Service
public class MysqlUserNoteDao implements UserDao, NoteDao {

	private static final long BASE_NOTE_ID = 1000000000000l;
	private JdbcTemplate mysqlDataSource;

	@Autowired
	public MysqlUserNoteDao(JdbcTemplate mysqlDataSource) {
		this.mysqlDataSource = mysqlDataSource;
	}

	@Override
	public UserDto findUser(long userId) {
		return queryUser("select * from duoshouji.v_user where user_id = " + userId);
	}
	
	private UserDto queryUser(String sql) {
		return mysqlDataSource.query(sql
				, new ResultSetExtractor<UserDto>(){
					@Override
					public UserDto extractData(ResultSet rs)
						throws SQLException, DataAccessException {
						UserDto userDto = null;
						if (rs.next()) {
							userDto = new UserDto();
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
		basicUserDto.userId = rs.getLong("user_id");
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
		noteDto.productName = rs.getString("product_name");
		noteDto.price = rs.getBigDecimal("price");
		noteDto.districtId = rs.getLong("district_id");
		noteDto.categoryId = rs.getLong("category_id");
		noteDto.brandId = rs.getLong("brand_id");
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
		noteDto.authorId = rs.getLong("user_id");
	}
	
	@Override
	public List<BasicNoteDto> findNotes(long cutoff, IndexRange range, long authorId) {
		return findNotes(range, new PublishedNotesSqlQueryBuilder(cutoff, authorId));
	}
	
	@Override
	public List<BasicNoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter, long followerId) {
		return findNotes(range, new SquareNoteSqlQueryBuilder(cutoff, followerId, filter));
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
		private long authorId;

		public PublishedNotesSqlQueryBuilder(long cutoff, long authorId) {
			super(cutoff);
			this.authorId = authorId;
		}

		@Override
		void appendWhereConditions(StringBuilder sqlBuilder) {
			sqlBuilder.append(" and user_id = ?");
		}

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			super.setValues(ps);
			ps.setLong(2, authorId);
		}
		
	}
	
	private static class SquareNoteSqlQueryBuilder extends NoteListSqlQuery {
		private long followerId;
		private NoteFilter noteFilter;
		
		public SquareNoteSqlQueryBuilder(long cutoff, long followerId, NoteFilter noteFilter) {
			super(cutoff);
			this.followerId = followerId;
			this.noteFilter = noteFilter;
		}

		@Override
		void appendWhereConditions(StringBuilder sqlBuilder) {
			if (noteFilter != null) {
				if (noteFilter.isChannelSet()) {
				}
			}
			if (followerId != UserFacade.NULL_USER_ID) {
				sqlBuilder.append(" and user_id in (select user_id from duoshouji.follow where is_activated = 1 and fan_user_id = ?)");
			}
		}

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			super.setValues(ps);
			if (followerId != UserFacade.NULL_USER_ID) {
				ps.setLong(2, followerId);
			}
		}
	}
	
	@Override
	public void createUser(final long userId) {
		mysqlDataSource.update("insert into duoshouji.user (user_id) values(?)"
				, new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, userId);
					}
		});
		mysqlDataSource.update("insert into duoshouji.user_extend (user_id) values(?)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, userId);
					}
					
				});
	}

	@Override
	public void saveNickname(final long userId, final String nickname) {
		mysqlDataSource.update("update duoshouji.user set user_name = ? where user_id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, nickname);
						ps.setLong(2, userId);
					}
				});		
	}

	@Override
	public void saveGender(final long userId, final Gender gender) {
		mysqlDataSource.update("update duoshouji.user set gender = ? where user_id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, gender.toString());
						ps.setLong(2, userId);
					}
				});				
	}

	@Override
	public void savePasswordDigest(final long userId, final String passwordDigest) {
		mysqlDataSource.update("update duoshouji.user set password = ? where user_id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, passwordDigest);
						ps.setLong(2, userId);
					}
				});	
	}

	@Override
	public long createNote(final long userId, final NotePublishAttributes noteAttributes) {
		final long time = System.currentTimeMillis();
		final long noteId = BASE_NOTE_ID + mysqlDataSource.queryForObject("select count(*) from duoshouji.note", Integer.class);
		mysqlDataSource.update(buildInsertNoteClause()
				, new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setBigDecimal(1, BigDecimal.valueOf(noteId));
						if (noteAttributes.isCategorySet()) {
							ps.setBigDecimal(2, BigDecimal.valueOf(noteAttributes.getCategory().getId()));
						} else {
							ps.setNull(2, Types.DECIMAL);
						}
						if (noteAttributes.isBrandSet()) {
							ps.setBigDecimal(3, BigDecimal.valueOf(noteAttributes.getBrand().getId()));
						} else {
							ps.setNull(3, Types.DECIMAL);
						}
						if (noteAttributes.isProductNameSet()) {
							ps.setString(4, noteAttributes.getProductName());
						} else {
							ps.setNull(4, Types.VARCHAR);
						}
						if (noteAttributes.isPriceSet()) {
							ps.setBigDecimal(5, noteAttributes.getPrice());
						} else {
							ps.setNull(5, Types.DECIMAL);
						}
						if (noteAttributes.isDistrictSet()) {
							ps.setBigDecimal(6, BigDecimal.valueOf(noteAttributes.getDistrict().getId()));
						} else {
							ps.setNull(6, Types.DECIMAL);
						}
						ps.setString(7, noteAttributes.getTitle());
						ps.setString(8, noteAttributes.getContent());
						ps.setLong(9, time);
						ps.setBigDecimal(10, BigDecimal.valueOf(userId));
						ps.setLong(11, time);
						ps.setInt(12, noteAttributes.getRating());
						if (noteAttributes.isProductNameSet()) {
							ps.setString(13, noteAttributes.getProductName());
						} else {
							ps.setNull(13, Types.VARCHAR);
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
		mysqlDataSource.update("update duoshouji.user_extend set note_number = note_number + 1 where user_id = " + userId);
		mysqlDataSource.update("insert into duoshouji.note_extend (note_id) values ("+noteId+")");
		return noteId;
	}
	
	private String buildInsertNoteClause() {
		return "insert into duoshouji.note "
				+ "(note_id, category_id, brand_id, product_name, price, district_id, title, content, create_time, user_id, last_update_time, rating, keyword) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}

	@Override
	public void savePortrait(final long userId, final Image portrait) {
		mysqlDataSource.update(
				"update duoshouji.user set avatar_url = ?, avatar_width = ?, avatar_height = ? where user_id = ?"
				,new PreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, portrait.getUrl());
						ps.setInt(2, portrait.getWidth());
						ps.setInt(3, portrait.getHeight());
						ps.setLong(4, userId);
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
	public void createComment(final long noteId, final CommentPublishAttributes commentAttributes, final long userId) {
		mysqlDataSource.update(
				"insert into duoshouji.comment (note_id, user_id, content, created_time, rating) values (?,?,?,?,?)"
				, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, noteId);
				ps.setLong(2, userId);
				ps.setString(3, commentAttributes.getComment());
				ps.setLong(4, System.currentTimeMillis());
				ps.setInt(5, commentAttributes.getRating());
			}
			
		});
		mysqlDataSource.update("update duoshouji.note_extend set comment_number = comment_number + 1, rating_sum = rating_sum + ? where note_id = ?"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, commentAttributes.getRating());
						ps.setLong(2, noteId);
					}
					
				});
	}

	@Override
	public List<NoteCommentDto> getNoteComments(long noteId) {
		return mysqlDataSource.query(
				"select user_id, content from duoshouji.comment where note_id = " + noteId
				, new RowMapper<NoteCommentDto>() {

			@Override
			public NoteCommentDto mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				NoteCommentDto commentDto = new NoteCommentDto();
				commentDto.authorId = rs.getLong("user_id");
				commentDto.comment = rs.getString("content");
				return commentDto;
			}
			
		});
	}

	@Override
	public void saveUserLikeNote(final long noteId, final long userId) {
		mysqlDataSource.update("insert into duoshouji.likes (user_id, note_id) values (?,?)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, userId);
						ps.setLong(2, noteId);
					}
					
				});
		mysqlDataSource.update("update duoshouji.note_extend set like_number = like_number + 1 where note_id = " + noteId);
	}

	@Override
	public List<Long> findInviters(long inviteeId) {
		return mysqlDataSource.query(
				"select inviter_id from duoshouji.invitation where invited_mobile = " + inviteeId
				, new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return Long.valueOf(rs.getLong("inviter_id"));
					}
			
		});
	}

	@Override
	public void saveInvitation(final long inviterId, final MobileNumber inviteeMobileNumber) {
		mysqlDataSource.update(
				"insert into duoshouji.invitation values (?,?)"
				, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, inviterId);
				ps.setLong(2, inviteeMobileNumber.toLong());
			}
		
		});
	}
	
	@Override
	public void saveFollowConnection(final long followerId, final long followeeId) {
		mysqlDataSource.update(
				"insert into duoshouji.follow values(?,?,?,?)"
				, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, followeeId);
				ps.setLong(2, followerId);
				ps.setLong(3, System.currentTimeMillis());
				ps.setBoolean(4, true);				
			}
		});
		mysqlDataSource.update("update duoshouji.user_extend set followed_number = followed_number + 1 where user_id = " + followeeId);
		mysqlDataSource.update("update duoshouji.user_extend set follow_number = follow_number + 1 where user_id = " + followerId);
	}
}
