package PR;

import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Server {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String requestLine = reader.readLine();
            if (requestLine != null && requestLine.startsWith("POST")) {
                System.out.println("Simulation request received...");

                // Read and discard HTTP headers
                while (reader.readLine().length() > 0)
                    ;

                // Execute CloudSimExample simulation
                Process process = Runtime.getRuntime()
                        .exec("java -cp bin:lib/cloudsim-3.0.3.jar:lib/json-simple-1.1.1.jar PR.CloudSimExample");
                process.waitFor();

                // Read JSON output
                JSONParser parser = new JSONParser();
                JSONArray results = (JSONArray) parser.parse(new FileReader("frontend/output.json"));

                // Send HTTP response
                writer.write("HTTP/1.1 200 OK\r\n");
                writer.write("Access-Control-Allow-Origin: *\r\n"); // Fix CORS issue
                writer.write("Content-Type: application/json\r\n");
                writer.write("\r\n");
                writer.write(results.toJSONString());
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
