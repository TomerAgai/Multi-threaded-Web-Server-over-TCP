**MultiThreadedServer: Main Class**
- This is the entry point of the server application. It initializes the server configurations, starts the server on a specified port, and handles incoming client connections by assigning them to a thread pool for processing.
- Manages server lifecycle, including starting and stopping the server. Utilizes a fixed-size thread pool to handle client requests concurrently, improving scalability and resource management.

**ClientHandler: Request Processing Class**
- Handles individual client connections. Reads the request from the client socket, processes the request, and sends back the appropriate response.
- Acts as the executor's task, processing incoming HTTP requests. It parses the request, interacts with other components to generate responses, and ensures proper resource cleanup post-processing.

**HTTPRequest: HTTP Request Parsing Class**
- Parses the HTTP request from the client. Extracts the method, path, headers, and parameters from the request for further processing.
- Provides a structured representation of a client's HTTP request to facilitate request handling. It supports basic validation and sanitation to prevent malformed or malicious requests.

**HTTPRequestHandler: Request Handler Class**
- Processes parsed HTTP requests based on the HTTP method. It delegates the handling of GET, POST, HEAD, and TRACE requests to specific methods within the class.
- Deciding how different types of requests are handled. It interacts with the ContentServer for serving static content or generating dynamic responses.

**ContentServer: Static Content Serving Class**
- Serves static files from a specified root directory. Handles directory listings and file serving based on the request path.
- Manages access to static resources. Supports basic content negotiation and chunked encoding for efficient data transfer.

**ResponseUtility: HTTP Response Utility Class**
- Provides utility methods for sending HTTP responses. It includes methods for sending OK, error, and chunked responses, as well as determining content types.
- It is a helper class that abstracts away the details of constructing and sending HTTP responses. It ensures that responses conform to HTTP standards.

Design Approach:
The server is designed around the concept of multi-threading and modularity, enabling efficient handling of multiple client requests concurrently.
By utilizing a fixed-size thread pool, the server ensures that system resources are effectively managed, preventing resource exhaustion under heavy load. 
This design also allows for easy expansion or modification of server capabilities, as each component (such as request parsing, handling, and response generation) is encapsulated in separate classes. 
The server supports basic HTTP methods and is capable of serving static content as well as handling dynamic requests, making it suitable for a variety of web applications. 
The configuration-driven approach, utilizing an external config.ini file, enhances flexibility and ease of deployment.

**Usage**
- Execute `./compile.sh`
- Execute `./run.sh`