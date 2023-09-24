package com.example.LianetCG_challenge.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.config.Kind;
import com.example.LianetCG_challenge.config.Status;
import com.example.LianetCG_challenge.dto.SinkADto;
import com.example.LianetCG_challenge.dto.SourceARecordDto;
import com.example.LianetCG_challenge.dto.SourceBRecordDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChallengeService {

	List<JsonNode> listAux = new ArrayList<JsonNode>();

	public JsonNode getResourceARecord() throws RestClientException {
		log.info("Fetching record from source A");
		String resourceAUrl = "http://localhost:7299/source/a";
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		
		try {
			ResponseEntity<JsonNode> response = restTemplate.getForEntity(resourceAUrl, JsonNode.class);		 
			log.info(response.toString());
			node = response.getBody();
		} catch (Exception e) {
			log.info("Found a DEFECTIVE record (ignored)");
		}
		return node;
	}

	public JsonNode getResourceBRecord() throws RestClientException {
		log.info("Fetching record from source B");
		String resourceBUrl = "http://localhost:7299/source/b";
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(resourceBUrl, String.class);
			log.info(response.toString());
			
			XmlMapper xmlMapper = new XmlMapper();
			node = xmlMapper.readTree(response.getBody());
			
		} catch (Exception e) {
			log.info("Found a DEFECTIVE record (ignored)");
		}						
		return node;
	}

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

	public void categorize(JsonNode id) throws Exception {
		  log.info("Categorizing records");
		  	   
			  if(listAux.contains(id)) { 
				  postSinkA (Kind.JOINED, id);
				  listAux.remove(id); 
				  log.info("Category: " + Kind.JOINED.toString()); 
			  } else { 
				  listAux.add(id); 
			  }
	  }
	 
	 public void sendOrphanedToSink() throws JsonProcessingException {
		  log.info("Sending ORPHANED records to sinkA: ");
		  String responseStatus = "";
			
		  for (JsonNode jsonNode : listAux) {
			  responseStatus = postSinkA (Kind.ORPHANED, jsonNode); 
			  log.info("Category: " + Kind.ORPHANED.toString());
			  log.info("Response status: " + responseStatus); 
		  }
	  }	  
		
	 	public void processRecordsA () throws Exception {
		  log.info("Processing SourceA 's records."); 
		  boolean statusDone = false;
		  //JsonNode statusRecordA = null;
		  
		  try {
			  do {
				  JsonNode sourceARecord = getResourceARecord();				  
				  if (!sourceARecord.isNull()) {
					  JsonNode idRecordA = sourceARecord.path("id");
					  JsonNode statusRecordA = sourceARecord.path("status");					  				  
					  if (!statusRecordA.asText().equalsIgnoreCase(Status.DONE.toString())) {
						  categorize(idRecordA);					  				
					  } else {
						  statusDone = true;
						  log.info("FINAL record from source A."); 
					  }
				  } 
			  } while (!statusDone);
			  
		  } catch (Exception e) {
			  //log.info(e.getMessage());
		  }
		  
		  		 
	}
	 	
	 	public void processRecordsB () throws Exception {
			log.info("Processing records."); 
			boolean statusDone = false;
			
			try {
				do {
					  JsonNode sourceBRecord = getResourceBRecord(); 
					  if (!sourceBRecord.isNull()) {
						  JsonNode idRecordB = sourceBRecord.path("id").path("value"); 
						  JsonNode statusRecordB = sourceBRecord.path("done");
						  
						  if (!statusRecordB.asText().equalsIgnoreCase(Status.DONE.toString())) {
							  categorize(idRecordB);				
						  } else {
							  statusDone = true;
							  log.info("FINAL record from source B."); 
						  }
						  
					  }					  
				  } while (!statusDone);
			} catch (Exception e) {
				//log.info(e.getMessage());
			}
				
				 
	 	}
}
