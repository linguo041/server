package com.duoshouji.server.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.db.BrandDto;
import com.duoshouji.server.db.dao.BrandDao;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.note.Tag;

@RestController
public class CommonResource {

	private DuoShouJiFacade duoShouJiFacade;
	
	
	private BrandDao brandDao;

	@Autowired
	public CommonResource(DuoShouJiFacade duoShouJiFacade) {
		super();
		this.duoShouJiFacade = duoShouJiFacade;
	}
	
	@RequestMapping(path = "/common/tags", method = RequestMethod.GET)
	public List<Tag> getTags() {
		return duoShouJiFacade.getTags();
	}
	
	@RequestMapping(path = "/common/brands", method = RequestMethod.GET)
	public List<BrandDto> getBrands() {
		return brandDao.findBrands();
	}
}
