import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HTTPRequest(BufferedReader reader) throws IOException {
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();

        // Parse the request line
        String requestLine = reader.readLine();
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length >= 2) {
                this.method = requestParts[0];
                this.path = requestParts[1];
            } else {
                throw new IOException("Invalid Request Line");
            }

            try {
                new URI(path);
            } catch (URISyntaxException e) {
                throw new IOException("Invalid Request Path");
            }

            // Read and store headers
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break; // Headers end with an empty line
                }
                String[] headerParts = line.split(": ");
                if (headerParts.length >= 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
                if (headerParts.length != 2) {
                    throw new IOException("Malformed Header Line");
                }
            }

            // Parse request body if method is POST
            if ("POST".equals(this.method)) {
                try {
                    int contentLength = Integer.parseInt(headers.get("Content-Length"));
                    parseRequestBody(reader, contentLength);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid Content-Length Format");
                }
            }
        }
    }

    private void parseRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        reader.read(body, 0, contentLength);
        String requestBody = new String(body);

        // Basic implementation for parsing URL-encoded form data
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
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
