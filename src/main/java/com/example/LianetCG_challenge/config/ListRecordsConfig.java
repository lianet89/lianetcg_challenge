package com.example.LianetCG_challenge.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

@Configuration
public class ListRecordsConfig {
	
	@Bean
	public List<JsonNode> sharedRecordsList(){
		return new ArrayList<JsonNode>();
	}

}
