package com.duoshouji.server.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.CommodityCatelogRepository;
import com.duoshouji.server.service.common.District;
import com.duoshouji.server.service.common.DistrictRepository;
import com.duoshouji.server.service.common.Product;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.common.TagRepository;

@RestController
public class CommonResource {

	private TagRepository tagRepository;
	private CommodityCatelogRepository commodityCatelogRepository;
	private DistrictRepository districtRepository;

	@Autowired
	public CommonResource(TagRepository tagRepository,
			CommodityCatelogRepository commodityCatelogRepository,
			DistrictRepository districtRepository) {
		super();
		this.tagRepository = tagRepository;
		this.commodityCatelogRepository = commodityCatelogRepository;
		this.districtRepository = districtRepository;
	}

	@RequestMapping(path = "/common/tags", method = RequestMethod.GET)
	public List<Tag> getTags() {
		return tagRepository.listTags();
	}

	@RequestMapping(path = "/common/channels", method = RequestMethod.GET)
	public List<Tag> getChannels() {
		return tagRepository.listChannels();
	}

	@RequestMapping(path = "/common/commodity/brands", method = RequestMethod.GET)
	public List<Brand> getBrands() {
		return commodityCatelogRepository.listBrands();
	}
	
	@RequestMapping(path = "/common/commodity/categories", method = RequestMethod.GET)
	public List<Category> getCategories() {
		return commodityCatelogRepository.listCategories();
	}

	@RequestMapping(path = "/common/commodity/products", method = RequestMethod.GET)
	public List<Product> getProducts(
			@RequestParam("categoryId") long categoryId,
			@RequestParam("brandId") long brandId
			) {
		final Brand brand = commodityCatelogRepository.getBrand(brandId);
		final Category category = commodityCatelogRepository.getCategory(categoryId);
		return commodityCatelogRepository.findProducts(brand, category);
	}
	
	@RequestMapping(path = "/common/geography/cities", method = RequestMethod.GET)
	public List<District> getCities() {
		return districtRepository.listDistricts();
	}
}
