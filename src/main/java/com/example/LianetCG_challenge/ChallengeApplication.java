package com.example.LianetCG_challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.LianetCG_challenge.service.ChallengeService;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@Slf4j
public class ChallengeApplication implements CommandLineRunner{
	
	@Autowired
	private ChallengeService serviceClass;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		log.info("Starting system.");
		serviceClass.categorize();
		serviceClass.categorize();
		serviceClass.categorize();
		serviceClass.categorize();
		serviceClass.sendOrphanedToSink();
	}

}
