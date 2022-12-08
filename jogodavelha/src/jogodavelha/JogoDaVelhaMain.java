package jogodavelha;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JogoDaVelhaMain extends Thread {

    private List<JogoDaVelhaServerConnection> clientes;
    private ServerSocket server;
    
    public String[] board = new String[9];
    public String turno = "X";
    public int pontuacaoX;
    public int pontuacaoO;

    public JogoDaVelhaMain(int porta) throws IOException {
        this.server = new ServerSocket(porta);
        System.out.println(this.getClass().getSimpleName() + " rodando na porta: " + server.getLocalPort());
        this.clientes = new ArrayList<>();
        
        for (int a = 0; a < 9; a++) {
            board[a] = String.valueOf(a + 1);
        }
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            try {
                socket = this.server.accept();
                JogoDaVelhaServerConnection cliente = new JogoDaVelhaServerConnection(socket);
                novoCliente(cliente);
                (new JogoDaVelhaServerHandler(cliente, this)).start();
            } catch (IOException ex) {
                System.out.println("Erro 4: " + ex.getMessage());
            }
        }
    }

    public synchronized void novoCliente(JogoDaVelhaServerConnection cliente) throws IOException {
        clientes.add(cliente);
        
        if (clientes.size() == 1) {
            cliente.getOutput().println("player" + "|" + "X");
        } else {
            cliente.getOutput().println("player" + "|" + "O");
        }
    }

    public synchronized void removerCliente(JogoDaVelhaServerConnection cliente) {
        clientes.remove(cliente);
        
        try {
            cliente.getInput().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        cliente.getOutput().close();
        try {
            cliente.getSocket().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List getClientes() {
        return clientes;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }


}
