package com.duoshouji.server.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import com.duoshouji.server.db.BrandDto;
import com.google.common.collect.Maps;

@Component
public class BrandDao extends BaseDao{

	public List <BrandDto> findBrands () {
		String sql = "select * from brand";

		return jdbcTemplate.query(sql,
				BeanPropertyRowMapper.newInstance(BrandDto.class));
	}
	
	public BrandDto findBrandById (Long id) {
		String sql = "select * from brand where id = :id";
		
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("id", id);
		
		List <BrandDto> brands = namedParameterJdbcTemplate.query(sql, params,
				BeanPropertyRowMapper.newInstance(BrandDto.class));
		
		if (brands != null && brands.size() > 0) {
			return brands.get(0);
		}
		
		return null;
	}
}
