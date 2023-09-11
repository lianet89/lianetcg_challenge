package com.example.LianetCG_challenge.service;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.LianetCG_challenge.model.SourceBRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class SourceBService {
	
	public Mono<SourceBRecord> fetchFromSourceA() throws JsonMappingException, JsonProcessingException {
		log.info("Fetching record from source B");
		
		WebClient webClient = WebClient.builder()
				  .baseUrl("http://localhost:7299")
				  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) 
				  .build();
		
		Mono<SourceBRecord> sourceBRecord = webClient.method(HttpMethod.GET).uri("source/b").retrieve().bodyToMono(SourceBRecord.class);
		
		sourceBRecord.onErrorResume(WebClientResponseException.class, ex -> {
			if (ex.getStatusCode() == HttpStatusCode.valueOf(406)) {
				return Mono.error(new RuntimeException(ex.getMessage()));
			}
			else return Mono.error(ex);
		});
						
		return sourceBRecord;	
		
	}
	
	
	


}
