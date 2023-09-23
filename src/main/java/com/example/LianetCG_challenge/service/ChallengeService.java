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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChallengeService {

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
		  
		  try { 
			  if(listAux.contains(id)) { 
				  postSinkA (Kind.JOINED, id);
				  listAux.remove(id); 
				  log.info("Category: " + Kind.JOINED.toString()); 
			  } else {
				  listAux.add(id); 
			  }  
		  }catch (Exception e) {
			  System.out.println("Invalid format for record (DEFECTIVE).");
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
		
	 	public void processRecords() throws Exception {
		  log.info("Processing records."); 
		  
		  JsonNode sourceARecord = getResourceARecord(); 
		  JsonNode idRecordA = sourceARecord.path("id");
		  categorize(idRecordA); 
			  		  
		  JsonNode sourceBRecord = getResourceBRecord(); 
		  JsonNode idRecordB = sourceBRecord.path("id").path("value"); 
		  categorize(idRecordB);
		  
		}
}
