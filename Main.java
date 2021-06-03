package trabalho_aed_prontuario.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import trabalho_aed_prontuario.mestre.Prontuario;
import trabalho_aed_prontuario.main.Controlador;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Controlador controlador = new Controlador();
        int opcao = 0;
        Prontuario prontuario;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate data;
        int cpf, num_registro;
        String nome, anotacoes, strData;
        char sexo;

        do {
            System.out.println("   MENU");
            System.out.println("[0] - Sair");
            System.out.println("[1] - Criar arquivo");
            System.out.println("[2] - Inserir registro");
            System.out.println("[3] - Editar registro");
            System.out.println("[4] - Remover registro");
            System.out.println("[5] - Imprimir arquivos");
            System.out.println("[6] - Simulacao");
            System.out.print("Opção: ");

            opcao = in.nextInt();

            switch (opcao) {
                case 0:
                    in.close();
                    System.out.println("Programa encerrado.");
                    break;
                case 1:
                    System.out.print("Qual será a profundidade inicial do hash? ");
                    int profundidade = in.nextInt();

                    System.out.print("Qual será o número de registros por bucket? ");
                    int tam_buckets = in.nextInt();

                    System.out.print("Qual será o tamanho de anotações por registro? ");
                    short tam_anotacoes = in.nextShort();

                    controlador.criarArquivos(profundidade, tam_buckets, tam_anotacoes);
                    break;
                case 2:
                    System.out.print("Qual será o cpf do paciente? ");
                    cpf = in.nextInt();

                    System.out.print("Qual será o nome do paciente? ");
                    nome = in.nextLine();

                    System.out.print("Qual será a data? (Digite no formato dia/mês/ano): ");
                    strData = in.nextLine();

                    if (strData.length() == 10)
                        data = LocalDate.parse(strData, formatter);
                    else
                        data = LocalDate.parse(strData, formatter2);

                    System.out.print("Qual será o sexo do paciente? ");
                    sexo = in.nextLine().charAt(0);

                    System.out.print("Qual será a anotação? ");
                    anotacoes = in.nextLine();

                    prontuario = new Prontuario(cpf, nome, data, sexo, (short) 10, anotacoes);

                    if (controlador.inserirRegistro(prontuario))
                        System.out.println("Prontuário inserido com sucesso.");
                    else
                        System.out.println("Falha ao inserir prontuário.");
                    break;
                case 3:
                    System.out.print("Qual é o cpf vinculado ao prontuário que vai ser modificado? ");
                    cpf = in.nextInt();

                    prontuario = controlador.recuperarRegistro(cpf);

                    if (prontuario == null) {
                        System.out.println("Cpf não cadastrado.");
                        return;
                    }
                    System.out.println("Informações atuais do prontuário: ");
                    System.out.println(prontuario);

                    System.out.println("Qual dos seguintes campos deseja alterar?");
                    System.out.println("[1] - Nome");
                    System.out.println("[2] - Sexo");
                    System.out.println("[3] - Data");
                    System.out.println("[4] - Anotações");
                    System.out.print("Campo: ");
                    opcao = in.nextInt();

                    while (opcao < 1 || opcao > 4) {
                        System.out.print("Campo inválido. Digite novamente o número do campo: ");
                        opcao = in.nextInt();
                    }

                    System.out.print("Escreva o novo valor: "); // problema com anotacoes
                    String valor = in.nextLine();

                    if (controlador.editarRegistro(cpf, opcao, valor) == StatusDeEdicao.TUDO_OK) {
                        System.out.println("Informações do prontuário atualizadas com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar informções do prontuário.");
                    }
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
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }
}
