import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    // Mensagem que define que a conexão deve ser fechada.
    private static final String ON_CLOSE_MESSAGE = "F4V0R1gXi3g9vOC5v";

    private final Socket socket;
    private final List<PrintWriter> writers;

    public ClientHandler(Socket socket, List<PrintWriter> writers) {
        // quando o número de conexões ativas for 10, o socket é fechado
        if (writers.size() == 10) {
            closeSocket(socket);
        }
        this.socket = socket;
        this.writers = writers;
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Interrompe a thread quando o socket estiver fechado
            if (socket.isClosed() || !socket.isConnected()) {
                this.interrupt();
                return;
            };

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // necessário para que não haja concorrência pelo recurso,
            // writers é compartilhado entre todas as threads dos clients
            synchronized (writers) {
                writers.add(writer);
                System.out.println("Cliente número " + writers.size() + " conectado");
            }

            // loop que fica monitorando a entrada de novas mensagens e faz a transmissão para todos os clients
            String message;
            while((message = reader.readLine()) != null) {
                if (message.equals(ON_CLOSE_MESSAGE)) {
                    System.out.println("Cliente número " + writers.size() + " desconectou-se");
                    writers.remove(writers.size() - 1);
                    this.interrupt();
                    break;
                }
                broadcast(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        synchronized (writers) {
            writers.forEach(writer -> writer.println(message));
        }
    }
}
