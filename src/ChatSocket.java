import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class ChatSocket {
    private static final int PORT = 8084;
    private static final List<PrintWriter> writers = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Servidor rodando na porta " + PORT);
        try(ServerSocket serverSocket = new ServerSocket(PORT, 10)) {
            while(true) {
                new ClientHandler(serverSocket.accept(), writers).start();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
