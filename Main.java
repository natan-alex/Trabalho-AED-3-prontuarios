package trabalho_aed_prontuario;

import java.time.LocalDate;
import java.util.Scanner;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.EOFException;

@SuppressWarnings("unchecked")
public class Main {
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static FileOutputStream fos;
    private static FileInputStream fis;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int opcao;

        try {
            fos = new FileOutputStream("arquivo_mestre.db");
            oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("MENU");
        System.out.println("[1] - Criar arquivo");
        System.out.println("[2] - Inserir registro");
        System.out.println("[3] - Editar registro");
        System.out.println("[4] - Remover registro");
        System.out.println("[5] - Imprimir arquivos");
        System.out.println("[6] - Simulacao");
        System.out.print("Opção: ");

        opcao = in.nextInt();

        switch(opcao) {
            case 1:
                Diretorio d = new Diretorio(5, "diretorio.db");
                d.setHeaders();
                // criar arquivo
                break;
            case 2:
                // inserir registro
                Prontuario p = new Prontuario("fulano", LocalDate.now(), 'm', "teste");
                try {
                    oos.writeObject(p);
                    fis = new FileInputStream("arquivo_mestre.db");
                    ois = new ObjectInputStream(fis);
                    Prontuario p2 = (Prontuario) ois.readObject();
                    System.out.println("lido: " + p2);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                // editar registro
                break;
            case 4:
                // remover registro
                break;
            case 5:
                // imprimir arquivos
                break;
            case 6:
                // simulacao
                break;
            default: 
                break;
        }
    }
}
