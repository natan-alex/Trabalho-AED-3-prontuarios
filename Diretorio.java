package trabalho_aed_prontuario;

import java.io.*;

public class Diretorio {
    private int profundidade;
    private String arquivo;

    public Diretorio(int profundidade, String arquivo) {
        this.profundidade = profundidade;
        this.arquivo = arquivo;
    }

    public void setHeaders() {

        try {
            InputStream inputStream = new FileInputStream(this.arquivo);
            System.out.println(inputStream.read());

            OutputStream outputStream = new FileOutputStream(this.arquivo);
            outputStream.write(profundidade);
            outputStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

/*
  O  diretório  é  um  arquivo  de  inteiros  de  4  bytes,  cujos  valores  apontam  para  o  número  de  uma página  do  arquivo  de  índices  (começando  por  0). O  valor  da  profundidade  global  (p)  deve  ser armazenado  no  início  do  arquivo.  Durante  o  processamento,  odiretório deve ficarsempre carregado  em  memóriaprimáriamas  será  atualizado  em  disco  sempre  que  houver  alguma mudança  em  sua  estrutura  e/ou  conteúdo.A  profundidade  global  inicial  deve  ser  parâmetro  para do programa.
*/
