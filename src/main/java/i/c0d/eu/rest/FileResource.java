package i.c0d.eu.rest;

import i.c0d.eu.resource.FileListResponse;
import i.c0d.eu.resource.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                String fileName = new StringBuilder().append(fileId).append(new Date().getTime()).append("-").append(file.getOriginalFilename()).toString().trim();

                if ( ! UPLOAD_PATH.isEmpty() ) {
                    UPLOAD_PATH += UPLOAD_PATH.endsWith("/") ? "" : "/";
                }
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


    private List<String> getFileList() {
        return Arrays.asList(new File(UPLOAD_PATH).list());
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}", consumes = "application/json" , produces =  "application/json" )
    public ResponseEntity<FileListResponse> list(@PathVariable String fileId ) throws IOException {
        if (fileId.isEmpty()) {
            throw new RuntimeException("FiledId must be valid");
        }

        logger.info("Received listing request for files matching fileId: " + fileId);

        FileListResponse list = new FileListResponse();
        list.setFiles(getFileList().parallelStream().filter( s -> s.startsWith(fileId)).collect(Collectors.toList()));

        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }



}
