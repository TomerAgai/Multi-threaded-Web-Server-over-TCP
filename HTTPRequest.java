import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();

    public HTTPRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty Request");
        }

        System.out.println("\nReceived HTTP Request: " + requestLine);
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {
            throw new IOException("Invalid Request Line");
        }

        this.method = requestParts[0];
        int queryParamStart = requestParts[1].indexOf('?');
        if (queryParamStart != -1) {
            this.path = requestParts[1].substring(0, queryParamStart);
            String queryString = requestParts[1].substring(queryParamStart + 1);
            parseQueryParameters(queryString);
        } else {
            this.path = requestParts[1];
        }

        this.path = sanitizePath(this.path);

        try {
            new URI(this.path);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid Request Path: " + this.path);
        }

        readHeaders(reader);

        if ("POST".equals(this.method) && this.headers.containsKey("Content-Length")) {
            parseRequestBody(reader);
        }

        System.out.println("Sanitized Path: " + this.path);
        System.out.print(this.parameters.isEmpty() ? "" : "Parameters: " + this.parameters.toString() + "\n");
    }

    private void readHeaders(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonPos = line.indexOf(":");
            if (colonPos == -1) {
                throw new IOException("Malformed Header Line: " + line);
            }
            String headerName = line.substring(0, colonPos).trim();
            String headerValue = line.substring(colonPos + 1).trim();
            headers.put(headerName, headerValue);
        }

        // Log received HTTP request headers
        System.out.println("Received HTTP Request Headers:");
        headers.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("**");
    }

    private void parseRequestBody(BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] body = new char[contentLength];
        reader.read(body, 0, contentLength);
        String requestBody = new String(body);
        parseQueryParameters(requestBody);
    }

    private void parseQueryParameters(String query) throws UnsupportedEncodingException {
        if (query == null || query.isEmpty()) {
            return;
        }
        if (method.equals("POST")) {
            System.out.println("request body: " + query);
        }
        for (String pair : query.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx < pair.length() - 1) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                parameters.put(key, value);
                System.out.println("Parameter: " + key + " = " + value);
            }
        }
    }

    private String sanitizePath(String path) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        return decodedPath.replaceAll("/+\\.+/", "/")
                .replaceAll("\\.\\./", "")
                .replaceAll("%2e%2e%2f", "")
                .replaceAll("\\.\\.", "");
    }

    // Getter methods
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
