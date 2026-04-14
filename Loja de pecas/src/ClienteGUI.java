import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ClienteGUI extends JFrame {

    private ClienteController controller;

    private JPanel painelPrincipal;
    private CardLayout cardLayout;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private DefaultTableModel modeloCatalogo;
    private JTable tabelaCatalogo;
    private DefaultListModel<String> modeloCarrinho;
    private JList<String> listaCarrinho;
    private JTextField txtCodAdicionar;
    private PainelBolha painelAvisos;
    private JLabel lblTextoBolha;
    
    // NOVO: Label do Total
    private JLabel lblTotalCarrinho;
    private double valorTotal = 0.0;
    private boolean usuarioEstaLogado = false;

    public ClienteGUI() {
        controller = new ClienteController();
        
        setTitle("AutoPeças Express - Área do Cliente");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        
        painelPrincipal.add(criarTelaLogin(), "LOGIN");
        painelPrincipal.add(criarTelaLoja(), "LOJA");
        
        setContentPane(painelPrincipal);
        
        // NOVO: Ação de recarregar catálogo em tempo real
        Runnable atualizarCatalogoSilencioso = () -> {
            if (usuarioEstaLogado) { // Só atualiza a tela se o cliente já tiver feito login
                SwingUtilities.invokeLater(() -> carregarLoja());
            }
        };

        controller.escutarAvisos(msg -> {
            SwingUtilities.invokeLater(() -> {
                painelAvisos.setCorDeFundo(new Color(241, 196, 15));
                lblTextoBolha.setForeground(Color.BLACK);
                lblTextoBolha.setText(msg);
            });
        }, atualizarCatalogoSilencioso);
    }

    private JPanel criarTelaLogin() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(240, 242, 245)); 
        
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));

        JLabel titulo = new JLabel("Acesso à Loja");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblUser = new JLabel("Usuário:");
        txtUser = new JTextField();
        txtUser.setMaximumSize(new Dimension(300, 35));
        
        JLabel lblPass = new JLabel("Senha:");
        txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(300, 35));
        
        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setBackground(Color.WHITE);
        botoes.setMaximumSize(new Dimension(300, 40));
        
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        
        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(46, 204, 113));
        btnCadastrar.setForeground(Color.WHITE);
        
        botoes.add(btnLogin);
        botoes.add(btnCadastrar);

        box.add(titulo);
        box.add(Box.createRigidArea(new Dimension(0, 25)));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT); box.add(lblUser);
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT); box.add(txtUser);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT); box.add(lblPass);
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT); box.add(txtPass);
        box.add(Box.createRigidArea(new Dimension(0, 25)));
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT); box.add(botoes);
        
        painel.add(box);

        btnLogin.addActionListener(e -> autenticar("LOGIN"));
        btnCadastrar.addActionListener(e -> autenticar("CADASTRO"));
        return painel;
    }

    private JPanel criarTelaLoja() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel containerBolha = new JPanel(new FlowLayout(FlowLayout.CENTER));
        containerBolha.setOpaque(false);
        painelAvisos = new PainelBolha(new Color(41, 128, 185));
        lblTextoBolha = new JLabel("Aguardando promoções...", SwingConstants.CENTER);
        lblTextoBolha.setForeground(Color.WHITE);
        lblTextoBolha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelAvisos.add(lblTextoBolha);
        containerBolha.add(painelAvisos);
        painel.add(containerBolha, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65);
        split.setBorder(null);

        // --- ESQUERDA: CATÁLOGO ---
        JPanel painelCat = new JPanel(new BorderLayout(0, 10));
        painelCat.setBorder(BorderFactory.createTitledBorder("Catálogo de Peças"));
        
        JPanel painelBusca = new JPanel(new BorderLayout(5, 0));
        painelBusca.add(new JLabel(" 🔍 Buscar Peça:"), BorderLayout.WEST);
        JTextField txtBusca = new JTextField();
        painelBusca.add(txtBusca, BorderLayout.CENTER);
        painelCat.add(painelBusca, BorderLayout.NORTH);

        modeloCatalogo = new DefaultTableModel(new String[]{"CÓDIGO", "PRODUTO", "PREÇO (R$)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } 
        };
        tabelaCatalogo = new JTable(modeloCatalogo);
        tabelaCatalogo.setRowHeight(28);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloCatalogo);
        tabelaCatalogo.setRowSorter(sorter);
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { pesquisar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { pesquisar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { pesquisar(); }
            private void pesquisar() {
                String text = txtBusca.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
            }
        });

        tabelaCatalogo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linhaSelecionada = tabelaCatalogo.getSelectedRow();
                if (linhaSelecionada != -1) {
                    int modelRow = tabelaCatalogo.convertRowIndexToModel(linhaSelecionada);
                    txtCodAdicionar.setText(modeloCatalogo.getValueAt(modelRow, 0).toString());
                }
            }
        });

        painelCat.add(new JScrollPane(tabelaCatalogo), BorderLayout.CENTER);
        
        JPanel painelAdd = new JPanel(new FlowLayout());
        painelAdd.add(new JLabel("Código:"));
        txtCodAdicionar = new JTextField(6);
        txtCodAdicionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnAdd = new JButton("Adicionar ao Carrinho");
        btnAdd.setBackground(new Color(52, 73, 94));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            String codDigitado = txtCodAdicionar.getText().trim();
            if(!codDigitado.isEmpty()) {
                String nomeProduto = "";
                String preco = "";
                for(int i=0; i<modeloCatalogo.getRowCount(); i++) {
                    if(modeloCatalogo.getValueAt(i, 0).toString().equals(codDigitado)) {
                        nomeProduto = modeloCatalogo.getValueAt(i, 1).toString();
                        preco = modeloCatalogo.getValueAt(i, 2).toString();
                        break;
                    }
                }
                if (!nomeProduto.isEmpty()) {
                    // ATUALIZADO: Guarda o preço no texto do carrinho para somar depois
                    modeloCarrinho.addElement(codDigitado + " - " + nomeProduto + " | R$ " + preco);
                    txtCodAdicionar.setText("");
                    calcularTotalCarrinho();
                } else {
                    JOptionPane.showMessageDialog(this, "Código não encontrado.");
                }
            }
        });
        
        painelAdd.add(txtCodAdicionar);
        painelAdd.add(btnAdd);
        painelCat.add(painelAdd, BorderLayout.SOUTH);

        // --- DIREITA: CARRINHO ---
        JPanel painelCarrinho = new JPanel(new BorderLayout(0, 10));
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("Seu Carrinho"));
        modeloCarrinho = new DefaultListModel<>();
        listaCarrinho = new JList<>(modeloCarrinho);
        painelCarrinho.add(new JScrollPane(listaCarrinho), BorderLayout.CENTER);
        
        // NOVO: Painel para o Botão e Total
        JPanel botoesCart = new JPanel(new BorderLayout(5, 5));
        
        lblTotalCarrinho = new JLabel("Total: R$ 0.00", SwingConstants.CENTER);
        lblTotalCarrinho.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalCarrinho.setForeground(new Color(192, 57, 43)); // Vermelho
        botoesCart.add(lblTotalCarrinho, BorderLayout.NORTH);

        JPanel pnlAcoes = new JPanel(new GridLayout(2,1, 5, 5));
        JButton btnLimpar = new JButton("Remover Item");
        btnLimpar.addActionListener(e -> {
            int idx = listaCarrinho.getSelectedIndex();
            if(idx != -1) {
                modeloCarrinho.remove(idx);
                calcularTotalCarrinho();
            }
        });
        
        JButton btnComprar = new JButton("FINALIZAR COMPRA");
        btnComprar.setBackground(new Color(39, 174, 96));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComprar.addActionListener(e -> finalizarCompraCarrinho());
        
        pnlAcoes.add(btnLimpar);
        pnlAcoes.add(btnComprar);
        botoesCart.add(pnlAcoes, BorderLayout.CENTER);
        
        painelCarrinho.add(botoesCart, BorderLayout.SOUTH);

        split.setLeftComponent(painelCat);
        split.setRightComponent(painelCarrinho);
        painel.add(split, BorderLayout.CENTER);

        return painel;
    }

    // LÓGICA DO TOTAL DO CARRINHO
    private void calcularTotalCarrinho() {
        valorTotal = 0.0;
        for(int i = 0; i < modeloCarrinho.size(); i++) {
            String item = modeloCarrinho.getElementAt(i);
            // Quebra o texto: "00001 - Produto | R$ 250.0" -> Pega a parte final
            String precoStr = item.split("R\\$ ")[1].trim();
            valorTotal += Double.parseDouble(precoStr);
        }
        lblTotalCarrinho.setText("Total: R$ " + String.format("%.2f", valorTotal));
    }

    private void autenticar(String acao) {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword()).trim();
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha!"); return;
        }

        String resposta = controller.autenticar(acao, u, p);
        if(resposta.startsWith("SUCESSO")) {
            JOptionPane.showMessageDialog(this, acao.equals("CADASTRO") ? "Conta criada com sucesso!" : "Bem-vindo(a), " + u + "!");
            if(acao.equals("LOGIN")) {
                usuarioEstaLogado = true;
                carregarLoja();
                cardLayout.show(painelPrincipal, "LOJA"); 
            }
        } else {
            JOptionPane.showMessageDialog(this, resposta, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarLoja() {
        modeloCatalogo.setRowCount(0); 
        String[][] dados = controller.carregarCatalogo();
        for(String[] linha : dados) {
            modeloCatalogo.addRow(linha);
        }
    }

    private void finalizarCompraCarrinho() {
        if(modeloCarrinho.isEmpty()) return;
        
        List<String> idsParaComprar = new ArrayList<>();
        List<String> nomesParaRecibo = new ArrayList<>();
        
        for(int i=0; i < modeloCarrinho.size(); i++) {
            String item = modeloCarrinho.getElementAt(i);
            idsParaComprar.add(item.split(" - ")[0]); // Extrai o ID
            nomesParaRecibo.add(item); // Salva o nome completo para o txt
        }
        
        String resposta = controller.finalizarCompra(idsParaComprar);
        
        if(resposta.startsWith("SUCESSO")) {
            JOptionPane.showMessageDialog(this, "Compra Aprovada!\nRecibo salvo na pasta 'recibos'.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            controller.gerarRecibo(nomesParaRecibo, valorTotal);
            modeloCarrinho.clear(); 
            calcularTotalCarrinho();
            carregarLoja(); 
        } else {
            JOptionPane.showMessageDialog(this, resposta, "Erro na Compra", JOptionPane.ERROR_MESSAGE);
        }
    }

    class PainelBolha extends JPanel {
        private Color corDeFundo;
        public PainelBolha(Color cor) {
            this.corDeFundo = cor;
            setOpaque(false);
            setBorder(new EmptyBorder(8, 20, 8, 20));
        }
        public void setCorDeFundo(Color cor) {
            this.corDeFundo = cor;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(corDeFundo);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30)); 
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        // FORÇA O TEMA NIMBUS (Resolve o problema da tela preta do Linux)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
    }
}