package i.c0d.eu.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import i.c0d.eu.resource.FileMetadata;
import i.c0d.eu.resource.UploadResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by antonio on 18/05/2016.
 */
@Component
@Path("/v1/files")
public class FileResource {

    private static final Logger logger = LoggerFactory.getLogger(FileResource.class);

    @Value("${upload_path}")
    private String UPLOAD_PATH;
    @Value("${static_files}")
    private String STATIC_FILE_LOCATION;
    @Value("${max_content_length}")
    private int MAX_CONTENT_LENGTH;


    @POST
    @Path("{fileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@Context final HttpServletRequest request,
                           @PathParam("fileId") String fileId,
                           @FormDataParam("fileDescription") FormDataBodyPart jsonPart,
                           @FormDataParam("files[]") List<FormDataBodyPart> parts) throws IOException {
        logger.info("fileId: {}", fileId);
        int contentLength = request.getContentLength();
        if ( contentLength > MAX_CONTENT_LENGTH ) {
            logger.error("File is too big !!!");
            throw new WebApplicationException("File is too big !!!");
        }
        if (jsonPart != null) {
            jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            FileMetadata[] fileMetadata = jsonPart.getValueAs(FileMetadata[].class);
            Arrays.stream(fileMetadata).forEach( e -> System.out.println(e.toString()));
        }
        UploadResponse uploadResponse = new UploadResponse();
        for (FormDataBodyPart part : parts) {
            FormDataContentDisposition disp = part.getFormDataContentDisposition();
            InputStream in = part.getValueAs(InputStream.class);
            String fileName = new StringBuilder().append(fileId).append(new Date().getTime()).append(disp.getFileName()).toString().trim();
            logger.info("Processing file named: {}", fileName);
            File file = new File(UPLOAD_PATH + fileName);
            try {
                int read;
                byte[] bytes = new byte[1024];
                OutputStream out = new FileOutputStream(file);
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.flush();
                out.close();
                uploadResponse.addFile(fileName, file.length(), fileName, fileName, fileName, "DELETE", Optional.empty());
            } catch (IOException e) {
                uploadResponse.addFile(fileName, file.length(), "", "", "", "", Optional.of("Error while uploading"));
            }

        }
        return Response.ok(uploadResponse).build();
    }

}
