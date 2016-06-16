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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;

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


    @Value("${fileName_tag_separator}")
    String FILENAME_TAG_SEPARATOR;

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
                .body("files[0].name", containsString("ds"))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "ds")
                .then()
                .extract()
                .path("files[0].name");
        File file = new File(upload_path+fileName);

        assertThat("File has been deleted" , file.delete());
    }

    @Test
    public void list_files_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name",  containsString("123456"+FILENAME_TAG_SEPARATOR))
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
                .when().log().all().get("/rest/v1/files/{id}", "123456");
        File file = new File(upload_path+fileName);

        assertThat("File has been deleted" , file.delete());
    }

    @Test
    public void tag_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name",  containsString("123456"+FILENAME_TAG_SEPARATOR))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0].name");

        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = fileNameWithoutExtension + FILENAME_TAG_SEPARATOR + "ci" + extension;
        given().log().all()
                .body("{\"fileName\": \"" + fileName + "\",\"tag\": \"ci\"}")
                .contentType(ContentType.JSON)
                .expect()
                .body("oldFileName", containsString(fileName))
                .body("newFileName", containsString(newFileName))
                .statusCode(HttpStatus.SC_ACCEPTED)
                .contentType(ContentType.JSON)
                .when().log().all().put("/rest/v1/files/");
        File file = new File(upload_path+newFileName);
        assertThat("File has been deleted" , file.delete());
    }

    @Test
    public void newFileName_will_ignore_tag_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name",  containsString("123456" + FILENAME_TAG_SEPARATOR))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0].name");

        String newFileName = "ci.txt";
        given().log().all()
                .body("{\"fileName\": \"" + fileName + "\",\"tag\": \"ci\",\"newFileName\": \"" + newFileName + "\"}")
                .contentType(ContentType.JSON)
                .expect()
                .body("oldFileName", containsString(fileName))
                .body("newFileName", containsString(newFileName))
                .statusCode(HttpStatus.SC_ACCEPTED)
                .contentType(ContentType.JSON)
                .when().log().all().put("/rest/v1/files/");
        File file = new File( upload_path + newFileName );
        assertThat("File has been deleted" , file.delete());
    }

    @Test
    public void newFileName_cannot_change_file_extension_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name",  containsString("123456" + FILENAME_TAG_SEPARATOR))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0].name");

        String newFileName = "ci.FDP";
        given().log().all()
                .body("{\"fileName\": \"" + fileName + "\",\"tag\": \"ci\",\"newFileName\": \"" + newFileName + "\"}")
                .contentType(ContentType.JSON)
                .expect()
                .body("message", containsString("Cannot change file extension"))
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .contentType(ContentType.JSON)
                .when().log().all().put("/rest/v1/files/");
        File file = new File( upload_path + fileName );
        assertThat("File has been deleted" , file.delete());
    }

    @Test
    public void multiple_tag_test() {
        String fileName = given().log().all()
                .multiPart("files[]", new File(String.valueOf(Resources.getResource("FileToUpload.txt").getFile())))
                .expect()
                .body("files", hasSize(1))
                .body("files[0].name",  containsString("123456" + FILENAME_TAG_SEPARATOR))
                .statusCode(HttpStatus.SC_CREATED)
                .when().log().all().post("/rest/v1/files/{id}", "123456")
                .then()
                .extract()
                .path("files[0].name");

        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = fileNameWithoutExtension + FILENAME_TAG_SEPARATOR + "ci" + extension;
        given().log().all()
                .body("{\"fileName\": \"" + fileName + "\",\"tag\": \"ci\"}")
                .contentType(ContentType.JSON)
                .expect()
                .body("oldFileName", containsString(fileName))
                .body("newFileName", containsString(newFileName))
                .statusCode(HttpStatus.SC_ACCEPTED)
                .contentType(ContentType.JSON)
                .when().log().all().put("/rest/v1/files/");

        given().log().all()
                .body("{\"fileName\": \"" + newFileName + "\",\"tag\": \"ci\"}")
                .contentType(ContentType.JSON)
                .expect()
                .statusCode(HttpStatus.SC_CONFLICT)
                .contentType(ContentType.JSON)
                .when().log().all().put("/rest/v1/files/");
        File file = new File(upload_path+newFileName);


        assertThat("File has been deleted" , file.delete());
    }

}