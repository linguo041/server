package com.duoshouji.server.service.common;

import java.util.List;

public interface CommodityCatelogRepository {
	
	List<Brand> listBrands();
	
	Brand getBrand(long brandId);
	
	List<Category> listCategories();
	
	Category getCategory(long categoryId);
	
	List<Product> findProducts(Brand brand, Category category);
}
