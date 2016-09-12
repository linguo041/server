package com.duoshouji.server.internal.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.CatalogItem;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.CommodityCatelogRepository;
import com.duoshouji.server.service.common.Product;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.common.TagRepository;

public class DatabaseCatalogRepository implements CommodityCatelogRepository, TagRepository {
	
	private List<Tag> tags;
	private List<Category> categories;
	private List<Brand> brands;
	private List<Product> products;
	
	private static final RowMapper<Tag> tagLoader = new RowMapper<Tag>() {
				
		@Override
		public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerTag(rs);
		}
		
	};
	
	private static final RowMapper<Category> categoryLoader = new RowMapper<Category>() {
		
		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (rs.getBoolean("is_tag")) {
				return new InnerCategory(rs);
			} else {
				return new InnerTagCategory(rs);
			}
		}
		
	};
	
	private static final RowMapper<Brand> brandLoader = new RowMapper<Brand>() {
		
		@Override
		public Brand mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerBrand(rs);
		}
		
	};
	
	private static final RowMapper<Product> productLoader = new RowMapper<Product>() {
		
		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerProduct(rs);
		}
		
	};
	
	private static class IdReference implements Comparable<IdReference> {
		long identifier;

		public IdReference(long identifier) {
			super();
			this.identifier = identifier;
		}

		@Override
		public int compareTo(IdReference o) {
			return Long.compare(identifier, o.identifier);
		}
	}
	
	private static class BasicCatalogItem extends IdReference implements CatalogItem {
		private String name;
		
		private BasicCatalogItem(long identifier, String name) {
			super(identifier);
			this.name = name;
		}
		
		@Override
		public long getIdentifier() {
			return identifier;
		}

		@Override
		public String getName() {
			return name;
		}
		
	}
	
	private static class InnerTag extends BasicCatalogItem implements Tag {
		private InnerTag(ResultSet rs) throws SQLException {
			super(rs.getLong("tag_id"), rs.getString("tag_name"));
			rs.getBoolean("is_channel");
		}
	}
	
	private static class InnerCategory extends BasicCatalogItem implements Category {
		
		private InnerCategory(ResultSet rs) throws SQLException {
			super(rs.getLong("category_id"), rs.getString("category_name"));
		}
	}
	
	private static class InnerTagCategory extends InnerCategory implements Tag {
		
		private InnerTagCategory(ResultSet rs) throws SQLException {
			super(rs);
		}
	}
	
	
	private static class InnerBrand extends BasicCatalogItem implements Brand {
		
		private InnerBrand(ResultSet rs) throws SQLException {
			super(rs.getLong("brand_id"), rs.getString("brand_name"));
		}
		
	}
	
	private static class InnerProduct extends BasicCatalogItem implements Product {
		private long categoryId;
		private long brandId;
		
		private InnerProduct(ResultSet rs) throws SQLException {
			super(rs.getLong("product_id"), rs.getString("product_name"));
			categoryId = rs.getLong("category_id");
			brandId = rs.getLong("brand_id");
		}
		
		boolean belongTo(Category category, Brand brand) {
			return category.getIdentifier() == categoryId && brand.getIdentifier() == brandId;
		}
	}
	
	@Autowired
	public DatabaseCatalogRepository(JdbcTemplate jdbcTemplate) {
		init(jdbcTemplate);
		setUnmodifiable();
	}
	
	private void init(JdbcTemplate jdbcTemplate) {
		tags = jdbcTemplate.query("select tag_id, tag_name, is_channel from duoshouji.tag order by tag_id", tagLoader);
		categories = jdbcTemplate.query("select category_id, category_name, is_tag from duoshouji.category order by category_id", categoryLoader);
		for (Category category : categories) {
			if (category instanceof InnerTagCategory) {
				tags.add((InnerTagCategory)category);
			}
		}
		brands = jdbcTemplate.query("select brand_id, brand_name from duoshouji.brand order by brand_id", brandLoader);
		products = jdbcTemplate.query("select * from duoshouji.product order by product_id", productLoader);
	}
	
	private void setUnmodifiable() {
		tags = Collections.unmodifiableList(tags);
		categories = Collections.unmodifiableList(categories);
		brands = Collections.unmodifiableList(brands);
		products = Collections.unmodifiableList(products);
	}
	
	@Override
	public List<Brand> listBrands() {
		return brands;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Brand getBrand(long brandId) {
		final int index = Collections.binarySearch((List)brands, new IdReference(brandId));
		if (index < 0) {
			throw new IllegalArgumentException("Brand Id: " + brandId + " not found!");
		}
		return brands.get(index);
	}

	@Override
	public List<Category> listCategories() {
		return categories;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Category getCategory(long categoryId) {
		final int index = Collections.binarySearch((List)categories, new IdReference(categoryId));
		if (index < 0) {
			throw new IllegalArgumentException("Category Id: " + categoryId + " not found!");
		}
		return categories.get(index);
	}

	@Override
	public List<Product> findProducts(Brand brand, Category category) {
		List<Product> filteredProducts = new LinkedList<Product>();
		for (Product product : products) {
			if (((InnerProduct)product).belongTo(category, brand)) {
				filteredProducts.add(product);
			}
		}
		return filteredProducts;
	}

	@Override
	public List<Tag> listChannels() {
		return tags;
	}
	
	@Override
	public List<Tag> listTags() {
		return tags;
	}
	
	@Override
	public Tag findTag(long tagId) {
		for (Tag tag : tags) {
			if (tag.getIdentifier() == tagId) {
				return tag;
			}
		}
		throw new IllegalArgumentException("Tag Id: " + tagId + " not found!");
	}
}
