package com.duoshouji.service.common;

import java.util.List;


public abstract class DistrictRepository {

	public abstract List<District> getDistricts();
	
	public abstract District findDistrict(long districtId);
	
	protected final District createDistrict(long districtId, String districtName) {
		return new District(districtId, districtName);
	}

}
