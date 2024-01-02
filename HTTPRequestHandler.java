import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HTTPRequestHandler {
    private final HTTPRequest httpRequest;
    private final OutputStream outputStream;
    private final Properties config;
    private final ContentServer contentServer;

    public HTTPRequestHandler(HTTPRequest httpRequest, OutputStream outputStream, Properties config) {
        this.httpRequest = httpRequest;
        this.outputStream = outputStream;
        this.config = config;
        this.contentServer = new ContentServer(config.getProperty("RootDirectory"));
    }

    public void handleRequest() throws IOException {
        String method = httpRequest.getMethod();
        String path = httpRequest.getPath();

        switch (method) {
            case "GET":
                handleGETRequest(path);
                break;
            case "POST":
                handlePOSTRequest(path);
                break;
            // Additional cases for other methods like POST, HEAD, etc.
            default:
                ResponseUtility.sendErrorResponse(outputStream, 501, "Not Implemented");
                break;

        }
    }

    private void handleGETRequest(String path) throws IOException {
        if ("/".equals(path)) {
            System.out.println("Serving home page");
            contentServer.serveHomePage(outputStream, config.getProperty("DefaultPage"));
        } else {
            System.out.println("Serving asset: " + path);
            contentServer.serveAsset(outputStream, path);
        }
    }

    private void handlePOSTRequest(String path) throws IOException {
        if ("/params_info.html".equals(path)) {
            StringBuilder responseHtml = new StringBuilder("<html><body>");
            responseHtml.append("<h1>Submitted Parameters</h1>");
            for (Map.Entry<String, String> entry : httpRequest.getParameters().entrySet()) {
                String decodedValue = URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8.name());
                responseHtml.append("<p>").append(entry.getKey()).append(": ").append(decodedValue).append("</p>");
            }
            responseHtml.append("</body></html>");

            ResponseUtility.sendOKResponse(outputStream, "text/html", responseHtml.toString().getBytes());
        }
    }

    // Additional private methods for handling other request types
}
