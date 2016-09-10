package com.duoshouji.server.db;

import lombok.Data;

@Data
public class CityDto {
	private Long id;
	private String name;
	private Long provinceId;
}
