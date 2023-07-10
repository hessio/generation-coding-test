package com.example.demo.controller;

import com.example.demo.exceptions.InvalidFileException;

import com.example.demo.service.DemoApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class DemoApplicationController {
	
	private final DemoApplicationService service;
	
	public DemoApplicationController(DemoApplicationService service) {
		this.service = service;
	}

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJsonFile(@RequestPart("file") MultipartFile file) throws IOException {
        try {
        	
        	return service.postRequest(file);
        }catch(InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload JSON file");
        }
        
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateJsonFile(@RequestPart("file") MultipartFile file) throws IOException {
    	
    	try {
        	
        	return service.putRequest(file);
        }catch(InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<String> addResultJsonFile(@PathVariable String fileName) throws IOException, FileNotFoundException{
    	
    	try {
        	return service.getRequest(fileName);
        }catch(InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        
    }
    
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteJsonFile(@PathVariable String fileName) {
        return service.deleteRequest(fileName);
    }

}