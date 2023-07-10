package com.example.demo;

import org.junit.Before;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controller.DemoApplicationController;
import com.example.demo.service.DemoApplicationService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.FileWriter;
import java.io.IOException;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class DemoServiceTest {

    private MockMvc mockMvc;

    // Setup method to initialize the MockMvc instance
    @BeforeEach
    void setup() {
    	DemoApplicationService demoApplicationService = new DemoApplicationService();
    	DemoApplicationController demoApplicationController = new DemoApplicationController(demoApplicationService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(demoApplicationController).build();
    }
    
    @Before
    void createFile(String name) {
    	String jsonData = "{ \"valueX\": 5, \"valueY\": 8 }";
        
        // File name
        String fileName = name;

        try {
            // Create FileWriter object with file name
            FileWriter fileWriter = new FileWriter(fileName);

            // Write JSON data to the file
            fileWriter.write(jsonData);

            // Flush and close the FileWriter
            fileWriter.flush();
            fileWriter.close();

            System.out.println("File created successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    @Test
    void testPostRequest() throws Exception {
        // Create a sample JSON file
        String fileContent = "{\"valueX\": 8, \"valueY\": 5}";
        MockMultipartFile file = new MockMultipartFile("file", "sample.json",
                MediaType.APPLICATION_JSON_VALUE, fileContent.getBytes());

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(file))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                		.string("File uploaded successfully!"));
    }
    
    @Test
    void testInvalidFileFormat_whenPost() throws Exception {
        // Create a sample JSON file
        String fileContent = "{\"valueX\": 8, \"valueB\": 5}";
        MockMultipartFile file = new MockMultipartFile("file", "newTest.json",
                MediaType.APPLICATION_JSON_VALUE, fileContent.getBytes());

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(file))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                		.string("Invalid file format"));
    }
    
    @Test
    void testFileAlreadyExists_whenPost() throws Exception {
        createFile("exists.json");
    	// Create a sample JSON file
        String fileContent = "{\"valueX\": 5, \"valueY\": 8}";
        MockMultipartFile file = new MockMultipartFile("file", "exists.json",
                MediaType.APPLICATION_JSON_VALUE, fileContent.getBytes());

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(file))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                		.string("File already exists."));
    }

    @Test
    void testPutRequest() throws Exception {
    	
    	// Create a sample JSON file
        String fileContent = "{\"valueX\": 8, \"valueY\": 5}";
        MockMultipartFile file = new MockMultipartFile("file", "update.json",
                MediaType.APPLICATION_JSON_VALUE, fileContent.getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/update");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file))
        		.andDo(print())
        		.andExpect(MockMvcResultMatchers.status().isOk())
        		.andExpect(MockMvcResultMatchers.content()
        				.string("JSON file updated successfully"));
    }

    @Test
    void testGetRequest() throws Exception {
        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/test.json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(77))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }
    
    @Test
    void testANonExistentFile_whenGet() throws Exception {
        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/fileNotReal.json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
        				.string("File Doesn't Exist."));
    }
    
    @Test
    void testDeleteRequest() throws Exception {
    	createFile("sample.json");
        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/delete/sample.json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                		.string("JSON file deleted"));
    }
    
    @Test
    void testANonExistentFile_whenDelete() throws Exception {
        // Perform the DELETE request on non existent file
        mockMvc.perform(MockMvcRequestBuilders.delete("/delete/non_existent.json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                		.string("File doesn't exist."));
    }
}
