package com.caltech.constants;

public enum Status {

	AVAILABLE("Available Now"),
	BUSY("Busy Now");
	
	private String customName;
	
	private Status(String customName) {
		this.customName = customName;
	}

	public String getCustomName() {
		return customName;
	}
}
