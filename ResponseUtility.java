import java.io.*;

public class ResponseUtility {

    public static void sendOKResponse(OutputStream out, String contentType, byte[] data, boolean useChunkedEncoding)
            throws IOException {
        if (useChunkedEncoding) {
            // Use ByteArrayInputStream to wrap the byte array
            try (InputStream in = new ByteArrayInputStream(data)) {
                sendChunkedResponseFromStream(out, in, contentType);
            }
        } else {
            String headers = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + data.length + "\r\n\r\n";
            printResponseHeaders(headers);
            out.write(headers.getBytes());
            out.write(data);
        }
        out.flush();
    }

    public static void sendFileOkResponse(OutputStream out, String contentType, File file, boolean sendBody,
            boolean useChunkedEncoding) throws IOException {

        if (useChunkedEncoding && sendBody) {
            // Use FileInputStream to read the file
            try (FileInputStream fis = new FileInputStream(file)) {
                sendChunkedResponseFromStream(out, fis, contentType);
            }
        } else {
            byte[] headerBytes = ("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + file.length() + "\r\n\r\n").getBytes();
            out.write(headerBytes);
            printResponseHeaders(new String(headerBytes));

            if (sendBody) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                out.write(data);
            }
        }
    }

    public static void sendChunkedResponseFromStream(OutputStream out, InputStream in, String contentType)
            throws IOException {
        String chunkedHeaders = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Transfer-Encoding: chunked\r\n\r\n";
        printResponseHeaders(chunkedHeaders);
        out.write(chunkedHeaders.getBytes());

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            String sizeHeader = Integer.toHexString(bytesRead) + "\r\n";
            out.write(sizeHeader.getBytes());
            out.write(buffer, 0, bytesRead);
            out.write("\r\n".getBytes());
        }
        out.write("0\r\n\r\n".getBytes());
        out.flush();
    }

    public static void sendErrorResponse(OutputStream out, int statusCode, String statusMessage) throws IOException {
        String headers = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/html\r\n\r\n";

        String body = "<html><body><h1>" + statusCode + " " + statusMessage +
                "</h1></body></html>";

        printResponseHeaders(headers);
        out.write(headers.getBytes());
        out.write(body.getBytes());
        out.flush();
    }

    private static void printResponseHeaders(String headers) {
        System.out.println();
        System.out.println("Sending HTTP Response Headers:");
        System.out.println(headers.trim());
        System.out.println("**");
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
        } else if (path.endsWith(".mp4")) {
            return "video/mp4"; 
        } else if (path.endsWith(".pdf")) {
            return "application/pdf"; 
        } else if (path.endsWith(".doc") || path.endsWith(".docx")) {
            return "application/msword"; 
        } else if (path.endsWith(".css")) {
            return "text/css"; 
        }else {
            return "application/octet-stream";
        }
    }
}
