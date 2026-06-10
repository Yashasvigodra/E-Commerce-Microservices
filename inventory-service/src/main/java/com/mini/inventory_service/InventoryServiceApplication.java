package com.mini.inventory_service;

import com.mini.inventory_service.model.Inventory;
import com.mini.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
//@EnableEurekaClient not needed as spring cloud starter netflix eureka client is on the classpath
public class
InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}




}
