package i.c0d.eu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by antonio on 30/06/2016.
 */
@Component
public class FileNotifierService {
    private RestTemplate restTemplate;
    private String url, jsonString, fileIdPlaceHolder;

    private static final Logger logger = LoggerFactory.getLogger(FileNotifierService.class);

    @Autowired
    public FileNotifierService(@Value("${trigger.protocol}") String protocol,
                               @Value("${trigger.host}") String host,
                               @Value("${trigger.port}") int port,
                               @Value("${trigger.endpoint}") String endpoint,
                               @Value("${trigger.jsonString}")String jsonString,
                               @Value("${trigger.fileIdPlaceHolder}") String fileIdPlaceHolder,
                               RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.url =  protocol + "://" + host + ":" + port + "/" + endpoint;
        this.jsonString = jsonString;
        this.fileIdPlaceHolder = fileIdPlaceHolder;
    }

    public Boolean notifyEvent(String fileId) throws IOException {

        String payload = jsonString.replace(fileIdPlaceHolder, fileId);
        logger.info("Notifying fileId {} to {} with payload {}", fileId, url, payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(payload,headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } catch(RestClientException restClientException) {
            logger.error("Error while notifying event, cause {}", restClientException.getLocalizedMessage());
            return false;
        }
        String json = responseEntity.getBody().toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        return rootNode.path("result").asBoolean();
    }
}
