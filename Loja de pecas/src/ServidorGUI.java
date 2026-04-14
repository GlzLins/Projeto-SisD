import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServidorGUI extends JFrame {

    private JTextArea areaLogs;
    private JButton btnLigarServidor;
    private JTextField txtAvisoMulticast;
    private DefaultTableModel modeloEstoque;
    private JTable tabelaEstoque;
    private JTextField txtCod, txtNome, txtPreco;
    private EstoqueDAO deposito;
    private boolean servidorRodando = false;
    private ServerSocket serverSocket;
    private final String ARQUIVO_USUARIOS = "usuarios.txt";

    public ServidorGUI() {
        deposito = new EstoqueDAO();
        initUI();
        iniciarServidorTCP();
    }

    private void initUI() {
        setTitle("AutoPeças Express - Painel do Gerente");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Servidor & Logs", criarAbaLogs());
        abas.addTab("Gerenciar Estoque", criarAbaEstoque());
        abas.addTab("Avisos (Multicast)", criarAbaAvisos());

        setContentPane(abas);
    }

    private JPanel criarAbaLogs() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(44, 62, 80));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        btnLigarServidor = new JButton("Servidor TCP Online");
        btnLigarServidor.setBackground(new Color(46, 204, 113));
        btnLigarServidor.setForeground(Color.WHITE);
        btnLigarServidor.setEnabled(false);
        painel.add(btnLigarServidor, BorderLayout.NORTH);

        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        areaLogs.setBackground(Color.BLACK);
        areaLogs.setForeground(new Color(0, 255, 0));
        areaLogs.setFont(new Font("Consolas", Font.PLAIN, 14));
        painel.add(new JScrollPane(areaLogs), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarAbaEstoque() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloEstoque = new DefaultTableModel(new String[]{"Código", "Produto", "Preço (R$)"}, 0);
        tabelaEstoque = new JTable(modeloEstoque);
        tabelaEstoque.setRowHeight(25);
        painel.add(new JScrollPane(tabelaEstoque), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout());
        form.add(new JLabel("Cód:"));
        txtCod = new JTextField(5);
        form.add(txtCod);
        
        form.add(new JLabel("Nome:"));
        txtNome = new JTextField(15);
        form.add(txtNome);
        
        form.add(new JLabel("Preço:"));
        txtPreco = new JTextField(7);
        form.add(txtPreco);

        JButton btnSalvar = new JButton("Salvar/Editar");
        btnSalvar.addActionListener(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText());
                double preco = Double.parseDouble(txtPreco.getText());
                deposito.adicionarOuEditarPeca(new Peca(cod, txtNome.getText(), preco));
                atualizarTabelaEstoque();
                registrarLog("[GERENTE] Adicionou/Editou a peça: " + txtNome.getText());
                txtCod.setText(""); txtNome.setText(""); txtPreco.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro nos dados!"); }
        });

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText());
                deposito.removerPeca(cod);
                atualizarTabelaEstoque();
                registrarLog("[GERENTE] Removeu a peça Cód: " + cod);
                txtCod.setText("");
            } catch (Exception ex) { }
        });

        form.add(btnSalvar);
        form.add(btnExcluir);
        painel.add(form, BorderLayout.SOUTH);

        atualizarTabelaEstoqueSilencioso();
        
        tabelaEstoque.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelaEstoque.getSelectedRow();
                if(row != -1) {
                    txtCod.setText(modeloEstoque.getValueAt(row, 0).toString());
                    txtNome.setText(modeloEstoque.getValueAt(row, 1).toString());
                    txtPreco.setText(modeloEstoque.getValueAt(row, 2).toString());
                }
            }
        });

        return painel;
    }

    // Atualiza a tabela do gerente e manda os clientes atualizarem também!
    private void atualizarTabelaEstoque() {
        atualizarTabelaEstoqueSilencioso();
        dispararSinalAtualizacaoUDP();
    }

    private void atualizarTabelaEstoqueSilencioso() {
        modeloEstoque.setRowCount(0);
        for(Peca p : deposito.listarEstoque()) {
            modeloEstoque.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getPreco()});
        }
    }

    private JPanel criarAbaAvisos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        txtAvisoMulticast = new JTextField("Promoção: 20% de desconto agora!");
        txtAvisoMulticast.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JButton btnAviso = new JButton("Disparar Bolha Multicast");
        btnAviso.setBackground(new Color(243, 156, 18));
        btnAviso.setForeground(Color.WHITE);
        btnAviso.setFont(new Font("Arial", Font.BOLD, 16));
        btnAviso.addActionListener(e -> {
            enviarMensagemMulticast(txtAvisoMulticast.getText());
            registrarLog("[GERENTE] Disparou aviso geral: " + txtAvisoMulticast.getText());
        });

        painel.add(new JLabel("Digite a mensagem que aparecerá na tela dos clientes:"), BorderLayout.NORTH);
        painel.add(txtAvisoMulticast, BorderLayout.CENTER);
        painel.add(btnAviso, BorderLayout.SOUTH);
        return painel;
    }

    private void registrarLog(String msg) {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            areaLogs.append("[" + hora + "] " + msg + "\n");
            areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
        });
    }

    private synchronized boolean gerenciarUsuario(String acao, String user, String pass) {
        String senhaHashed = Seguranca.gerarHash(pass); 
        File arquivo = new File(ARQUIVO_USUARIOS);
        try {
            if (!arquivo.exists()) arquivo.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes[0].equals(user)) {
                    br.close();
                    if (acao.equals("CADASTRO")) return false; 
                    if (acao.equals("LOGIN")) return partes[1].equals(senhaHashed); 
                }
            }
            br.close();

            if (acao.equals("CADASTRO")) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true));
                bw.write(user + ";" + senhaHashed + "\n");
                bw.close();
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    private void iniciarServidorTCP() {
        if (servidorRodando) return;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                servidorRodando = true;
                registrarLog("Servidor TCP Online na porta 5000.");
                while (servidorRodando) {
                    Socket cliente = serverSocket.accept();
                    new Thread(new TratadorClienteGUI(cliente, deposito, this)).start();
                }
            } catch (Exception e) {}
        }).start();
    }

    // Envia Mensagem de Texto pro Rádio UDP
    private void enviarMensagemMulticast(String mensagem) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] msgBytes = mensagem.getBytes();
            socket.send(new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getByName("230.0.0.0"), 4446));
            socket.close();
        } catch (Exception e) {}
    }

    // Manda o "Sinal Secreto" para os clientes atualizarem a tabela
    private void dispararSinalAtualizacaoUDP() {
        enviarMensagemMulticast("CMD:ATUALIZAR_CATALOGO");
    }

    public static void main(String[] args) {
        // Tema bonitinho pro servidor também
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ServidorGUI().setVisible(true));
    }

    // --- TRATADOR TCP COM LOGS AVANÇADOS ---
    class TratadorClienteGUI implements Runnable {
        private Socket socket;
        private EstoqueDAO deposito;
        private ServidorGUI gui;

        public TratadorClienteGUI(Socket socket, EstoqueDAO deposito, ServidorGUI gui) {
            this.socket = socket; this.deposito = deposito; this.gui = gui;
        }

        @Override
        public void run() {
            try {
                String ip = socket.getInetAddress().getHostAddress();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String comando = in.readUTF();

                if (comando.startsWith("CADASTRO:")) {
                    String[] p = comando.split(":");
                    boolean ok = gui.gerenciarUsuario("CADASTRO", p[1], p[2]);
                    out.writeUTF(ok ? "SUCESSO" : "ERRO: Usuário já existe.");
                    gui.registrarLog("[CLIENTE " + ip + "] Tentou cadastrar o usuário: " + p[1] + (ok ? " (Aprovado)" : " (Negado)"));
                } 
                else if (comando.startsWith("LOGIN:")) {
                    String[] p = comando.split(":");
                    boolean ok = gui.gerenciarUsuario("LOGIN", p[1], p[2]);
                    out.writeUTF(ok ? "SUCESSO" : "ERRO: Usuário ou senha inválidos.");
                    gui.registrarLog("[CLIENTE " + ip + "] Efetuou login com usuário: " + p[1] + (ok ? " (Aprovado)" : " (Negado)"));
                }
                else if (comando.equals("LISTAR")) {
                    Peca[] estoque = deposito.listarEstoque();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < estoque.length; i++) {
                        json.append(estoque[i].toJson());
                        if (i < estoque.length - 1) json.append(",");
                    }
                    json.append("]");
                    out.writeUTF(json.toString());
                    gui.registrarLog("[SISTEMA] Enviou o Catálogo de Peças Atualizado para IP: " + ip);
                } 
                else if (comando.startsWith("CARRINHO:")) {
                    String[] partes = comando.split(":");
                    String usuario = partes[1];
                    String[] ids = partes[2].split(",");
                    
                    int comprados = 0;
                    for (String idStr : ids) {
                        if (deposito.comprarPeca(Integer.parseInt(idStr))) comprados++;
                    }
                    out.writeUTF("SUCESSO: " + comprados + " peças compradas com sucesso!");
                    gui.registrarLog("[VENDA] Cliente '" + usuario + "' (" + ip + ") finalizou compra de " + comprados + " itens do carrinho.");
                    
                    // Dispara a atualização na tela do gerente e nas telas de todos os clientes!
                    gui.atualizarTabelaEstoque();
                }
                socket.close();
            } catch (Exception e) {}
        }
    }
}