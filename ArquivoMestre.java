package trabalho_aed_prontuario.mestre;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.EOFException;

public class ArquivoMestre {
    public ArquivoMestre() {
        try {
            FileOutputStream fos = new FileOutputStream("arquivo_mestre.db");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
