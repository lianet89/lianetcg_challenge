package com.example.LianetCG_challenge.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.LianetCG_challenge.service.IResourceProcessor;

@Configuration
public class ListProcessorsConfig {
	
	@Bean
	public List<IResourceProcessor> sharedProcessorsList(){
		return new ArrayList<IResourceProcessor>();
	}

}
