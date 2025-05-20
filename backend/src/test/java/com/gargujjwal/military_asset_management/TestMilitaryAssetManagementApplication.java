package com.gargujjwal.military_asset_management;

import org.springframework.boot.SpringApplication;

public class TestMilitaryAssetManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(MilitaryAssetManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
