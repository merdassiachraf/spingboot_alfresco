package com.dqlick.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.entity.StringEntity;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
public class FileUploadController {

    @PostMapping("/upload/file")
    public ResponseEntity<JsonNode> uploadFile(
            //file upload form-data
            @RequestParam("filedata") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("relativePath") String relativePath,
            @RequestParam("nodeType") String nodeType,
            @RequestParam("cm:title") String title,
            @RequestParam("cm:description") String description,

            // tag add
            @RequestParam("tag") String tag,

            //basic authentification alfresco
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) throws IOException {

        // Create an HTTP client
        HttpClient httpClient = HttpClients.createDefault();

        // Prepare the POST request to the external API with basic authentication
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/-root-/children");
        httpPost.setHeader("Authorization", "Basic " +
                java.util.Base64.getEncoder().encodeToString(
                        (username + ":" + password).getBytes()
                ));

        // Create form data for the file upload
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("filedata", file.getInputStream(), ContentType.APPLICATION_OCTET_STREAM, file.getOriginalFilename())
                .addTextBody("name", name)
                .addTextBody("relativePath", relativePath)
                .addTextBody("nodeType", nodeType)
                .addTextBody("cm:title", title)
                .addTextBody("cm:description", description)
                .addTextBody("tag", tag)
                .build();

        httpPost.setEntity(entity);

        // Execute the POST request
        HttpResponse response = httpClient.execute(httpPost);

        // Handle the response
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());

        // Parse the JSON response using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonUploadResponse = objectMapper.readTree(responseBody);

        // Add to response
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        jsonResponse.set("upload", jsonUploadResponse);

// ****************TAG**********************
        String fileId = jsonUploadResponse.path("entry").path("id").asText();
        System.out.println(fileId);

// Construct the URL with the fileId
        String alfrescoApiUrl = "http://127.0.0.1:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + fileId+"/tags";

        HttpPost httpTagPost = new HttpPost(alfrescoApiUrl);

// Create the JSON payload
        String jsonPayload = "{\"tag\":\"" + tag + "\"}";

// Set the Content-Type header for JSON
        httpTagPost.setHeader("Content-Type", "application/json");

// Set the Authorization header for basic authentication
        httpTagPost.setHeader("Authorization", "Basic " +
                java.util.Base64.getEncoder().encodeToString(
                        (username + ":" + password).getBytes()
                ));

// Attach the JSON payload as the request entity
        StringEntity requestTagEntity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
        httpTagPost.setEntity(requestTagEntity);

// Create an HTTP client
        HttpClient httpTagClient = HttpClients.createDefault();


        // Execute the POST request
        HttpResponse tagResponse = httpTagClient.execute(httpTagPost);

        // Handle the tagResponse as needed (e.g., check status code, parse response JSON, etc.)

        // You can return a ResponseEntity based on the tagResponse if needed
        // For example, you can return a ResponseEntity with status code and message
        int tagStatusCode = tagResponse.getStatusLine().getStatusCode();
        String tagResponseBody = EntityUtils.toString(tagResponse.getEntity());


        // Parse the JSON response using Jackson
        JsonNode jsonTagResponse = objectMapper.readTree(tagResponseBody);

        // Add to response
        jsonResponse.set("tag", jsonTagResponse);

        // Return the JSON response in the ResponseEntity
        return ResponseEntity.status(statusCode).body(jsonResponse);
    }
}

