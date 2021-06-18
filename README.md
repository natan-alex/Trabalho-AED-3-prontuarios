# Trabalho-AED-3-prontuarios
## Descrição
Implementar um sistema de cadastro de prontuários para uma empresa de planos
de saúde, com opções de inserção, alteração, exclusão e impressão.
A organização do arquivo de dados deve ser indexado, com índice baseado em hashing dinâmico.
O sistema deverá oferecer uma tela inicial com um menu de opções:
1. Criar arquivo: Cria um novo sistema de arquivos constituído por um diretório, um índice e um
arquivo-mestre.
a) O diretório é um arquivo de inteiros de 4 bytes, cujos valores apontam para o número de uma
página do arquivo de índices (começando por 0). O valor da profundidade global (p) deve ser
armazenado no início do arquivo. Durante o processamento, o diretório deve ficar sempre
carregado em memória primária mas será atualizado em disco sempre que houver alguma
mudança em sua estrutura e/ou conteúdo. A profundidade global inicial deve ser parâmetro para
do programa.
b) O índice é um arquivo de inteiros de 4 bytes, organizado por buckets. Cada bucket contém o valor
de profundidade local (p’) seguido por n entradas, as quais são constituídas por um CPF e o
número do registro no arquivo-mestre onde o registro correspondente ao CPF se encontra
(começando por 0). Por simplicidade, considere que o número de CPF é um inteiro que varia de 1
a 999999999. O valor -1 indica que a entrada está desocupada. O número de entradas por bucket
deve ser um parâmetro do programa.
c) O arquivo-mestre é um arquivo de registros de tamanho fixo, contendo no mínimo os campos para
nome, data de nascimento, sexo e uma área de m caracteres/bytes para anotações do médico. O
valor de m é um parâmetro do programa.
2. Inserir registro: Insere um novo registro, através da entrada dos valores dos campos, exceto o campo
de anotações. Os arquivos são atualizados segundo a técnica de hash dinâmico. O tempo decorrido no
processo (sem envolver a interação do usuário) deve ser medido e exibido na tela.
3. Editar registro: Compreende uma busca ao registro a partir do CPF e a edição do campo de
anotações. O tempo decorrido no processo de pesquisa e recuperação do registro (sem envolver a
interação do usuário) deve ser medido e exibido na tela.
4. Remover registro: Remove o registro a partir do número do CPF. A remoção é sempre lógica,
zerando-se o CPF no bucket correspondente. O espaço do registro do arquivo-mestre não deve ser
excluído e pode ser reutilizado em uma próxima inclusão.
5. Imprimir arquivos: Imprime na tela o conteúdo atual do diretório, do arquivo de índices e do
arquivo-mestre.
6. Simulação: Nesta opção deverá ser criado um conjunto aleatório de k chaves sem repetição que serão
inseridos em um arquivo inicialmente vazio, com registros vazios. Em seguida, as chaves devem ser
pesquisadas e os registros recuperados. Marcar o tempo de execução para a inserção e para a pesquisa
do conjunto de dados. Repita o processo para diversos valores de k, p (profundidade global), n
(número de entradas por bucket) e m (tamanho adicional do registro).
