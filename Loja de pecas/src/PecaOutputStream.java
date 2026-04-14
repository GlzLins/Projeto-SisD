import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PecaOutputStream extends OutputStream {
    private DataOutputStream dos;

    // O construtor atende às regras i, ii e iv do PDF
    public PecaOutputStream(Peca[] dados, int quantidade, OutputStream destino) throws IOException {
        this.dos = new DataOutputStream(destino);
        this.dos.writeInt(quantidade); // Grava a quantidade total
        
        for (int i = 0; i < quantidade; i++) {
            // Grava os 3 atributos (Regra iii do PDF)
            this.dos.writeInt(dados[i].getCodigo());
            this.dos.writeUTF(dados[i].getNome());
            this.dos.writeDouble(dados[i].getPreco());
        }
    }

    @Override
    public void write(int b) throws IOException {
        dos.write(b);
    }
}