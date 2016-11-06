package com.duoshouji.core.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.core.common.DatabaseDistrictRepository.InnerDistrict;
import com.duoshouji.service.common.District;
import com.duoshouji.service.common.DistrictRepository;

@Service
public class DatabaseDistrictRepository extends DistrictRepository implements RowMapper<InnerDistrict> {

	private final List<District> districts = new ArrayList<District>();
	private final Collection<InnerDistrict> states = new LinkedList<InnerDistrict>();

	public DatabaseDistrictRepository(JdbcTemplate jdbcTemplate) {
		List<InnerDistrict> temp = jdbcTemplate.query(
				"select * from duoshouji.district where mod(district_id, 100000000) = 0 order by district_id", this);
		Iterator<InnerDistrict> ite = temp.iterator();
		while (ite.hasNext()) {
			InnerDistrict district = ite.next();
			if (district.isState()) {
				states.add(district);
				ite.remove();
			}
		}
		for (InnerDistrict district : temp) {
			districts.add(createDistrict(district.getIdentifier(), district.getName()));
		}
	}
	
	@Override
	public List<District> getDistricts() {
		return Collections.unmodifiableList(districts);
	}

	@Override
	public District findDistrict(long districtId) {
		for (District district : districts) {
			if (district.getId() == districtId) {
				return district;
			}
		}
		throw new IllegalArgumentException("District Id: " + districtId + " not found!");
	}

	@Override
	public InnerDistrict mapRow(ResultSet rs, int rowNum) throws SQLException {
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
	
	class InnerDistrict {
		private long districtId;
		private String districtName;

		private InnerDistrict(ResultSet rs) throws SQLException {
			super();
			this.districtId = rs.getLong("district_id");
			this.districtName = rs.getString("district_name");
		}

		public long getIdentifier() {
			return districtId;
		}

		public String getName() {
			String name = districtName;
			if (!isState()) {
				name = findState(this).getName() + "/" + name;
			}
			return name;
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
