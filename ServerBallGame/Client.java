import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Client {
    private Socket socket;
    private int playerId;
    Scanner clientReader;
    PrintWriter clientMessage;
    private boolean holder;

    public Client (Socket socket, int playerId) throws IOException {
        this.socket = socket;
        this.playerId = playerId;
        clientReader = new Scanner(socket.getInputStream());
        clientMessage = new PrintWriter (socket.getOutputStream(), true);
    }

    public void readMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String incomingMsg = clientReader.nextLine();
                    System.out.println(incomingMsg);
                }
            }
        }).start();
    }

    public void sendInput() {
        try {
                {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                clientMessage.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int playerId = new Random().nextInt(1001);
        Socket socket = new Socket("localhost", 5156);
        Client client = new Client (socket, playerId);
        System.out.println("You have joined as player #" + playerId + ". \n");
        client.clientMessage.println(playerId);
        client.readMessages();
        client.sendInput();
    }
}
           /* if (line.equals("holder")) {
                holder = true;
                System.out.println("You have been given the ball.");
            }
            if (line.contains("playerid")) {
                System.out.println("Players currently in lobby: ");
                System.out.println(line);
                if (holder) {
                    System.out.println("You are the current ball holder.");
                } else {
            if (line.contains("ballholder"))
                    System.out.println("The current ball holder is #" + Integer.parseInt(line.replace("ballholder ", "")));
            }
            System.out.println(line);
            } */

    /*public static void runClient() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 5155);
            Scanner reader = new Scanner (socket.getInputStream());
            String serverResponse = reader.nextLine();
            System.out.println("You have joined as player #" + serverResponse + ".");
            serverResponse = reader.nextLine();
            String[] players = serverResponse.replaceAll("\\p{Punct}", "").split(" ");
            System.out.println("Players currently on: ");
            for (String player : players) {
                System.out.println("#" + player);
            }
            while (true) {
                PrintWriter clientResponse = new PrintWriter (socket.getOutputStream(), true);
                clientResponse.println("ping");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runClient();
    } */

