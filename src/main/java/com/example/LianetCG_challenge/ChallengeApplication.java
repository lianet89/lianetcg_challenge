package com.example.LianetCG_challenge;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.LianetCG_challenge.service.IResourceProcessor;
import com.example.LianetCG_challenge.service.ResourceProcessor;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@Slf4j
public class ChallengeApplication implements CommandLineRunner{
	
	  @Autowired 
	  IResourceProcessor resourceProcessor;
	 
	
	@Autowired
	List<IResourceProcessor> listProcessors;
	
	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		log.info("Starting system.");
		
		
		for (IResourceProcessor processor : listProcessors) {
			processor.processRecords();
		}
		 
		log.info("***DONE***");
		 
		//listProcessors.get(0).processRecords();
		//listProcessors.get(1).processRecords();
		//resourceProcessor.categorize();
		//resourceProcessor.sendOrphanedToSink();
		
	}

}
