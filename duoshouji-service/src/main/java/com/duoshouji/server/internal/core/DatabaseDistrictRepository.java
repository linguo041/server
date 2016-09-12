package com.duoshouji.server.internal.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.duoshouji.server.service.common.District;
import com.duoshouji.server.service.common.DistrictRepository;

public class DatabaseDistrictRepository implements DistrictRepository, RowMapper<District> {

	private final List<District> districts;
	private final Collection<InnerDistrict> states = new LinkedList<InnerDistrict>();

	public DatabaseDistrictRepository(JdbcTemplate jdbcTemplate) {
		List<District> temp = jdbcTemplate.query(
				"select * from duoshouji.district where mod(district_id, 100000000) = 0 order by district_id", this);
		Iterator<District> ite = temp.iterator();
		while (ite.hasNext()) {
			InnerDistrict district = (InnerDistrict) ite.next();
			if (district.isState()) {
				states.add(district);
				ite.remove();
			}
		}
		districts = Collections.unmodifiableList(temp);
	}
	
	@Override
	public List<District> listDistricts() {
		return districts;
	}

	@Override
	public District getDistrict(long districtId) {
		for (District district : districts) {
			if (district.getIdentifier() == districtId) {
				return district;
			}
		}
		throw new IllegalArgumentException("District Id: " + districtId + " not found!");
	}

	@Override
	public District mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new InnerDistrict(rs);
	}

	private InnerDistrict findState(InnerDistrict district) {
		InnerDistrict found = null;
		for (InnerDistrict state : states) {
			if (district.belongTo(state)) {
				found = state;
				break;
			}
		}
		return found;
	}
	
	private class InnerDistrict implements District {
		private long districtId;
		private String districtName;

		private InnerDistrict(ResultSet rs) throws SQLException {
			super();
			this.districtId = rs.getLong("district_id");
			this.districtName = rs.getString("district_name");
		}

		@Override
		public long getIdentifier() {
			return districtId;
		}

		@Override
		public String getName() {
			return findState(this).getName() + "/" + districtName;
		}
		
		private boolean isState() {
			return getIdentifier() % 10000000000l == 0;
		}
		
		private boolean belongTo(InnerDistrict district) {
			long d = 1;
			long n = district.getIdentifier();
			while (n % (d * 10) == 0) {
				d = d * 10;
			}
			return (getIdentifier() / d) == (district.getIdentifier() / d);
		}
	}

}
