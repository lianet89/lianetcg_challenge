package com.example.LianetCG_challenge.service;

import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IResourceProcessor {
	
	//public abstract JsonNode getResourceRecord() throws RestClientException;
	
	public abstract void processRecords() throws Exception;
	
	
	public void categorize(JsonNode id) throws Exception;
	
	
	public void sendOrphanedToSink() throws JsonProcessingException;

}
