package com.duoshouji.server.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.duoshouji.server.db.CategoryDto;
import com.google.common.collect.Maps;

public class CategoryDao extends BaseDao {

	public List<CategoryDto> findCategories () {
		String sql = "select * from catagory";

		return jdbcTemplate.query(sql,
			BeanPropertyRowMapper.newInstance(CategoryDto.class));
	}
	
	public CategoryDto findCategoryById (Long id) {
		String sql = "select * from catagory where id = :id";
	
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("id", id);
	
		List<CategoryDto> cats = namedParameterJdbcTemplate.query(sql, params,
			BeanPropertyRowMapper.newInstance(CategoryDto.class));
		
		if (cats != null && cats.size() > 0) {
			return cats.get(0);
		}

		return null;
	}
	
	public List<CategoryDto> findCategoriesByTagId (Long tagId) {
		String sql = "select cat.id, cat.name from catagory cat, category_tag ct, tag"
					+ " where ct.category_id = cat.id"
					+ "   and ct.tag_id = tag.id"
					+ "   and ct.tag_id = :tagId";
		
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("tagId", tagId);
		
		return namedParameterJdbcTemplate.query(sql, params,
			BeanPropertyRowMapper.newInstance(CategoryDto.class));
	}
}
