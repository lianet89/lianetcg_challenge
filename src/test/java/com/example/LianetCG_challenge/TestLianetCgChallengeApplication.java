package com.example.LianetCG_challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestLianetCgChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.from(LianetCgChallengeApplication::main).with(TestLianetCgChallengeApplication.class).run(args);
	}

}
