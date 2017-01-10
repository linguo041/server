package com.duoshouji.service.common;

import java.util.List;

public abstract class BrandRepository {
	
	public abstract List<Brand> getBrands();
	
	public abstract List<Brand> getBrands(String keyword);

	public abstract Brand findBrand(long brandId);

	protected final Brand createBrand(long brandId, String brandName) {
		return new Brand(brandId, brandName);
	}
}
