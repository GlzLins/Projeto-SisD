import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO{
    private Connection conexao;
    private final String URL_BANCO = "jdbc:sqlite:loja_pecas.db";

    public EstoqueDAO() {
        conectar();
        criarTabela();

    }

    // 1. Liga o Java ao Ficheiro do Banco de Dados
    private void conectar() {
        try {
            conexao = DriverManager.getConnection(URL_BANCO);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao Banco de Dados: " + e.getMessage());
        }
    }

    // 2. Cria a Tabela SQL se ela não existir
    private void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS estoque (" +
                     "codigo INTEGER PRIMARY KEY," +
                     "nome TEXT NOT NULL," +
                     "preco REAL NOT NULL" +
                     ");";
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
            
            // Verifica se a tabela está vazia. Se estiver, cria o estoque inicial
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM estoque");
            if (rs.getInt("total") == 0) {
                gerarEstoqueInicial();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    // 3. Lê do Banco de Dados (SELECT)
    public synchronized Peca[] listarEstoque() {
        List<Peca> lista = new ArrayList<>();
        String sql = "SELECT codigo, nome, preco FROM estoque";
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(new Peca(rs.getInt("codigo"), rs.getString("nome"), rs.getDouble("preco")));
            }
        } catch (SQLException e) { }
        
        return lista.toArray(new Peca[0]);
    }

    // 4. Compra (DELETE condicional)
    public synchronized boolean comprarPeca(int codigo) {
        // Primeiro verifica se a peça existe
        String sqlVerifica = "SELECT codigo FROM estoque WHERE codigo = ?";
        String sqlDeleta = "DELETE FROM estoque WHERE codigo = ?";
        
        try (PreparedStatement pstmtVerifica = conexao.prepareStatement(sqlVerifica)) {
            pstmtVerifica.setInt(1, codigo);
            ResultSet rs = pstmtVerifica.executeQuery();
            
            if (rs.next()) { // A peça existe, vamos vendê-la (apagar do banco)
                try (PreparedStatement pstmtDeleta = conexao.prepareStatement(sqlDeleta)) {
                    pstmtDeleta.setInt(1, codigo);
                    pstmtDeleta.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) { }
        return false;
    }

    // 5. Funções do Gerente (INSERT, UPDATE e DELETE)
    public synchronized void adicionarOuEditarPeca(Peca novaPeca) {
        // O comando REPLACE do SQLite insere ou atualiza automaticamente se o código já existir
        String sql = "REPLACE INTO estoque (codigo, nome, preco) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, novaPeca.getCodigo());
            pstmt.setString(2, novaPeca.getNome());
            pstmt.setDouble(3, novaPeca.getPreco());
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    public synchronized void removerPeca(int codigo) {
        String sql = "DELETE FROM estoque WHERE codigo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, codigo);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    // O nosso plano de emergência (agora usando SQL)
    private void gerarEstoqueInicial() {
        String[] nomes = {"Amortecedor Dianteiro", "Pneu Aro 15", "Bateria 60Ah", "Vela de Ignicao", "Filtro de Oleo", "Filtro de Ar", "Pastilha de Freio", "Disco de Freio", "Correia Dentada", "Bomba D'Agua"};
        double[] precos = {250.0, 320.0, 400.0, 45.0, 30.0, 25.0, 80.0, 120.0, 60.0, 150.0};
        
        for (int i = 0; i < 10; i++) {
            adicionarOuEditarPeca(new Peca(i + 1, nomes[i], precos[i]));
        }
    }
}