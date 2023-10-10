package com.example.LianetCG_challenge.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.config.Kind;
import com.example.LianetCG_challenge.dto.SinkADto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SinkAProcessor {
	
	
	public String postSinkA(Kind kind, JsonNode id) throws JsonProcessingException {
		log.info("Sending record to sink A: " + id);
		String sinkAUrl = "http://localhost:7299/sink/a";
				
		ObjectMapper objectMapper = new ObjectMapper();		
		SinkADto sinkADto = new SinkADto(kind.toString(), id.toString());
		String json = objectMapper.writeValueAsString(sinkADto);
		
		HttpEntity<String> request = new HttpEntity<>(json);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<SinkADto> response = restTemplate.exchange(sinkAUrl, HttpMethod.POST, request, SinkADto.class);	
		
		return response.getStatusCode().toString();
	}

}
