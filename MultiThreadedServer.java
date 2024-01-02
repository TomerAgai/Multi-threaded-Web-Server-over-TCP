import java.net.*;
import java.io.*;
import java.util.Properties;

public class MultiThreadedServer {

    public static void main(String[] args) throws IOException {
        Properties config = loadConfigurations();
        int port = Integer.parseInt(config.getProperty("Port", "8080"));
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket, config)).start();
        }
    }

    private static Properties loadConfigurations() {
        Properties config = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("config.ini"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("[")) {
                    // Skip empty lines and section headers
                    continue;
                }
                String[] parts = line.split("=", 2);
                if (parts.length >= 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    config.setProperty(key, value);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }

}