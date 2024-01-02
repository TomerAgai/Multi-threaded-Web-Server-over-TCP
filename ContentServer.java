import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ContentServer {
    private final String rootDirectory;

    public ContentServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void serveHomePage(OutputStream out, String defaultPage) throws IOException {
        File file = new File(rootDirectory + defaultPage);
        if (!file.exists()) {
            ResponseUtility.sendErrorResponse(out, 404, "Not Found");
            return;
        }
        sendFile(out, file, "text/html");
    }

    public void serveAsset(OutputStream out, String path) throws IOException {
        File file = new File(rootDirectory + path);
        if (!file.exists()) {
            ResponseUtility.sendErrorResponse(out, 404, "Not Found");
            return;
        }

        String contentType = ResponseUtility.getContentType(path);
        sendFile(out, file, contentType);
    }

    private void sendFile(OutputStream out, File file, String contentType) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        ResponseUtility.sendOKResponse(out, contentType, data);
    }
}
