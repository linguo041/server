package com.duoshouji.server.service.common;

import java.util.List;

public interface DistrictRepository {

	List<District> listDistricts();
	
	District getDistrict(long districtId);
}
