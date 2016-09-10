package com.duoshouji.server.db;

import lombok.Data;

@Data
public class CountryDto {
	private Long id;
	private String name;
	private Integer countryCode;
}
