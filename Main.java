package trabalho_aed_prontuario.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import trabalho_aed_prontuario.mestre.Prontuario;
import trabalho_aed_prontuario.main.Controlador;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int opcao;

        Controlador controlador = new Controlador();

        System.out.println("MENU");
        System.out.println("[1] - Criar arquivo");
        System.out.println("[2] - Inserir registro");
        System.out.println("[3] - Editar registro");
        System.out.println("[4] - Remover registro");
        System.out.println("[5] - Imprimir arquivos");
        System.out.println("[6] - Simulacao");
        System.out.print("Opção: ");

        opcao = in.nextInt();

        Prontuario prontuario;
        DateTimeFormatter formatter;

        LocalDate data;
        int cpf, num_registro;
        String nome, anotacoes, strData;
        char sexo;

        switch(opcao) {
            case 1:
                System.out.println("Qual será a profundidade inicial do hash? ");
                int profundidade = in.nextInt();

                System.out.println("Qual será o número de registros por bucket? ");
                int tam_buckets = in.nextInt();

                System.out.println("Qual será o tamanho de anotações por registro? ");
                short tam_anotacoes = in.nextShort();

                controlador.criarArquivos(profundidade, tam_buckets, tam_anotacoes);
                break;
            case 2:
                System.out.print("Qual será o cpf do paciente? ");
                cpf = in.nextInt();

                System.out.print("Qual será o nome do paciente? ");
                nome = in.next();

                System.out.print("Qual será a data? ");
                strData = in.next();
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                data = LocalDate.parse(strData, formatter);

                System.out.print("Qual será o sexo do paciente? ");
                sexo = in.next().charAt(0);

                in.nextLine();
                System.out.print("Qual será a anotação? ");
                anotacoes = in.nextLine();

                prontuario = new Prontuario(cpf, nome, data, sexo, (short)10, anotacoes);
                controlador.inserirRegistro(prontuario);
                break;
            case 3:
                System.out.print("Qual será o cpf do paciente a ser modificado? ");
                cpf = in.nextInt();

                prontuario = controlador.recuperarRegistro(cpf);
                System.out.println("Informações atuais do prontuário: " + prontuario);

                System.out.print("Qual dos seguintes campos deseja alterar? [1] Nome | [2] Sexo | [3] Data | [4] Anotações: ");
                opcao = in.nextInt();

                // in.nextLine();
                System.out.println("Escreva o novo valor: "); // problema com anotacoes
                String valor = in.nextLine();

                controlador.editarRegistro(cpf, opcao, valor);
                break;
            case 4:
                // remover registro
                controlador.removerRegistro();
                break;
            case 5:
                // imprimir arquivos
                controlador.imprimirArquivos();
                break;
            case 6:
                // simulacao
                break;
            default:
                break;
        }
    }
}
