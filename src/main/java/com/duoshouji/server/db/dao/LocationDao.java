package com.duoshouji.server.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.duoshouji.server.db.CityDto;
import com.duoshouji.server.db.CountryDto;
import com.duoshouji.server.db.ProvinceDto;
import com.google.common.collect.Maps;

public class LocationDao extends BaseDao {
	
	public List<CountryDto> findCountries () {
		String sql = "select * from country";

		return jdbcTemplate.query(sql,
			BeanPropertyRowMapper.newInstance(CountryDto.class));
	}
	
	public List<ProvinceDto> findProvincesByCountryId (Long countryId) {
		String sql = "select * from province where country_id = :countryId";

		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("countryId", countryId);

		return namedParameterJdbcTemplate.query(sql, params,
			BeanPropertyRowMapper.newInstance(ProvinceDto.class));
	}
	
	public List<CityDto> findCitiesByProvinceId (Long provinceId) {
		String sql = "select * from city where province_id = :provinceId";

		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("provinceId", provinceId);

		return namedParameterJdbcTemplate.query(sql, params,
			BeanPropertyRowMapper.newInstance(CityDto.class));
	}
}
