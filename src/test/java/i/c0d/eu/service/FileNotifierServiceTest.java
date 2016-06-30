package i.c0d.eu.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
/**
 * Created by antonio on 30/06/2016.
 */
public class FileNotifierServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    private String endpoint = "/rest/v1/trigger/upload";

    private FileNotifierService testObj;

    @Before
    public void init() {
       testObj = new FileNotifierService("http", "localhost", wireMockRule.port(), endpoint, "{\"fileId\": \"PH\"}", "PH", new RestTemplate());
    }

    @Test
    public void testNotifyEvent_success() throws IOException {
        stubFor(post(urlEqualTo(endpoint))
                .withHeader("Accept", containing("application/json"))
                .withRequestBody(equalToJson("{\"fileId\": \"492374923\"}"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\": true}")));


        assertTrue(testObj.notifyEvent("492374923"));


        verify(postRequestedFor(urlMatching(endpoint))
                .withRequestBody(equalToJson("{\"fileId\": \"492374923\"}"))
                .withHeader("Content-Type", matching("application/json")));
    }

    @Test
    public void testNotifyEvent_failed() throws IOException {
        stubFor(post(urlEqualTo(endpoint))
                .withHeader("Accept", containing("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\": false}")));


        assertFalse(testObj.notifyEvent("492373"));

        verify(postRequestedFor(urlMatching(endpoint))
                .withRequestBody(equalToJson("{\"fileId\": \"492373\"}"))
                .withHeader("Content-Type", matching("application/json")));
    }

}