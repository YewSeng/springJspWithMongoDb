package com.caltech.constants;

public enum Role {
	ADMIN("Admin Role"),
	DOCTOR("Doctor Role"),
	USER("User Role");

	private String customName;
	
	private Role(String customName) {
		this.customName = customName;
	}

	public String getCustomName() {
		return customName;
	}
}
