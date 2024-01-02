import java.io.*;
import java.net.*;
import java.util.Properties;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Properties config;

    public ClientHandler(Socket socket, Properties config) {
        this.clientSocket = socket;
        this.config = config;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            try {
                HTTPRequest httpRequest = new HTTPRequest(in);
                HTTPRequestHandler requestHandler = new HTTPRequestHandler(httpRequest, out, config);
                requestHandler.handleRequest();
            } catch (IOException e) {
                // Handle specific bad request scenario
                ResponseUtility.sendErrorResponse(out, 400, "Bad Request");
            }

            // Close streams and socket
            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception e) {
            // Log the error and send a 500 Internal Server Error response
            e.printStackTrace();
            try {
                ResponseUtility.sendErrorResponse(clientSocket.getOutputStream(), 500, "Internal Server Error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
