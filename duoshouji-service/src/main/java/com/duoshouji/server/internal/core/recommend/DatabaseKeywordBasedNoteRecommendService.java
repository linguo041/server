package com.duoshouji.server.internal.core.recommend;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.note.recommand.EcommerceItem;
import com.duoshouji.server.util.Image;

@Service
public class DatabaseKeywordBasedNoteRecommendService extends
		KeywordBasedNoteRecommendService {

	private JdbcTemplate mysqlDataSource;
	
	public DatabaseKeywordBasedNoteRecommendService(JdbcTemplate mysqlDataSource) {
		super();
		this.mysqlDataSource = mysqlDataSource;
	}

	@Override
	protected List<EcommerceItem> recommend(String keyword) {
		return mysqlDataSource.query(
				"select * from duoshouji.item where keyword = '" + keyword + "' order by item_order"
				, new RowMapper<EcommerceItem>() {

			@Override
			public EcommerceItem mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				return new InnerEcommerceItem(rs);
			}
			
		});
	}

	private static class InnerEcommerceItem implements EcommerceItem {
		final String provider;
		final String providerItemId;
		final String title;
		final BigDecimal price;
		final Image image;
		
		InnerEcommerceItem(ResultSet rs) throws SQLException {
			provider = rs.getString("source");
			providerItemId = rs.getString("item_id");
			title = rs.getString("item_title");
			price = rs.getBigDecimal("item_price");
			image = new Image(400, 400, rs.getString("pic_url"));
		}
		
		@Override
		public String getProvider() {
			return provider;
		}

		@Override
		public String getProviderItemId() {
			return providerItemId;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public BigDecimal getPrice() {
			return price;
		}

		@Override
		public Image getImage() {
			return image;
		}
		
	}
}
