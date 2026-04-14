import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClienteController {
    private String usuarioLogado = "";
    private final String HOST = "localhost";
    private final int PORTA = 5000;

    private String enviarComandoServidor(String comando) {
        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {
            out.writeUTF(comando);
            return in.readUTF();
        } catch (Exception e) { return "ERRO: Servidor offline."; }
    }

    public String autenticar(String acao, String u, String p) {
        String resposta = enviarComandoServidor(acao + ":" + u + ":" + p);
        if (resposta.startsWith("SUCESSO")) usuarioLogado = u;
        return resposta;
    }

    public String[][] carregarCatalogo() {
        String jsonRecebido = enviarComandoServidor("LISTAR");
        List<String[]> linhas = new ArrayList<>();
        try {
            String json = jsonRecebido.trim();
            if(json.startsWith("[")) json = json.substring(1, json.length() - 1);
            if(!json.isEmpty()) {
                String[] pecas = json.split("\\},\\{");
                for(String p : pecas) {
                    p = p.replace("{", "").replace("}", "").replace("\"", "");
                    String[] atributos = p.split(",");
                    
                    String cod = String.format("%05d", Integer.parseInt(atributos[0].split(":")[1].trim()));
                    String nome = atributos[1].split(":")[1].trim();
                    String preco = atributos[2].split(":")[1].trim();
                    linhas.add(new String[]{cod, nome, preco});
                }
            }
        } catch (Exception e) {}
        return linhas.toArray(new String[0][0]);
    }

    public String finalizarCompra(List<String> ids) {
        StringBuilder idsStr = new StringBuilder();
        for(String id : ids) idsStr.append(id).append(",");
        return enviarComandoServidor("CARRINHO:" + usuarioLogado + ":" + idsStr.toString());
    }

    // ATUALIZADO: Salva na pasta "recibos" e inclui os detalhes da compra!
    public void gerarRecibo(List<String> itens, double total) {
        try {
            // Garante que a pasta existe, mesmo se você esquecer de criar
            File diretorio = new File("recibos");
            if (!diretorio.exists()) diretorio.mkdirs();

            String nomeArquivo = "recibos/recibo_" + usuarioLogado + "_" + System.currentTimeMillis() + ".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo));
            bw.write("==========================================\n");
            bw.write("   RECIBO DE COMPRA - AUTO PECAS EXPRESS  \n");
            bw.write("==========================================\n");
            bw.write("Cliente: " + usuarioLogado + "\n");
            bw.write("Data: " + new java.util.Date().toString() + "\n\n");
            bw.write("ITENS COMPRADOS:\n");
            for(String item : itens) {
                bw.write("- " + item + "\n");
            }
            bw.write("\nTOTAL PAGO: R$ " + String.format("%.2f", total) + "\n");
            bw.write("Status: APROVADO\n");
            bw.write("==========================================\n");
            bw.close();
        } catch (Exception e) {}
    }

    @SuppressWarnings("deprecation")
    public void escutarAvisos(Consumer<String> callbackTela, Runnable callbackAtualizarCatalogo) {
        new Thread(() -> {
            try {
                MulticastSocket socket = new MulticastSocket(4446);
                socket.joinGroup(InetAddress.getByName("230.0.0.0"));
                byte[] buffer = new byte[512];
                while (true) {
                    DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                    socket.receive(pacote);
                    String msg = new String(pacote.getData(), 0, pacote.getLength()).trim();
                    
                    // ATUALIZADO: Identifica se é o Sinal Secreto do Servidor
                    if (msg.equals("CMD:ATUALIZAR_CATALOGO")) {
                        callbackAtualizarCatalogo.run();
                    } else {
                        callbackTela.accept(msg);
                    }
                }
            } catch (Exception e) {}
        }).start();
    }
}