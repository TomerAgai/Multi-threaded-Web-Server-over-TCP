import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedServer {
    private static ExecutorService executor;
    private static int maxThreads;

    public static void main(String[] args) {
        Properties config = loadConfigurations();
        maxThreads = Integer.parseInt(config.getProperty("MaxThreads", "10"));
        int port = Integer.parseInt(config.getProperty("Port", "8080"));

        // Create a thread pool
        executor = Executors.newFixedThreadPool(maxThreads);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Submit a new task to the thread pool rather than creating a new thread
                    executor.execute(new ClientHandler(clientSocket, config));
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown(); // Initiates an orderly shutdown
            }
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
            System.out.println("Error loading configuration: " + ex.getMessage());
        }
        return config;
    }

}