package com.example.LianetCG_challenge.model;

import com.example.LianetCG_challenge.config.Status;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "msg")
public class SourceBRecord {

	private String id;
	private String done;
	
	public SourceBRecord(String id, String done) {
		this.id = id;
		this.done = done;
	}

	public String getId() {
		return id;
	}
	
	@XmlElement
	public void setId(String id) {
		this.id = id;
	}

	public String getDone() {
		return done;
	}
	
	@XmlElement
	public void setDone(String done) {
		this.done = done;
	}
	
	public String getStatus() {
		if(!getDone().isEmpty()) {
			return Status.DONE.status;			
		} else 
			return Status.OK.status;
	}

}
