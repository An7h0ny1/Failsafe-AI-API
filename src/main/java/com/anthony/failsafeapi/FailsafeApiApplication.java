package com.anthony.failsafeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class FailsafeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FailsafeApiApplication.class, args);
	}

}
