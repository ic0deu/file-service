package i.c0d.eu.rest;

import i.c0d.eu.resource.FileMetadata;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by antonio on 18/05/2016.
 */
@Component
@Path("/v1/files")
public class FileResource {

    @Value("${upload_path}")
    private String UPLOAD_PATH;



    @POST
    @Path("{fileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@PathParam("fileId") String fileId,
                           @FormDataParam("fileDescription") FormDataBodyPart jsonPart,
                           @FormDataParam("file") List<FormDataBodyPart> parts) {
        if (jsonPart != null) {
            jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            FileMetadata[] fileMetadata = jsonPart.getValueAs(FileMetadata[].class);
            Arrays.stream(fileMetadata).forEach( e -> System.out.println(e.toString()));
        }
        for (FormDataBodyPart part : parts) {
            FormDataContentDisposition disp = part.getFormDataContentDisposition();
            InputStream in = part.getValueAs(InputStream.class);

            try {
                int read = 0;
                byte[] bytes = new byte[1024];

                OutputStream out = new FileOutputStream(new File(UPLOAD_PATH + fileId + new Date().getTime() + disp.getFileName()));
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new WebApplicationException("Error while uploading file. Please try again !!");
            }

        }


        return Response.ok("Data uploaded successfully !!").build();
    }

}
