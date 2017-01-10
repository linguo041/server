package com.duoshouji.restapi.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.BrandRepository;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.CategoryRepository;
import com.duoshouji.service.common.District;
import com.duoshouji.service.common.DistrictRepository;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.common.TagRepository;

@RestController
public class CatalogResource {

	private TagRepository tagRepository;
	private BrandRepository brandRepository;
	private CategoryRepository categoryRepository;
	private DistrictRepository districtRepository;

	@Autowired
	@Required
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Autowired
	@Required
	public void setBrandRepository(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	@Autowired
	@Required
	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Autowired
	@Required
	public void setDistrictRepository(DistrictRepository districtRepository) {
		this.districtRepository = districtRepository;
	}

	@RequestMapping(path = "/common/channels", method = RequestMethod.GET)
	public List<Tag> getChannels() {
		return tagRepository.getChannels();
	}

	@RequestMapping(path = "/common/commodity/brands", method = RequestMethod.GET)
	public List<Brand> getBrands(
			@RequestParam(name="keyword", required=false) String keyword
			) {
		return brandRepository.getBrands(keyword);
	}
	
	@RequestMapping(path = "/common/commodity/categories", method = RequestMethod.GET)
	public List<Category> getCategories() {
		return new LinkedList<Category>(categoryRepository.getCategories());
	}
	
	@RequestMapping(path = "/common/geography/cities", method = RequestMethod.GET)
	public List<District> getCities() {
		return districtRepository.getDistricts();
	}
	
}
