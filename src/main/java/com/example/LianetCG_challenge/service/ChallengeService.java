package com.example.LianetCG_challenge.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.config.Kind;
import com.example.LianetCG_challenge.dto.SinkADto;
import com.example.LianetCG_challenge.dto.SourceARecordDto;
import com.example.LianetCG_challenge.dto.SourceBRecordDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChallengeService {

	List<JsonNode> listA = new ArrayList<JsonNode>();
	List<JsonNode> listB = new ArrayList<JsonNode>();
	List<JsonNode> listAux = new ArrayList<JsonNode>();

	public JsonNode getResourceARecord() throws Exception {
		log.info("Fetching record from source A");
		String resourceAUrl = "http://localhost:7299/source/a";
		RestTemplate restTemplate = new RestTemplate();
						
		ResponseEntity<JsonNode> response = restTemplate.getForEntity(resourceAUrl, JsonNode.class);		 
		log.info(response.toString());
		JsonNode node = response.getBody();		
		return node;
	}

	public JsonNode getResourceBRecord() throws IOException {
		log.info("Fetching record from source B");
		String resourceBUrl = "http://localhost:7299/source/b";
		RestTemplate restTemplate = new RestTemplate();
	
		ResponseEntity<String> response = restTemplate.getForEntity(resourceBUrl, String.class);
		log.info(response.toString());
		
		XmlMapper xmlMapper = new XmlMapper();
		JsonNode node = xmlMapper.readTree(response.getBody());				
		return node;
	}

	public String postSinkA(Kind kind, JsonNode id) {
		log.info("Sending record to sink A: " + id);
		String sinkAUrl = "http://localhost:7299/sink/a";
		RestTemplate restTemplate = new RestTemplate();
		
		HttpEntity<SinkADto> request = new HttpEntity<>(new SinkADto(kind, id));		
		ResponseEntity<SinkADto> response = restTemplate.exchange(sinkAUrl, HttpMethod.POST, request, SinkADto.class);				
		return response.getStatusCode().toString();
	}

	
	  public void categorize() throws Exception {  
		  log.info("Categorizing records");
		  boolean found = false; 
		  String responseStatus = "";
		  
		  try {
			  JsonNode sourceARecord = getResourceARecord();			  
			  JsonNode idRecordA = sourceARecord.path("id");			  
			  log.info(idRecordA.asText());
			  
			  JsonNode sourceBRecord = getResourceBRecord();
			  JsonNode idRecordB = sourceBRecord.path("id").path("value");
			  log.info(idRecordB.asText());
			  
			  if(idRecordA.equals(idRecordB)) { 
				  responseStatus = postSinkA(Kind.JOINED, idRecordA); 
				  log.info("Category: " + Kind.JOINED.toString());
			  } 				  
				  
			  if(!idRecordA.equals(idRecordB)) { 
				  if(listAux.contains(idRecordA)) {
					  responseStatus = postSinkA (Kind.JOINED, idRecordA);
					  log.info("Category: " + Kind.JOINED.toString());
					  found = true; 						  
					  listAux.remove(idRecordA);
				  } else {
					  listAux.add(idRecordA);
				  }
				  
				  if(listAux.contains(idRecordB)) {
					  responseStatus = postSinkA (Kind.JOINED, idRecordB);
					  log.info("Category: " + Kind.JOINED.toString());
					  found = true; 						  
					  listAux.remove(idRecordB);
				  } else {
					  listAux.add(idRecordB);
				  } 
							  
				  log.info("Auxiliar list: " + listAux.toString()); 
			  }
			  
		  }catch (Exception e) {
			  System.out.println("Incorrect format for record (DEFECTIVE).");
		  }
	  }
	  
	  public void sendOrphanedToSink() {
		  log.info("Sending ORPHANED records to sinkA: ");
		  String responseStatus = "";
			
		  for (JsonNode jsonNode : listAux) {
			  responseStatus = postSinkA (Kind.ORPHANED, jsonNode); 
			  log.info("Category: " + Kind.ORPHANED.toString());
			  log.info("Response status: " + responseStatus); 
		  }
	  }
	 

}
