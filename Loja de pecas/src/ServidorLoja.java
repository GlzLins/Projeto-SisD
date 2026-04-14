import java.io.*;
import java.net.*;

public class ServidorLoja {
    private static EstoqueDAO EstoqueDAO = new EstoqueDAO();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(5000)) {
            System.out.println("=== Servidor da Loja Iniciado (Porta 5000) ===");
            
            // Servidor Multi-threaded
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Novo cliente conectado: " + cliente.getInetAddress());
                new Thread(new TratadorCliente(cliente, EstoqueDAO)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TratadorCliente implements Runnable {
    private Socket socket;
    private EstoqueDAO EstoqueDAO;

    public TratadorCliente(Socket socket, EstoqueDAO EstoqueDAO) {
        this.socket = socket;
        this.EstoqueDAO = EstoqueDAO;
    }

    @Override
    public void run() {
        try {
            DataInputStream entradaCmd = new DataInputStream(socket.getInputStream());
            DataOutputStream saidaCmd = new DataOutputStream(socket.getOutputStream());

            // 1. Recebe e desempacota o comando do cliente
            String comando = entradaCmd.readUTF(); 

            if (comando.equals("LISTAR")) {
                // 2. Empacota a resposta usando nosso Stream customizado
                Peca[] estoque = EstoqueDAO.listarEstoque();
                new PecaOutputStream(estoque, estoque.length, socket.getOutputStream());
            } 
            
            socket.close();
        } catch (IOException e) {
            System.out.println("Conexão encerrada.");
        }
    }
}