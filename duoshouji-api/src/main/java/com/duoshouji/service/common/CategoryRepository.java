package com.duoshouji.service.common;

import java.util.List;

public abstract class CategoryRepository {
	
	public abstract List<Category> getCategories();
		
	public abstract Category findCategory(long categoryId);

	protected final Category createCategory(long categoryId, String categoryName) {
		return new Category(categoryId, categoryName);
	}
}
