import java.io.*;

public class ResponseUtility {

    public static void sendOKResponse(OutputStream out, String contentType, byte[] data) throws IOException {
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write(("Content-Type: " + contentType + "\r\n").getBytes());
        out.write(("Content-Length: " + data.length + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(data);
        out.flush();
    }

    public static void sendErrorResponse(OutputStream out, int statusCode, String statusMessage) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.flush();
    }

    public static String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        } else if (path.endsWith(".bmp")) {
            return "image/bmp";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream";
        }
    }
}
