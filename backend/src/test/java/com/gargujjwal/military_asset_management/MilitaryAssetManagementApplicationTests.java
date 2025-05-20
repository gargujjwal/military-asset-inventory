package com.gargujjwal.military_asset_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MilitaryAssetManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
