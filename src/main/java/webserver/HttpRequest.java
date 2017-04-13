package webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gingeraebi on 2017. 4. 10..
 */
public class HttpRequest {
    public String httpMethod;
    public String httpBody;
    public String requestURL;
    public String queryString;

    private Map<String, String> headerMap;

    public HttpRequest(String httpRequestString) {
        String httpMethodLine = httpRequestString.split("\n")[0];
        httpMethod = httpMethodLine.split(" ")[0];
        requestURL = httpMethodLine.split(" ")[1];

        httpRequestString = httpRequestString.split("\n", 2)[1];
        headerMap = makeHeaderMap(httpRequestString);

    }

    public String getHeader(String headerName) {
        return headerMap.get(headerName);
    }

    private Map<String, String> makeHeaderMap(String stringHeaders) {
        Map<String, String> headerMap = new HashMap<>();
        for (String header : stringHeaders.split("\n")) {
            headerMap.put(header.split(": ")[0], header.split(": ")[1]);
        }
        return headerMap;
    }
}
