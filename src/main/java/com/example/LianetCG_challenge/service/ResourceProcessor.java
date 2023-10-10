package com.example.LianetCG_challenge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.example.LianetCG_challenge.config.Kind;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class ResourceProcessor implements IResourceProcessor{
	@Autowired
	List<JsonNode> listRecords;
	
	@Autowired
	SinkAProcessor sinkAProcessor;	
		
	public abstract JsonNode getResourceRecord() throws RestClientException;
	
	//public abstract void processRecords() throws Exception;
	
	
	  @Async 
	  public void categorize(JsonNode id) throws Exception {
		  log.info("Categorizing records"); 
		  if(listRecords.contains(id)) {
			  sinkAProcessor.postSinkA(Kind.JOINED, id); 
			  listRecords.remove(id);
			  log.info("Category: " + Kind.JOINED.toString()); 
			  //return true;
		  } else {
			  listRecords.add(id); 
			  //return false;
		  } 
	  }
		 
	
	
	/*
	 * public void categorize() throws Exception { log.info("Categorizing records");
	 * 
	 * JsonNode recordId;
	 * 
	 * for(int i = 0; i < listRecords.size(); i++) { recordId =
	 * listRecords.remove(i); if(listRecords.remove(recordId)) {
	 * sinkAProcessor.postSinkA(Kind.JOINED, recordId); log.info("Category: " +
	 * Kind.JOINED.toString()); } } sendOrphanedToSink(); }
	 */
	
	
	/*
	 * public void sendOrphanedToSink() throws JsonProcessingException {
	 * log.info("Sending ORPHANED records to sinkA: "); String responseStatus = "";
	 * 
	 * for (JsonNode jsonNode : listRecords) { responseStatus =
	 * sinkAProcessor.postSinkA(Kind.ORPHANED, jsonNode); log.info("Category: " +
	 * Kind.ORPHANED.toString()); log.info("Response status: " + responseStatus); }
	 * }
	 */
	  @Async
	public void sendOrphanedToSink() throws JsonProcessingException {
		  log.info("Sending ORPHANED records to sinkA: ");
		  String responseStatus = "";
			
		  for (JsonNode recordId : listRecords) {
				/*
				 * JsonNode id = recordId; listRecords.remove(recordId);
				 */
				/*
				 * if(listRecords.remove(id)) { sinkAProcessor.postSinkA(Kind.JOINED, id);
				 * log.info("Category: " + Kind.JOINED.toString()); } else { responseStatus =
				 * sinkAProcessor.postSinkA(Kind.ORPHANED, id); log.info("Category: " +
				 * Kind.ORPHANED.toString()); log.info("Response status: " + responseStatus); }
				 */
			  
			  responseStatus = sinkAProcessor.postSinkA(Kind.ORPHANED, recordId); 
			  log.info("Category: " + Kind.ORPHANED.toString());
			  log.info("Response status: " + responseStatus); 
			
		  }
	  }

}
