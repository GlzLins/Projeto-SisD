import java.net.*;

public class GerenteAvisosUDP {
    public static void main(String[] args) {
        try {
            InetAddress grupo = InetAddress.getByName("230.0.0.0");
            DatagramSocket socket = new DatagramSocket();

            String aviso = "MENSAGEM DO GERENTE: Promoção de Óleo de Motor nos próximos 30 minutos!";
            byte[] msgBytes = aviso.getBytes();

            // Envia o pacote UDP via Multicast
            DatagramPacket pacote = new DatagramPacket(msgBytes, msgBytes.length, grupo, 4446);
            socket.send(pacote);
            
            System.out.println("Aviso Multicast disparado para todos os clientes.");
            socket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}