package jogodavelha;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class JogoDaVelhaServerHandler extends Thread {

    private JogoDaVelhaServerConnection cliente;
    private JogoDaVelhaMain caller;

    public JogoDaVelhaServerHandler(JogoDaVelhaServerConnection cliente, JogoDaVelhaMain caller) throws IOException {
        this.cliente = cliente;
        this.caller = caller;
    }

    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.caller.removerCliente(this.cliente);
    }
    
    private String verificaVencedor() {
        for (int a = 0; a < 8; a++) {
            String line = null;
 
            switch (a) {
            case 0:
                line = this.caller.board[0] + this.caller.board[1] + this.caller.board[2];
                break;
            case 1:
                line = this.caller.board[3] + this.caller.board[4] + this.caller.board[5];
                break;
            case 2:
                line = this.caller.board[6] + this.caller.board[7] + this.caller.board[8];
                break;
            case 3:
                line = this.caller.board[0] + this.caller.board[3] + this.caller.board[6];
                break;
            case 4:
                line = this.caller.board[1] + this.caller.board[4] + this.caller.board[7];
                break;
            case 5:
                line = this.caller.board[2] + this.caller.board[5] + this.caller.board[8];
                break;
            case 6:
                line = this.caller.board[0] + this.caller.board[4] + this.caller.board[8];
                break;
            case 7:
                line = this.caller.board[2] + this.caller.board[4] + this.caller.board[6];
                break;
            }
      
            if ("XXX".equals(line)) {
                return "X";
            } else if ("OOO".equals(line)) {
                return "O";
            }
        }
         
        for (int a = 0; a < 9; a++) {
            if (Arrays.asList(this.caller.board).contains(String.valueOf(a + 1))) {
                break;
            } else if (a == 8) {
                return "draw";
            }
        }
        
        return null;
    }

    public synchronized void messageDispatcher(String message) throws IOException {
        List<JogoDaVelhaServerConnection> clientes = this.caller.getClientes();
        for (JogoDaVelhaServerConnection cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getOutput() != null) {
                cli.getOutput().println(message);
                cli.getOutput().flush();
            }
        }
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.cliente.getSocket().isConnected() && this.cliente.getInput() != null) {
                    message = this.cliente.getInput().readLine();
                } else {
                    break;
                }
                
                if (message == null || message.equals("")) {
                    break;
                }
                
                StringTokenizer tokens = new StringTokenizer(message, "|");

                String jogada = tokens.nextToken();

                if (jogada.equals("X") || jogada.equals("O")) {
                   int posicao = Integer.parseInt(tokens.nextToken());

                    this.caller.board[posicao] = jogada;
                    this.caller.turno = this.caller.turno.equals("X") ? "O" : "X";

                    String vencedor = verificaVencedor();

                    if (vencedor != null) {
                       switch (vencedor) {
                           case "X":
                               this.caller.pontuacaoX++;
                               break;
                           case "O":
                               this.caller.pontuacaoO++;
                               break;
                           default:
                               this.caller.pontuacaoX++;
                               this.caller.pontuacaoO++;
                               break;
                       }
                       
                       this.caller.turno = vencedor.equals("draw") || vencedor.equals("X") ? "X" : "O";
  
                       message = "final" + "|" + String.valueOf(this.caller.pontuacaoX) + "|" + String.valueOf(this.caller.pontuacaoO) + "|" + this.caller.turno; 
               
                       for (int a = 0; a < 9; a++) {
                            this.caller.board[a] = String.valueOf(a + 1);
                       }
                    } else {
                       message = "jogada" + "|" + jogada + "|" + String.valueOf(posicao) + "|" + this.caller.turno;
                    }                  
                }
                
                messageDispatcher(message); 
                System.out.println(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        
        encerrar();
    }
}
