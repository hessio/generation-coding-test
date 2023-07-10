package com.example.demo.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DemoApplicationService {
	
	private JsonFileValidator fileValidator = new JsonFileValidator();
	
	public ResponseEntity<String> postRequest(@RequestPart("file") MultipartFile file) throws IOException {
        
        try {
        	String fileName = file.getOriginalFilename();
        	
        	
        	byte[] fileBytes = file.getBytes();

            // Convert the byte array to a string
            String fileContent = new String(fileBytes);
        	
            if (!fileValidator.isValidFormat(fileContent)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                		.body("Invalid file format");
            }
        	
        	File newFile = new File(fileName);
        	if(newFile.exists()) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        				.body("File already exists.");
        	}
        	FileWriter fileWriter = new FileWriter(newFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            
            writer.write(fileContent);
            writer.close();
            
            
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
	
	public ResponseEntity<String> putRequest(@RequestPart("file") MultipartFile file){
		
		try{
			 
    		String fileName = file.getOriginalFilename();
    		File fileObj = new File(fileName);
    		if (!fileObj.exists()) {
    			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    					.body("File does not exist. Try running a PUT command to create the file");
    		}
    	
    		byte[] fileBytes = file.getBytes();
    		
	        // Convert the byte array to a string
	        String fileContent = new String(fileBytes);
	        
	        if (!fileValidator.isValidFormat(fileContent)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                		.body("Invalid file format");
            }
	        
	        File newFile = new File(fileName);
        	FileWriter fileWriter = new FileWriter(newFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            
            writer.write(fileContent);
            writer.close();
            
        
    	}catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }

        return ResponseEntity.ok("JSON file updated successfully");
		
	}
	
    public ResponseEntity<String> getRequest(@PathVariable String fileName) throws IOException, FileNotFoundException{
    	
        try {
        	
        	File file = new File(fileName);
        	
        	if (!file.exists()) {
        		return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File Doesn't Exist.");
            }

        	
        	Path filePath = Paths.get(fileName);
            String fileContent = new String(Files.readAllBytes(filePath));
        	
        	if (!fileValidator.isValidFormat(fileContent)) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                		.body("Invalid file format");
            }
        	ObjectMapper objectMapper = new ObjectMapper();
        	JsonNode jsonNode = objectMapper.readTree(file);

            // Access specific data from the JSON
            int valueX = jsonNode.get("valueX").asInt();
            int valueY = jsonNode.get("valueY").asInt();
            
            int result = valueX + valueY;
            
            ObjectNode objectNode = (ObjectNode) jsonNode;
            
            // Add a new value to the JSON
            objectNode.put("result", result);
            
            String output = objectMapper.writeValueAsString(objectNode);
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
            
            // Set the appropriate response headers
            return ResponseEntity.ok()
                    .body(output);
        }catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    public ResponseEntity<String> deleteRequest(@PathVariable String fileName) {
    	
    	fileName = "./" + fileName;
    	
        File file = new File(fileName);
        if(!file.exists()) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body("File doesn't exist.");
        }
        else if (file.delete()) {
            return ResponseEntity.ok("JSON file deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("An error occurred while deleting the JSON file");
        }
    }

}
