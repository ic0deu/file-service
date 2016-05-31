package i.c0d.eu.rest;

import com.google.common.io.Resources;
import com.jayway.restassured.RestAssured;
import i.c0d.eu.DocumentServiceApplication;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DocumentServiceApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(locations="classpath:test.properties")
public class FileResourceTest {

    @Value("${local.server.port}")
    int port;

    @Value("${upload_path}")
    String upload_path;



    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void upload_a_single_file() {
        String fileName = given().log().all()
                .multiPart(new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect().body("result", is("Data Uploaded Successfully"))
                .body("fileNames", hasSize(1))
                .body("fileNames[0]", containsString("FileToUpload"))
                .statusCode(HttpStatus.SC_OK)
                .when().log().all().post("/rest/v1/files/{id}", "ds")
                .then()
                .extract()
                .path("fileNames[0]");
        File file = new File(upload_path+fileName);
        if(file.delete()){
            System.out.println(file.getName() + " is deleted!");
        }else{
            System.out.println("Delete operation is failed.");
        }
    }

}