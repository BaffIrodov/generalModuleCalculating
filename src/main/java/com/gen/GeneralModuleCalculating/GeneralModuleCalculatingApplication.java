package com.gen.GeneralModuleCalculating;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
@EntityScan({"com.gen.GeneralModuleCalculating.entities", "com.querydsl.jpa.impl"})
public class GeneralModuleCalculatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneralModuleCalculatingApplication.class, args);

	}

	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
		return new JPAQueryFactory(entityManager);
	}

}
