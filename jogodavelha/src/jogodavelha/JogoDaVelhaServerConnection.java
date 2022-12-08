package jogodavelha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JogoDaVelhaServerConnection {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public JogoDaVelhaServerConnection(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.input.close();
        this.output.close();
        this.socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }

}
