package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gingeraebi on 2017. 4. 10..
 */
@SuppressWarnings("Duplicates")
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private Map<String, String> headerMap = new HashMap<>();
    private DataOutputStream dos;
    private HttpRequest httpRequest;

    public HttpResponse(DataOutputStream dos, HttpRequest httpRequest) {
        this.dos = dos;
        this.httpRequest = httpRequest;
        addHeader("Content-Type", httpRequest.getHeader("Accept"));
        addHeader("Set-Cookie", httpRequest.getHeader("Cookie"));
    }

    public void sendRedirect(String redirectURL) {
        addHeader("Location", redirectURL);
        makeResponseHeader("HTTP/1.1 302 Found");
        sendResponse();
    }


    public void response200Header() {
        makeResponseHeader("HTTP/1.1 200 OK");
        makeResponseBody();
        sendResponse();
    }


    public void addHeader(String headerName, String headerContent) {
        if (headerContent != null && !headerContent.isEmpty()) headerMap.put(headerName, headerContent);
    }

    private void makeResponseHeader(String httpStatus) {
        try {
            dos.writeBytes(httpStatus + "\r\n");
            for (String headerName : headerMap.keySet()) {
                String headerString = headerName + ": " + headerMap.get(headerName) + "\r\n";
                dos.writeBytes(headerString);
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void makeResponseBody() {
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + httpRequest.requestURL).toPath());
            dos.write(body, 0, body.length);

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void sendResponse() {
        try {
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
