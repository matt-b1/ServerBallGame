import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void runServer() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(5156);
            System.out.println("Waiting for incoming connections...");
            while (true) {
                Socket client = serverSocket.accept();
                ClientHandler session = new ClientHandler(client);
                new Thread(session).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        runServer();
    }
}
