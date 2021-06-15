package trabalho_aed_prontuario.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import trabalho_aed_prontuario.indice.StatusDeInsercao;
import trabalho_aed_prontuario.mestre.Prontuario;
import trabalho_aed_prontuario.main.Controlador;

public class Main {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        final Controlador controlador = new Controlador();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yy");

        int cpf;
        String nome, anotacoes, strData;
        char sexo;
        LocalDate data;
        int opcao;
        Prontuario prontuario;

        int profundidade = -1, tam_buckets = -1;
        short tam_anotacoes = -1;

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
            opcao = Integer.parseInt(in.nextLine());

            switch (opcao) {
                case 0:
                    in.close();
                    System.out.println("Programa encerrado.");
                    break;
                case 1:
                    System.out.print("Qual será a profundidade inicial do hash? ");
                    profundidade = Integer.parseInt(in.nextLine());

                    System.out.print("Qual será o número de registros por bucket? ");
                    tam_buckets = Integer.parseInt(in.nextLine());

                    System.out.print("Qual será o tamanho de anotações por registro? ");
                    tam_anotacoes = Short.parseShort(in.nextLine());

                    if (controlador.criarArquivos(profundidade, tam_buckets, tam_anotacoes))
                        System.out.println("Arquivos criados com sucesso.");
                    else
                        System.out.println("Falha ao criar arquivos.");
                    break;
                case 2:
                    System.out.print("Qual será o cpf do paciente? ");
                    String lido = in.nextLine();
                    cpf = Integer.parseInt(lido);

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

                    prontuario = new Prontuario(cpf, nome, data, sexo, anotacoes);

                    if (controlador.inserirRegistro(prontuario) == StatusDeInsercao.TUDO_OK)
                        System.out.println("Prontuário inserido com sucesso.");
                    else
                        System.out.println("Falha ao inserir prontuário.");
                    break;
                case 3:
                    System.out.print("Qual é o cpf vinculado ao prontuário que vai ser modificado? ");
                    cpf = Integer.parseInt(in.nextLine());

                    prontuario = controlador.recuperarRegistro(cpf);

                    if (prontuario == null) {
                        System.out.println("Cpf não cadastrado.");
                        break;
                    }

                    System.out.println("Informações atuais do prontuário: ");
                    System.out.println(prontuario);

                    System.out.println("Qual dos seguintes campos deseja alterar?");
                    System.out.println("[1] - Nome");
                    System.out.println("[2] - Sexo");
                    System.out.println("[3] - Data");
                    System.out.println("[4] - Anotações");
                    System.out.print("Campo: ");
                    opcao = Integer.parseInt(in.nextLine());

                    while (opcao < 1 || opcao > 4) {
                        System.out.print("Campo inválido. Digite novamente o número do campo: ");
                        opcao = Integer.parseInt(in.nextLine());
                    }

                    System.out.print("Escreva o novo valor: "); // problema com anotacoes
                    String valor = in.nextLine();

                    if (controlador.editarRegistro(prontuario, opcao, valor) == StatusDeEdicao.TUDO_OK) {
                        System.out.println("Informações do prontuário atualizadas com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar informções do prontuário.");
                    }
                    break;
                case 4:
                    System.out.print("Qual será o cpf do paciente a ser removido? ");
                    opcao = Integer.parseInt(in.nextLine());

                    if (controlador.removerRegistro(opcao) == StatusDeRemocao.TUDO_OK)
                        System.out.println("Prontuário removido com sucesso.");
                    else
                        System.out.println("Falha ao remover prontuário.");
                    break;
                case 5:
                    // imprimir arquivos
                    controlador.imprimirArquivos();
                    break;
                case 6:
                    System.out.print("Qual será o número de chaves? ");
                    int numero_chaves = Integer.parseInt(in.nextLine());

                    System.out.print("Qual será a profundidade inicial do hash? ");
                    profundidade = Integer.parseInt(in.nextLine());

                    System.out.print("Qual será o número de registros por bucket? ");
                    tam_buckets = Integer.parseInt(in.nextLine());

                    System.out.print("Qual será o tamanho de anotações por registro? ");
                    tam_anotacoes = Short.parseShort(in.nextLine());

                    controlador.criarArquivos(profundidade, tam_buckets, tam_anotacoes);
                    controlador.simular(numero_chaves);
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }
}
