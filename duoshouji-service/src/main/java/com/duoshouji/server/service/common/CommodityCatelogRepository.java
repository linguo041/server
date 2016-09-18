package com.duoshouji.server.service.common;

import java.util.List;

public interface CommodityCatelogRepository {
	
	List<Brand> listBrands();
	
	List<Category> listCategories();
	
	Brand getBrand(long brandId);
	
	Category getCategory(long categoryId);
}
