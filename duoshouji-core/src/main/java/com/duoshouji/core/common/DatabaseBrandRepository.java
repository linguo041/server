package com.duoshouji.core.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.BrandRepository;

@Service
public class DatabaseBrandRepository extends BrandRepository implements RowMapper<Brand> {
	private List<Brand> brands;
	
	public DatabaseBrandRepository(JdbcTemplate jdbcTemplate) {
		brands = jdbcTemplate.query("select brand_id, brand_name from duoshouji.brand order by brand_id", this);
	}
	
	@Override
	public List<Brand> getBrands() {
		return Collections.unmodifiableList(brands);
	}

	@Override
	public Brand findBrand(long brandId) {
		for (Brand brand : brands) {
			if (brand.getId() == brandId) {
				return brand;
			}
		}
		throw new IllegalArgumentException("Brand Id: " + brandId + " not found!");
	}

	@Override
	public Brand mapRow(ResultSet rs, int rowNum) throws SQLException {
		return createBrand(rs.getLong("brand_id"), rs.getString("brand_name"));
	}

}
