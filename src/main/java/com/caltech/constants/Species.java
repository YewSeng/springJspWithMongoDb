package com.caltech.constants;

public enum Species {

    BIRD("Pet Bird"),
    CAT("Pet Cat"),
    DOG("Pet Dog"),
    HAMSTER("Pet Hamster"),
    RABBIT("Pet Rabbit");
	
	private String customName;
	
	private Species(String customName) {
		this.customName = customName;
	}

	public String getCustomName() {
		return customName;
	}
}
