package com.duoshouji.service.common;

import java.util.List;

public interface DistrictRepository {

	List<District> listDistricts();
	
	District getDistrict(long districtId);
}
