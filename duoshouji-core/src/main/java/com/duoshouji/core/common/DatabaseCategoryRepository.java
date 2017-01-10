package com.duoshouji.core.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.CategoryRepository;

@Service
public class DatabaseCategoryRepository extends CategoryRepository implements RowMapper<Category> {

	private List<Category> categories;
	
	public DatabaseCategoryRepository(JdbcTemplate jdbcTemplate) {
		categories = jdbcTemplate.query("select category_id, category_name from duoshouji.category order by category_id", this);
	}
	
	@Override
	public List<Category> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	@Override
	public Category findCategory(long categoryId) {
		for (Category category : categories) {
			if (category.getId() == categoryId) {
				return category;
			}
		}
		throw new IllegalArgumentException("Catetory Id: " + categoryId + " not found!");
	}

	@Override
	public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
		return createCategory(rs.getLong("category_id"), rs.getString("category_name"));
	}

}
