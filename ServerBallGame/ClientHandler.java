import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;


public class ClientHandler implements Runnable {
    private Socket client;
    private Scanner serverReader;
    private PrintWriter serverWriter;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private int playerId, ballHolderId;
    public static Set<Integer> players = new HashSet<>();
    private boolean ball;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        serverReader = new Scanner(client.getInputStream());
        serverWriter = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            playerId = Integer.parseInt(serverReader.nextLine());
            players.add(playerId);
            if (clientHandlers.isEmpty()) {
                ball = true;
                ballHolderId = playerId;
                System.out.println("Client (User #" + playerId + ") has received the ball.");
                serverWriter.println("You are the new ball holder");
            }
            clientHandlers.add(this);
            System.out.println("Client (User #" + playerId + ") has connected.");
            broadcastMessage("\nPlayer #" + playerId + " has joined.\n");
            showAll();
            ballHolder();
            userInput();
        } catch (Exception e) {
        } finally {
                System.out.println("Client (User #" + playerId + ") has disconnected.");
                if (ball) {
                    ball = false;
                    players.remove(this);
                    clientHandlers.remove(this);
                    broadcastMessage("\nThe ball holder (#" + playerId + ") has left.\n");
                    if (clientHandlers.size() >= 1) {
                        Random randomGenerator = new Random();
                        int randomIndex = randomGenerator.nextInt(clientHandlers.size());
                        ClientHandler ballHolder = clientHandlers.get(randomIndex);
                        System.out.println("Client (User #" + ballHolder.playerId + ") has received the ball.");
                        ballHolder.serverWriter.println("You are the new ball holder. \n");
                        ballHolderId = ballHolder.playerId;
                        ballHolder.ball = true;
                    }
                } else {
                    broadcastMessage("\nPlayer #" + playerId + " has left.\n");
                    players.remove(this);
                    clientHandlers.remove(this);
                }
                try {
                    client.close();
                    serverWriter.close();
                    serverReader.close();
                } catch (IOException e) {

                }
                showAll();
                ballHolder();
        }
    }


  private void showAll() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.serverWriter.println("Players currently in lobby: ");
            clientHandlers.forEach(player -> clientHandler.serverWriter.println("#" + player.playerId));
                /*if (clientHandler.serverReader.hasNextLine()) {
                    userInput = clientHandler.serverReader.nextLine();
                    for (int player : players) {
                        if (Integer.parseInt(userInput) == player) {
                            clientHandler.serverWriter.println("Ball passed to player #" + player + ".");
                        }
                    }
                }*/
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.playerId != playerId) {
                    clientHandler.serverWriter.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void ballHolder() {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.ball) {
                String userInput;
                ballHolderId = clientHandler.playerId;
                clientHandler.serverWriter.println("\nYou are the current ball holder.");
                clientHandler.serverWriter.println("Enter another player to pass the ball to: ");
            } else {
            clientHandler.serverWriter.println("\nThe current ball holder is #" + ballHolderId + ".");
            }
        }
    }

    public void userInput () {
        while (true) {
            int input = Integer.parseInt(serverReader.nextLine());
            for (ClientHandler clientHandler : clientHandlers) {
                if (input == clientHandler.playerId && ball) {
                    ball = false;
                    clientHandler.ball = true;
                    ballHolderId = clientHandler.playerId;
                    System.out.println("Client (User #" + playerId + ") has passed the ball to Client (User #" + clientHandler.playerId + ").");
                    serverWriter.println("\nYou have passed the ball to #" + clientHandler.playerId + ".\n");
                    broadcastMessage("\nBall has been passed to #" + clientHandler.playerId + ".\n");
                    clientHandler.serverWriter.println("You are the new ball holder. \n");
                    showAll();
                    ballHolder();
                }
            }
        }
    }
}
