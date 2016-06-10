package i.c0d.eu.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class AppErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(AppErrorController.class);

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();


    @Value("${error_page_url}")
    private String ERROR_PAGE_URL;

    private final static String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        logger.info("Handling error path: {} status: {}",  body.get("path"),  body.get("status"));

        if (logger.isDebugEnabled()) {
            body.forEach( (k, v) -> logger.debug("Values in body for {} = {}", k, v));
        }

        if (!ERROR_PAGE_URL.isEmpty()) {
            response.sendRedirect(ERROR_PAGE_URL);
        }

        if ( body.get("message").toString().contains("Connection terminated as request was larger than") ||
                body.get("message").toString().contains("Connection terminated parsing multipart data")) {
            logger.info("Customising response for payload too large");
            status = HttpStatus.PAYLOAD_TOO_LARGE;
            body.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
            body.put("error", HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase());
        }

        return new ResponseEntity<>(body, status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes,
                includeStackTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
