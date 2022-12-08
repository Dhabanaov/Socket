package jogodavelha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class JogoDaVelhaClientHandler extends Thread {

    private Socket socket;
    private JogoDaVelhaClient caller;
    private BufferedReader input;

    public JogoDaVelhaClientHandler(Socket socket, JogoDaVelhaClient caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {                
                if (this.socket.isConnected() && this.input != null) {
                    message = this.input.readLine();
                } else {
                    break;
                }
                
                if (message == null || message.equals("")) {
                    break;
                }
                
                StringTokenizer tokens = new StringTokenizer(message, "|");
                String resposta = tokens.nextToken();        
                
                if (resposta.equals("player")) {                    
                    this.caller.player = tokens.nextToken();
                    
                    this.caller.iniciar();
                }
                
                if (resposta.equals("jogada")) {
                    String jogada = tokens.nextToken();
                    int posicao = Integer.parseInt(tokens.nextToken());
                    String turno = tokens.nextToken();
                    this.caller.board[posicao] = jogada;
                    this.caller.turno = turno;
                }
                
                if (resposta.equals("final")) {
                    int pontuacaoX = Integer.parseInt(tokens.nextToken());
                    int pontuacaoO = Integer.parseInt(tokens.nextToken());
                    String turno = tokens.nextToken();          

                    this.caller.turno = turno;
                    
                    for (int a = 0; a < 9; a++) {
                        this.caller.board[a] = String.valueOf(a + 1);
                    }
                    
                    this.caller.reiniciar(pontuacaoX, pontuacaoO);
                }
                
                caller.repaint();
            } catch (Exception ex) {
                System.out.println("Deu erro no cliente");
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}
