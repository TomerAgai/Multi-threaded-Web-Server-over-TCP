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
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HTTPRequest(BufferedReader reader, String requestLine) throws IOException {
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();

        if (requestLine != null && !requestLine.isEmpty()) {
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length >= 2) {
                this.method = requestParts[0];
                this.path = requestParts[1];
                this.path = sanitizePath(this.path);
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

            if ("GET".equals(this.method) && this.path.contains("?")) {
                String[] pathAndQuery = this.path.split("\\?", 2);
                this.path = pathAndQuery[0];
                if (pathAndQuery.length > 1) {
                    parseQueryParameters(pathAndQuery[1]); // Parse and store GET query parameters
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
        } else {
            throw new IOException("Empty Request");
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

    private void parseQueryParameters(String query) throws UnsupportedEncodingException {
        if (query == null || query.isEmpty()) {
            return;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx < pair.length() - 1) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                parameters.put(key, value);
            }
        }
    }

    private String sanitizePath(String path) {
        return path.replaceAll("\\.\\.", "");
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
