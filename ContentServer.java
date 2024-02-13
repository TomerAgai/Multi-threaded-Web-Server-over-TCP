import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ContentServer {
    private final String rootDirectory;

    public ContentServer(String rootDirectory) {
        if (rootDirectory.startsWith("~" + File.separator)) {
            this.rootDirectory = System.getProperty("user.home") + rootDirectory.substring(1);
        } else {
            this.rootDirectory = rootDirectory;
        }
    }

    public void serveStaticContent(OutputStream out, String path, boolean sendBody, boolean useChunkedEncoding)
            throws IOException {
        String filePath = path;
        File file = new File(rootDirectory, filePath);
        if (file.isDirectory()) {
            System.out.println("is a directory");
            // Instead of forbidding access, generate directory listing
            if (sendBody) {
                // Pass the requested path to generate correct links in the directory listing
                generateAndSendDirectoryListing(out, file, path, useChunkedEncoding);
            } else {
                ResponseUtility.sendOKResponse(out, "text/html", new byte[0], useChunkedEncoding);
            }
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

    private void generateAndSendDirectoryListing(OutputStream out, File directory, String requestedPath,
            boolean useChunkedEncoding) throws IOException {

        StringBuilder listingHtml = new StringBuilder("<html><head><title>Directory Listing</title></head><body>");
        listingHtml.append("<h1> ").append(requestedPath).append("</h1><ul>");
        String parentPath = requestedPath.endsWith("/") ? requestedPath : requestedPath + "/";
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                listingHtml.append("<li><a href=\"").append(parentPath).append(name).append(f.isDirectory() ? "/" : "")
                        .append("\">").append(name).append("</a></li>");
            }
        }

        listingHtml.append("</ul></body></html>");
        byte[] listingBytes = listingHtml.toString().getBytes();
        ResponseUtility.sendOKResponse(out, "text/html", listingBytes, useChunkedEncoding);
    }
}
