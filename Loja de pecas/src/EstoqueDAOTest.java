import org.junit.Test;
import static org.junit.Assert.*;

public class EstoqueDAOTest {

    @Test
    public void testIntegracaoBancoDeDados() {
        System.out.println("Iniciando Teste: Adicionar, Listar e Remover Peça...");
        
        EstoqueDAO dao = new EstoqueDAO();
        
        // Criamos uma peça com um código "impossível" (9999) para não bagunçar sua loja real
        int codTeste = 9999;
        Peca pecaTeste = new Peca(codTeste, "Motor de Dobra Espacial (Teste)", 5000.0);

        // --- 1. TESTA A INSERÇÃO (CREATE) ---
        dao.adicionarOuEditarPeca(pecaTeste);

        // --- 2. TESTA A LEITURA (READ) ---
        boolean encontrou = false;
        for (Peca p : dao.listarEstoque()) {
            if (p.getCodigo() == codTeste) {
                encontrou = true;
                // Assert = "Afirme que". Se isso não for verdade, o teste falha na hora!
                assertEquals("Motor de Dobra Espacial (Teste)", p.getNome());
                assertEquals(5000.0, p.getPreco(), 0.01); 
                break;
            }
        }
        // Confirma que a peça realmente foi salva e lida do banco de dados (loja_pecas.db)
        assertTrue("ERRO: A peça não foi salva no banco de dados!", encontrou);


        // --- 3. TESTA A REMOÇÃO (DELETE) ---
        dao.removerPeca(codTeste);

        // --- 4. VERIFICA SE SUMIU ---
        boolean aindaExiste = false;
        for (Peca p : dao.listarEstoque()) {
            if (p.getCodigo() == codTeste) {
                aindaExiste = true;
                break;
            }
        }
        // Se aindaExiste for true, o teste reprova! O correto é ser false.
        assertFalse("ERRO: A peça não foi deletada do banco de dados!", aindaExiste);
        
        System.out.println("Sucesso! O EstoqueDAO está lendo e gravando perfeitamente.");
    }
}