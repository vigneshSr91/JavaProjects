package com.sap.cc;

import com.sap.cc.users.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SpringBootUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootUsersApplication.class, args);
	}

	@Bean
	public List<User> getUsers(){
		return Arrays.asList(
				new User("J. Lennon", "+44 151 2526130", "2"),
				new User("G. Harrison", "+49 176 24202105", "3")
		);
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().build();
	}

}
