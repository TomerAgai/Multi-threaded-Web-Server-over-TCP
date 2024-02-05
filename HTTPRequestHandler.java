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
        // System.out.println("http request: " + httpRequest.getHeaders() + " " +
        // httpRequest.getParameters());
        // System.out.println("Handling " + method + " request for " + path);
        switch (method) {
            case "GET":
                handleGETorHEADRequest(path, true);
                break;
            case "POST":
                handlePOSTRequest(path);
                break;
            case "HEAD":
                handleGETorHEADRequest(path, false);
                break;
            case "TRACE":
                handleTRACERequest();
                break;
            default:
                ResponseUtility.sendErrorResponse(outputStream, 501, "Not Implemented");
                break;
        }
    }

    private void handleGETorHEADRequest(String path, Boolean isGet) throws IOException {

        if ("/".equals(path)) {
            System.out.println("Serving home page");
            contentServer.serveStaticContent(outputStream, config.getProperty("DefaultPage"), isGet);
        } else if (path.startsWith("/Asset") || path.startsWith("/favicon.ico")) {
            System.out.println("Serving asset: " + path);
            contentServer.serveStaticContent(outputStream, path, isGet);
        } else {
            ResponseUtility.sendErrorResponse(outputStream, 404, "Not Found");
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
        } else {
            ResponseUtility.sendErrorResponse(outputStream, 404, "Not Found");
        }
    }

    private void handleTRACERequest() throws IOException {
        StringBuilder response = new StringBuilder();
        response.append(httpRequest.getMethod()).append(" ").append(httpRequest.getPath()).append(" HTTP/1.1\r\n");
        for (Map.Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        response.append("\r\n");
        if (!httpRequest.getParameters().isEmpty()) {
            for (Map.Entry<String, String> param : httpRequest.getParameters().entrySet()) {
                String decodedValue = URLDecoder.decode(param.getValue(), StandardCharsets.UTF_8.name());
                response.append(param.getKey()).append("=").append(decodedValue).append("&");
            }
            response.setLength(response.length() - 1); // Remove the last "&"
        }

        ResponseUtility.sendOKResponse(outputStream, "message/http", response.toString().getBytes());
    }
}
