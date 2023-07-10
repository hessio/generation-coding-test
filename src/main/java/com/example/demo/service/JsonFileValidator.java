package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileValidator {
	
	public boolean isValidFormat(String fileContent) {
	    // Perform your validation logic here
	    // Return true if the format is valid, false otherwise
		try {
	        // Create an ObjectMapper instance
	        ObjectMapper objectMapper = new ObjectMapper();
	
	        // Parse the JSON string into a JsonNode object
	        JsonNode jsonNode = objectMapper.readTree(fileContent);
	        
	        if (jsonNode.isObject() && jsonNode.has("valueX")
	        		&& jsonNode.has("valueY")
	        		&& jsonNode.size() == 2) {
	        	return true;
	           
	        } else {
	        	return false;
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
