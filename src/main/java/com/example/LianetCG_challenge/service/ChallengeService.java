package com.example.LianetCG_challenge.service;

import java.util.ArrayList;
import java.util.List;


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


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChallengeService {
	
	List <SourceARecordDto> listA = new ArrayList<SourceARecordDto>();
	List <SourceBRecordDto> listB = new ArrayList<SourceBRecordDto>();
	
	public SourceARecordDto getResourceARecord () {
		log.info("Fetching record from source A");
		String resourceAUrl = "http://localhost:7299/resource/a";			
		RestTemplate restTemplate = new RestTemplate();
		SourceARecordDto sourceADto = restTemplate.getForObject(resourceAUrl, SourceARecordDto.class);
		return sourceADto;			
	}
	
	public SourceBRecordDto getResourceBRecord () {
		//TODO: Verify how XML formatted records are obtained using RestTemplate
		log.info("Fetching record from source B");
		String resourceBUrl = "http://localhost:7299/resource/b";			
		RestTemplate restTemplate = new RestTemplate();
		SourceBRecordDto sourceBDto = restTemplate.getForObject(resourceBUrl, SourceBRecordDto.class);
		return sourceBDto;			
	}
	
	public String postSinkA (Kind kind, String id) {
		log.info("Sending record to sink A");
		String sinkAUrl = "http://localhost:7299/sink/a";		
		RestTemplate restTemplate = new RestTemplate();			
		HttpEntity<SinkADto> request = new HttpEntity<>(new SinkADto(kind, id));
		//SinkADto sinkADto = restTemplate.postForObject(sinkAUrl, request, SinkADto.class);
		ResponseEntity<SinkADto> response = restTemplate.exchange(sinkAUrl, HttpMethod.POST, request, SinkADto.class);
		return response.getStatusCode().toString();		 
	}
	
	public void categorize () {
		log.info("Categorizing records");
		boolean found = false;
		SourceARecordDto sourceARecord = getResourceARecord();
		SourceBRecordDto sourceBRecord = getResourceBRecord();
		
		//Si sourceARecord y sourceBRecord tienen el mismo ID son iguales, entonces se envia a SinkA con tipo JOINED
		if(sourceARecord.id().equals(sourceBRecord.getId())) {
			postSinkA (Kind.JOINED, sourceARecord.id());			
		}
		/*
		 * Si sourceARecord y sourceBRecord no tienen el mismo ID son diferentes, entonces busca el sourceARecord en la
		 * listaB. Si encuentra uno con el mismo ID, entonces se envian a SinkA con tipo
		 * JOINED y se elimina de la listaB
		 */
		if(!sourceARecord.id().equals(sourceBRecord.getId())) {
			for(int i = 0; i < listB.size(); i++) {
				 if(sourceARecord.id().equals(listB.get(i).getId())) {
					 postSinkA (Kind.JOINED, sourceARecord.id());
					 found = true;
					 listB.remove(i);
					 break;
				 }
			}
			/*
			 * Si no encontro un record igual en la listaB, entonces busca el sourceBRecord en la
			 * listaA. Si encuentra uno con el mismo ID, entonces se envian a SinkA con tipo
			 * JOINED y se elimina de la listaA
			 */
			if(found == false) {
				listA.add(sourceARecord);
				for(int i = 0; i < listA.size(); i++) {
					 if(sourceBRecord.getId().equals(listA.get(i).id())) {
						 postSinkA (Kind.JOINED, sourceBRecord.getId());
						 found = true;
						 listA.remove(i);
						 break;
					 }
				}
				/*
				 * Si tampoco encontro un record igual en la listaA, entonces se envian a SinkA con tipo
				 * ORPHANED y se elimina de la listaA
				 */
				if(found == false) {
					postSinkA (Kind.ORPHANED, sourceBRecord.getId());
					listB.remove(getResourceBRecord());
				}
				
			}
			
		}
		//TODO: Verificar cuando un registro es DEFECTIVE e implementar el comportamiento para ese caso
	}

}
