package i.c0d.eu.rest;

import com.google.common.io.Resources;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import i.c0d.eu.FileServiceApplication;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FileServiceApplication.class)
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
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name", containsString("FileToUpload"))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "ds")
                .then()
                .extract()
                .path("files[0].name");
        File file = new File(upload_path+fileName);
        if(file.delete()){
            System.out.println(file.getName() + " is deleted!");
        }else{
            System.out.println("Delete operation is failed.");
        }
    }

    @Test
    public void list_files_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name", containsString("FileToUpload"))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0].name");


        given().log().all()
                .expect()
                .body("files", hasSize(1))
                .body("files[0]", containsString("123456"))
                .statusCode(HttpStatus.SC_ACCEPTED)
                .contentType(ContentType.JSON)
                .when().log().all().get("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0]");
        File file = new File(upload_path+fileName);
        if(file.delete()){
            System.out.println(file.getName() + " is deleted!");
        }else{
            System.out.println("Delete operation is failed.");
        }
    }

}