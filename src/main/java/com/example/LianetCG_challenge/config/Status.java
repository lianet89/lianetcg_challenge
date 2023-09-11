package com.example.LianetCG_challenge.config;

public enum Status {
	
	OK ("ok"), DONE ("done");
	
	public final String status;
	
	private Status(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	
}
