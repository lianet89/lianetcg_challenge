package com.example.LianetCG_challenge.service;

import java.io.IOException;
import java.util.ArrayList;
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

	public JsonNode getResourceARecord() {
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
		log.info("Sending record to sink A");
		String sinkAUrl = "http://localhost:7299/sink/a";
		RestTemplate restTemplate = new RestTemplate();
		
		HttpEntity<SinkADto> request = new HttpEntity<>(new SinkADto(kind, id));		
		ResponseEntity<SinkADto> response = restTemplate.exchange(sinkAUrl, HttpMethod.POST, request, SinkADto.class);				
		return response.getStatusCode().toString();
	}

	
	  public String categorize() throws IOException {  
		  log.info("Categorizing records");
		  boolean found = false; 
		  String responseStatus = "";
		  		  
		  JsonNode sourceARecord = getResourceARecord();
		  JsonNode idRecordA = sourceARecord.path("id");	
		  log.info(idRecordA.asText());
		  
		  JsonNode sourceBRecord = getResourceBRecord();
		  JsonNode idRecordB = sourceBRecord.path("id").path("value");
		  log.info(idRecordB.asText());
		  			  
			/*
			 * Si sourceARecord y sourceBRecord tienen el mismo ID son iguales, entonces se
			 * envia a SinkA con tipo JOINED
			 */
			  if(idRecordA.equals(idRecordB)) { 				  
				  responseStatus = postSinkA(Kind.JOINED, idRecordA); 
				  log.info("Category: " + Kind.JOINED.toString());
			  }			  
			  /*Si sourceARecord y sourceBRecord no tienen el mismo ID son diferentes,
			  entonces busca el sourceARecord en la listaB. Si encuentra uno con el mismo
			  ID, entonces se envian a SinkA con tipo JOINED y se elimina de la listaB*/
			  
			  if(!idRecordA.equals(idRecordB)) { 
				  for(int i = 0; i < listB.size(); i++) { 
					  if(idRecordA.equals(listB.get(i).path("id").path("value"))) {		
						  responseStatus = postSinkA (Kind.JOINED, idRecordA);
						  log.info("Category: " + Kind.JOINED.toString());
						  found = true; 						  
						  listB.remove(i);			
						  break; 
					  } 
				  }			  
			  /*Si no encontro un record igual en la listaB, entonces busca el sourceBRecord
			  en la listaA. Si encuentra uno con el mismo ID, entonces se envian a SinkA
			  con tipo JOINED y se elimina de la listaA*/
			  
				  if(found == false) { 
					  listA.add(sourceARecord); 
					  for(int i = 0; i < listA.size(); i++) { 
						  if(idRecordB.equals(listA.get(i).path("id"))) {
							  responseStatus = postSinkA (Kind.JOINED, idRecordB); 
							  log.info("Category: " +  Kind.JOINED.toString());
							  found = true;
							  listA.remove(i); 
							  break; 
						  } 
					  }
				  
				  /*Si tampoco encontro un record igual en la listaA, entonces se envian a SinkA
				  con tipo ORPHANED y se elimina de la listaA*/
				  
					  if(found == false) { 
						  responseStatus = postSinkA (Kind.ORPHANED, idRecordB);
						  log.info("Category: " + Kind.ORPHANED.toString());
						  listB.remove(sourceBRecord); 
					  }			  
				  }
			  
			  } 
			  /*TODO: Verificar cuando un registro es DEFECTIVE e implementar el
			  comportamiento para ese caso*/
			 return responseStatus;
	  }
	 

}
