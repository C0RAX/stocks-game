package stocksgame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StockServer {

    public static final int PORT = 8888;
    public static List<StockService> clientList = new ArrayList<>();

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        
        Game game = new Game();
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started StockServer at port " + PORT);
        System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = server.accept();
            System.out.println("Client connected.");
            StockService service = new StockService(game, socket);
            clientList.add(service);
            new Thread(service).start();
            System.out.println("Thread started");
        }
    }

}
