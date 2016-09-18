package com.duoshouji.server.internal.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.CatalogItem;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.CommodityCatelogRepository;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.common.TagRepository;

@Service
public class DatabaseCatalogRepository implements CommodityCatelogRepository, TagRepository {
	
	private List<Tag> tags;
	private List<Category> categories;
	private List<Brand> brands;
	private List<Mapping> mapping;
	
	private static final RowMapper<Tag> tagLoader = new RowMapper<Tag>() {
				
		@Override
		public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerTag(rs);
		}
	};
	
	private static final RowMapper<Category> categoryLoader = new RowMapper<Category>() {
		
		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerCategory(rs);
		}
		
	};
	
	private static final RowMapper<Brand> brandLoader = new RowMapper<Brand>() {
		
		@Override
		public Brand mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new InnerBrand(rs);
		}
		
	};
	
	private static final RowMapper<Mapping> mappingLoader = new RowMapper<Mapping>() {

		@Override
		public Mapping mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Mapping(rs);
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
		final boolean isChannel;
		
		private InnerTag(ResultSet rs) throws SQLException {
			super(rs.getLong("tag_id"), rs.getString("tag_name"));
			isChannel = rs.getBoolean("is_channel");
		}
	}
	
	private static class InnerCategory extends BasicCatalogItem implements Category {
		
		private InnerCategory(ResultSet rs) throws SQLException {
			super(rs.getLong("category_id"), rs.getString("category_name"));
		}
	}
	
	private static class InnerBrand extends BasicCatalogItem implements Brand {
		
		private InnerBrand(ResultSet rs) throws SQLException {
			super(rs.getLong("brand_id"), rs.getString("brand_name"));
		}
	}
	
	private static class Mapping {
		long categoryId, brandId, tagId;

		public Mapping(ResultSet rs) throws SQLException {
			super();
			this.categoryId = rs.getLong("category_id");
			this.brandId = rs.getLong("brand_id");
			this.tagId = rs.getLong("tag_id");
		}
	}
	
	@Autowired
	public DatabaseCatalogRepository(JdbcTemplate jdbcTemplate) {
		init(jdbcTemplate);
		setUnmodifiable();
	}
	
	private void init(JdbcTemplate jdbcTemplate) {
		tags = jdbcTemplate.query("select tag_id, tag_name, is_channel from duoshouji.tag order by tag_id", tagLoader);
		categories = jdbcTemplate.query("select category_id, category_name from duoshouji.category order by category_id", categoryLoader);
		brands = jdbcTemplate.query("select brand_id, brand_name from duoshouji.brand order by brand_id", brandLoader);
		mapping = jdbcTemplate.query("select * from duoshouji.catalog_tag_map", mappingLoader);
	}
	
	private void setUnmodifiable() {
		tags = Collections.unmodifiableList(tags);
		categories = Collections.unmodifiableList(categories);
		brands = Collections.unmodifiableList(brands);
		mapping = Collections.unmodifiableList(mapping);
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
	public List<Tag> listChannels() {
		List<Tag> channels = new LinkedList<Tag>();
		for (Tag tag : tags) {
			if (((InnerTag)tag).isChannel) {
				channels.add(tag);
			}
		}
		return channels;
	}
	
	@Override
	public List<Tag> listTags(Category category, Brand brand) {
		List<Tag> tags = new LinkedList<Tag>();
		for (Mapping m : mapping) {
			if (m.categoryId == category.getIdentifier() && m.brandId == brand.getIdentifier()) {
				tags.add(findTag(m.tagId));
			}
		}
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

	@Override
	public List<Tag> findTags(long[] tagIds) {
		List<Tag> tags = new ArrayList<Tag>(tagIds.length);
		for (long tagId : tagIds) {
			tags.add(findTag(tagId));
		}
		return tags;
	}
}
