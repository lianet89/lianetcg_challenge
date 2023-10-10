package com.example.LianetCG_challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.config.Status;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
@Primary
@Slf4j
@Service
public class JsonResourceProcessor extends ResourceProcessor implements IResourceProcessor {
	
	
	@Override
	public JsonNode getResourceRecord() throws RestClientException {
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
	
	@Async
 	public void processRecords() throws Exception {
	  log.info("Processing SourceA 's records."); 
	  boolean statusDone = false;
	  
		  do {
			JsonNode sourceARecord = getResourceRecord();		
				try {
				  if (sourceARecord != null && !sourceARecord.isNull()) {
					  JsonNode idRecordA = sourceARecord.path("id");
					  JsonNode statusRecordA = sourceARecord.path("status");					  				  
					  if (!statusRecordA.asText().equalsIgnoreCase(Status.DONE.toString())) {
						  categorize(idRecordA);	
						  //listRecords.add(idRecordA);
						  
					  } else {
						  statusDone = true;
						  log.info("FINAL record from source A."); 
						  sendOrphanedToSink();
						
						  //categorize();
					  }
				  }
			  } catch (Exception e) {
				  log.error("Processor terminated with error:", e);
			  }	 
				
		  } while (!statusDone);
}

}
