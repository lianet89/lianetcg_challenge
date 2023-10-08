package com.example.LianetCG_challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.LianetCG_challenge.config.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XmlResourceProcessor extends ResourceProcessor implements IResourceProcessor {
		
	
	@Override
	public JsonNode getResourceRecord() throws RestClientException {
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
	
	@Async
 	public void processRecords() throws Exception {
		log.info("Processing records."); 
		boolean statusDone = false;
		
		
			do {
				try {
					 JsonNode sourceBRecord = getResourceRecord(); 
					 if (sourceBRecord != null &&!sourceBRecord.isNull()) {
						 JsonNode idRecordB = sourceBRecord.path("id").path("value"); 
						 JsonNode statusRecordB = sourceBRecord.path("done");
						  
						 if (!statusRecordB.asText().equalsIgnoreCase(Status.DONE.toString())) {
							 categorize(idRecordB);
							 //listRecords.add(idRecordB);
							 
						 } else {
							 statusDone = true;
							 log.info("FINAL record from source B."); 
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
