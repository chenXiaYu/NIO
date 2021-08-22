package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9998 ;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true){
                Socket accept = serverSocket.accept();
                new AcceptScoketHandler(accept).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
