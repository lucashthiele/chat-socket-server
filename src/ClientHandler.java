import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final List<PrintWriter> writers;

    public ClientHandler(Socket socket, List<PrintWriter> writers) {
        this.socket = socket;
        this.writers = writers;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            synchronized (writers) {
                writers.add(writer);
            }

            String mensagem;
            while((mensagem = reader.readLine()) != null) {
                broadcast(mensagem);
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
