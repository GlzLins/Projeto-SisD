import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PecaInputStream extends InputStream {
    private DataInputStream dis;

    // Construtor recebe o InputStream de origem
    public PecaInputStream(InputStream origem) {
        this.dis = new DataInputStream(origem);
    }

    public Peca[] lerPecas() throws IOException {
        int quantidade = dis.readInt();
        Peca[] pecas = new Peca[quantidade];

        for (int i = 0; i < quantidade; i++) {
            int codigo = dis.readInt();
            String nome = dis.readUTF();
            double preco = dis.readDouble();
            pecas[i] = new Peca(codigo, nome, preco);
        }
        return pecas;
    }

    @Override
    public int read() throws IOException {
        return dis.read();
    }
}