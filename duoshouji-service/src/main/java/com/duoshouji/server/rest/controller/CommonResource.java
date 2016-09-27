package com.duoshouji.server.rest.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.CommodityCatelogRepository;
import com.duoshouji.server.service.common.District;
import com.duoshouji.server.service.common.DistrictRepository;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.common.TagRepository;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

@RestController
public class CommonResource {

	private UserRepository userRepository;
	private TagRepository tagRepository;
	private CommodityCatelogRepository commodityCatelogRepository;
	private DistrictRepository districtRepository;

	@Autowired
	@Required
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	@Required
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Autowired
	@Required
	public void setCommodityCatelogRepository(
			CommodityCatelogRepository commodityCatelogRepository) {
		this.commodityCatelogRepository = commodityCatelogRepository;
	}

	@Autowired
	@Required
	public void setDistrictRepository(DistrictRepository districtRepository) {
		this.districtRepository = districtRepository;
	}

	@RequestMapping(path = "/common/tags", method = RequestMethod.GET)
	public List<Tag> getTags(
			@RequestParam("categoryId") long categoryId,
			@RequestParam("brandId") long brandId
			) {
		final Category category = commodityCatelogRepository.getCategory(categoryId);
		final Brand brand = commodityCatelogRepository.getBrand(brandId);
		return tagRepository.listTags(category, brand);
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
		return new LinkedList<Category>(commodityCatelogRepository.listCategories());
	}
	
	@RequestMapping(path = "/common/geography/cities", method = RequestMethod.GET)
	public List<District> getCities() {
		return districtRepository.listDistricts();
	}
	
	@RequestMapping(path = "/accounts/check-exist", method = RequestMethod.GET)
	public AccountExistResult[] checkAccountsExist(
			@RequestParam("mobiles") MobileNumber[] mobileNumbers) {
		AccountExistResult[] results = new AccountExistResult[mobileNumbers.length];
		for (int i = 0; i < mobileNumbers.length; ++i) {
			final MobileNumber mobileNumber = mobileNumbers[i];
			results[i] = new AccountExistResult(mobileNumber, userRepository.isMobileNumberRegistered(mobileNumber));
		}
		return results;
	}
	
	public static class AccountExistResult {
		private MobileNumber mobileNumbers;
		private boolean isExists;
		
		private AccountExistResult(MobileNumber mobileNumbers, boolean isExists) {
			super();
			this.mobileNumbers = mobileNumbers;
			this.isExists = isExists;
		}
		
		public long getMobile() {
			return mobileNumbers.toLong();
		}
		
		public boolean isExists() {
			return isExists;
		}
	}
}
