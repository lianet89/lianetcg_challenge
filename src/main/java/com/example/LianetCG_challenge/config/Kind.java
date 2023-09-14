package com.example.LianetCG_challenge.config;

public enum Kind {
	
	JOINED ("joined"), ORPHANED ("orphaned"), DEFECTIVE ("defective");
	
	public final String kind;
	
	private Kind (String kind) {
		this.kind = kind;
	}

	public String getKind() {
		return kind;
	}

}
