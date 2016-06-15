package i.c0d.eu.rest;

import i.c0d.eu.exception.TagDuplicationException;
import i.c0d.eu.exception.TagFileNotFoundException;
import i.c0d.eu.exception.TagRenameException;
import i.c0d.eu.request.TagRequest;
import i.c0d.eu.resource.FileListResponse;
import i.c0d.eu.resource.TagResponse;
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
                String fileName = new StringBuilder().append(fileId).append(fileId.endsWith("-") ? "" : "-").append(new Date().getTime()).append(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))).toString().trim();

                if ( ! UPLOAD_PATH.isEmpty() ) {
                    UPLOAD_PATH += UPLOAD_PATH.endsWith("/") ? "" : "/";
                }
                File newFile = new File(UPLOAD_PATH + fileName.replace("/",""));
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
            throw new RuntimeException("FileId must be valid");
        }

        logger.info("Received listing request for files matching fileId: " + fileId);

        FileListResponse list = new FileListResponse();
        list.setFiles(getFileList().parallelStream().filter( s -> s.startsWith(fileId)).collect(Collectors.toList()));

        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json" , produces =  "application/json" )
    public ResponseEntity<TagResponse> appendTag(@RequestBody TagRequest tag ) throws IOException {
        if (tag.getFileName().isEmpty()) {
            throw new RuntimeException("Filename must be valid");
        }
        if ( ! UPLOAD_PATH.isEmpty() ) {
            UPLOAD_PATH += UPLOAD_PATH.endsWith("/") ? "" : "/";
        }
        TagResponse response = new TagResponse();
        String fileName = tag.getFileName().replace("/","");
        File oldFile = new File(UPLOAD_PATH + fileName);
        if (oldFile.isFile()) {
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
            if (fileNameWithoutExtension.contains("-" + tag.getTag())) {
                throw new TagDuplicationException();
            }
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = fileNameWithoutExtension + "-" + tag.getTag() + extension;

            response.setOldFileName(fileName);
            response.setNewFileName(newFileName);

            File newFile = new File(UPLOAD_PATH + newFileName);
            if(oldFile.renameTo(newFile)){
                logger.info("File successfully renamed from {} to {}", fileName, newFileName);
                if(oldFile.delete()){
                    logger.info("OldFile {} removed", fileName);
                }
            }else{
                logger.error("Failed to rename file from {} to {}", fileName, newFileName);
                throw new TagRenameException("Failed to rename file from " + fileName + " to " + newFileName);
            }
        } else {
            logger.error("File named {} does not exist", tag.getFileName());
            throw new TagRenameException("File named " + tag.getFileName() + " does not exist");
        }


        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }


    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Tag already exist")  // 409
    @ExceptionHandler(TagDuplicationException.class)
    public void tagDuplicationHandler() {
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Failed to rename")  // 409
    @ExceptionHandler(TagRenameException.class)
    public void failedRenamingHandler() {
        // Nothing to do
    }


    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="File not found")  // 404
    @ExceptionHandler(TagFileNotFoundException.class)
    public void fileNotFoundHandler() {
        // Nothing to do
    }

}
