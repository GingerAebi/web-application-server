package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;


import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

@SuppressWarnings("Duplicates")
public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            String headers = line;

            while ((!"".equals(line))) {
                if (line == null) return;
                line = br.readLine();
                headers += line + "\n";
            }
            HttpRequest httpRequest = new HttpRequest(headers);
            HttpResponse httpResponse = new HttpResponse(dos, httpRequest);

            if (httpRequest.requestURL.startsWith("/user/create") && httpRequest.httpMethod.equals("POST")) {

                String bodyString = IOUtils.readData(br, Integer.parseInt(httpRequest.getHeader("Content-Length")));
                Map<String, String> data = HttpRequestUtils.parseQueryString(bodyString);

                User user = new User(data.get("userId"), data.get("password"), data.get("name"), data.get("email"));
                DataBase.addUser(user);

                httpResponse.sendRedirect("../index.html");
            } else if (httpRequest.requestURL.startsWith("/user/login") && httpRequest.httpMethod.equals("POST")) {

                String contentLength = httpRequest.getHeader("Content-Length");
                String bodyString = IOUtils.readData(br, Integer.parseInt(httpRequest.getHeader("Content-Length")));


                Map<String, String> data = HttpRequestUtils.parseQueryString(bodyString);
                User user = new User(data.get("userId"), data.get("password"), data.get("name"), data.get("email"));
                if (user.isAuthUser()) {
                    httpResponse.addHeader("Set-Cookie", "logined=true");
                    httpResponse.sendRedirect("../index.html");

                } else {
                    httpResponse.addHeader("Set-Cookie", "logined=false");
                    httpResponse.sendRedirect("./login_failed.html");
                }
            } else {
                httpResponse.response200Header();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}
