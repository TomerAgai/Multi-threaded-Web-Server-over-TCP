import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ContentServer {
    private final String rootDirectory;

    public ContentServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void serveStaticContent(OutputStream out, String path, boolean sendBody, boolean useChunkedEncoding)
            throws IOException {
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
        ResponseUtility.sendFileOkResponse(out, contentType, file, sendBody, useChunkedEncoding);
    }
}
