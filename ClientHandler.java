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
        BufferedReader in = null;
        OutputStream out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();

            // System.out.println("Handling request from " +
            // clientSocket.getRemoteSocketAddress());
            HTTPRequest httpRequest = new HTTPRequest(in);
            HTTPRequestHandler requestHandler = new HTTPRequestHandler(httpRequest, out, config);
            requestHandler.handleRequest();

        } catch (IOException e) {
            System.out.println("Error handling client request: " + e.getMessage());
            if (!clientSocket.isClosed()) {
                try {
                    ResponseUtility.sendErrorResponse(out, 400, "Bad Request");
                } catch (SocketException se) {
                    System.out.println("Client closed connection: " + se.getMessage());
                } catch (IOException ioException) {
                    // Handle other IOExceptions that might occur when sending the error response
                    System.out.println("Error sending error response: " + ioException.getMessage());
                }
            }
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (!clientSocket.isClosed())
                    clientSocket.close();
            } catch (IOException ioException) {
                System.out.println("Error closing resources: " + ioException.getMessage());
            }
        }
    }

}
