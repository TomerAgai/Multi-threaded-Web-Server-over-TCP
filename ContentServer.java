import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ContentServer {
    private final String rootDirectory;

    public ContentServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void serveStaticContent(OutputStream out, String path, boolean sendBody) throws IOException {
        String filePath = path;
        File file = new File(rootDirectory, filePath);

        if (file.isDirectory()) {
            ResponseUtility.sendErrorResponse(out, 403, "Forbidden");
            System.out.println("Access to directories is forbidden: " + path);
            return;
        }
        if (!file.exists()) {
            ResponseUtility.sendErrorResponse(out, 404, "Not Found");
            System.out.println("File not found: " + path);
            return;
        }

        String contentType = ResponseUtility.getContentType(filePath);
        sendFile(out, file, contentType, sendBody);
    }

    private void sendFile(OutputStream out, File file, String contentType, boolean sendBody) throws IOException {
        byte[] headerBytes = ("HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + file.length() + "\r\n\r\n").getBytes();
        out.write(headerBytes);

        if (sendBody) {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            out.write(data);
        }
    }
}
