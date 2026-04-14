public interface ServicoPecas {
    Peca[] listarEstoque();
    boolean comprarPeca(int codigo);
}