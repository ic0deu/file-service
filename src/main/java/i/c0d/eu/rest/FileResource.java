package i.c0d.eu.rest;

import i.c0d.eu.resource.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 * Created by antonio on 18/05/2016.
 */
@RestController
@RequestMapping("rest/v1/files")
public class FileResource {

    private static final Logger logger = LoggerFactory.getLogger(FileResource.class);

    @Value("${upload_path}")
    private String UPLOAD_PATH;
    @Value("${static_files}")
    private String STATIC_FILE_LOCATION;
    @Value("${max_content_length}")
    private int MAX_CONTENT_LENGTH;
    @Value("${success_page_url}")
    private String SUCCESS_PAGE_URL;

    @RequestMapping(method = RequestMethod.POST, value = "/{fileId}", consumes = "multipart/form-data" , produces =  "application/json" )
    public ResponseEntity<UploadResponse> upload(
                                 @PathVariable String fileId,
                                 @RequestParam("files[]") MultipartFile[] files,
                                 HttpServletResponse response) throws IOException {
        logger.info("fileId: {}", fileId);

        UploadResponse uploadResponse = new UploadResponse();
        Arrays.stream(files).forEach(file -> {

            if (!file.isEmpty()) {
                String fileName = new StringBuilder().append(fileId).append(new Date().getTime()).append(file.getOriginalFilename()).toString().trim();
                File newFile = new File(UPLOAD_PATH + fileName);
                try {
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
                    FileCopyUtils.copy(file.getInputStream(), stream);
                    uploadResponse.addFile(fileName, newFile.length(), fileName, fileName, fileName, "DELETE", Optional.empty());

                    stream.close();
                    logger.info("File {} succesfully uploaded", fileName);
                }
                catch (Exception e) {
                    logger.info("Error while uploading file: {}", fileName);
                    uploadResponse.addFile(fileName, newFile.length(), "", "", "", "", Optional.of("Error while uploading"));
                }
            }
        });

        if (!SUCCESS_PAGE_URL.isEmpty()) {
            response.sendRedirect(SUCCESS_PAGE_URL);
        }

        return new ResponseEntity<>(uploadResponse, HttpStatus.CREATED);


    }



}
