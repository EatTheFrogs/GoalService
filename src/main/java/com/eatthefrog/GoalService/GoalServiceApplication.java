package com.eatthefrog.GoalService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan("com.eatthefrog.GoalService.model")
public class GoalServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoalServiceApplication.class, args);
	}

}
