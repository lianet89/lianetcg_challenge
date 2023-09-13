package com.example.LianetCG_challenge.service;

import org.modelmapper.internal.util.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.dto.SinkADto;
import com.example.LianetCG_challenge.dto.SourceARecordDto;
import com.example.LianetCG_challenge.dto.SourceBRecordDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChallengeService {
	
	
	String resourceBUrl = "http://localhost:7299/resource/b";
	String sinkAUrl = "http://localhost:7299/sink/a";
	
	public SourceARecordDto getResourceARecord() {
		log.info("Fetching record from source A");
		String resourceAUrl = "http://localhost:7299/resource/a";			
		RestTemplate restTemplate = new RestTemplate();
		SourceARecordDto sourceADto = restTemplate.getForObject(resourceAUrl, SourceARecordDto.class);
		return sourceADto;			
	}
	
	public SourceBRecordDto getResourceBRecord() {
		//TODO: Verify how XML formatted records are obtained using RestTemplate
		log.info("Fetching record from source B");
		String resourceBUrl = "http://localhost:7299/resource/b";			
		RestTemplate restTemplate = new RestTemplate();
		SourceBRecordDto sourceBDto = restTemplate.getForObject(resourceBUrl, SourceBRecordDto.class);
		return sourceBDto;			
	}
	
	public String postSinkA(String kind, String id) {
		log.info("Sending record to sink A");
		String sinkAUrl = "http://localhost:7299/sink/a";		
		RestTemplate restTemplate = new RestTemplate();			
		HttpEntity<SinkADto> request = new HttpEntity<>(new SinkADto(kind, id));
		//SinkADto sinkADto = restTemplate.postForObject(sinkAUrl, request, SinkADto.class);
		ResponseEntity<SinkADto> response = restTemplate.exchange(sinkAUrl, HttpMethod.POST, request, SinkADto.class);
		return response.getStatusCode().toString();		 
	}

}
