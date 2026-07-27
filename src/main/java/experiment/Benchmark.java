package experiment;

import model.PriorityQueue;
import treemap.TreeMapPriorityQueue;
import heap.MinHeap;

/**
 * Classe responsável por executar os testes de desempenho (benchmarking) comparando diferentes 
 * implementações de Fila de Prioridade. São avaliadas as operações de inserção e remoção do menor 
 * elemento em diferentes tamanhos de entrada e cenários de dados.
 * 
 * Para cada configuração, são realizadas rodadas de aquecimento e múltiplas execuções para medir
 * o tempo de execução em nanossegundos. Ao final, a média e o desvio padrão dos tempos são
 * impressos em CSV.
 */
public class Benchmark {

    // Configurações Globais do Experimento
    private static final int[] TAMANHOS = {1000, 10000, 100000, 1000000};
    private static final String[] CENARIOS = {"Aleatorio", "Crescente", "Decrescente", "Repetido"};
    private static final String[] ESTRUTURAS = {"MinHeap", "TreeMap"};
    private static final int REPETICOES = 10;
    private static final int WARMUP_ROUNDS = 3;

    /**
     * Executa todos os experimentos para cada combinação de estrutura, tamanho de entrada e cenário, 
     * imprimindo os resultados em formato CSV.
     * @param args
     */
    public static void main(String[] args) {
        //Cabeçalho do arquivo CSV de saída
        System.out.println("estrutura,cenario,tamanho,operacao,tempoMedio_ns,desvioPadrao");

        for (String estrutura: ESTRUTURAS) {
            for (int tamanho: TAMANHOS) {
                for (String cenario: CENARIOS) {
                    int[] dados = DataGenerator.gerar(tamanho, cenario);

                    medirInsercao(estrutura, cenario, tamanho, dados);
                    medirRemoveMin(estrutura, cenario, tamanho, dados);
                }
            }
        }
    }

    /**
     * Mede o tempo de execução da operação de inserção para uma determinada estrutura de dados.
     * Inclui rodadas de aquecimento para otimização.
     * 
     * @param nomeEstrutura Nome da estrutura de dados utilizada.
     * @param cenario       Tipo de ordenação do conjunto de dados.
     * @param tamanho       Tamanho da entrada.
     * @param dados         Vetor contendo os elementos a serem inseridos.
     */
    private static void medirInsercao(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] tempos = new long[REPETICOES];

        // Fase de Warm-up (não contabilizada)
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);
            for (int valor: dados)
                queue.add(valor);
        }

        // Fase de Medição Real
        for (int i = 0; i < REPETICOES; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);

            // Solicita a limpeza de memória antes de iniciar o cronômetro
            System.gc();

            long inicio = System.nanoTime();
            for (int valor: dados)
                queue.add(valor);
            long fim = System.nanoTime();

            tempos[i] = fim - inicio;
        }

        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "Add", tempos);
    }

    /**
     * Mede o tempo de execução para esvaziar completamente a estrutura.
     * Garante que o tempo gasto preenchendo a fila não afete os resultados da remoção
     * 
     * @param nomeEstrutura Nome da estrutura de dados utilizada.
     * @param cenario       Tipo de ordenação do conjunto de dados.
     * @param tamanho       Tamanho da entrada.
     * @param dados         Vetor contendo os elementos da fila.
     */
    private static void medirRemoveMin(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] tempos = new long[REPETICOES];

        // Fase de Warm-up (não contabilizada)
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);
            for (int valor: dados)
                queue.add(valor);
            
            while(!queue.isEmpty())
                queue.removeMin();
        }

        // Fase de Medição Real
        for (int i = 0; i < REPETICOES; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);

            //Setup: O preenchimento da estrutura ocorre fora do cronômetro
            for (int valor: dados)
                queue.add(valor);

            // Solicita a limpeza de memória antes de iniciar o cronômetro
            System.gc();

            long inicio = System.nanoTime();
            while (!queue.isEmpty())
                queue.removeMin();
            long fim = System.nanoTime();

            tempos[i] = fim - inicio;
        }

        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "RemoveMin", tempos);
    }

    /**
     * Instancia dinamicamente a implementação de fila de prioridade correspondente ao nome informado.
     * 
     * @param estrutura Nome da estrutura de dados desejada.
     * @param tamanho   Capacidade inicial da fila.
     * @return          Uma instância da fila de prioridade correspondente.
     */
    private static PriorityQueue instanciarFila(String estrutura, int tamanho) {
        if (estrutura.equals("MinHeap"))
            return new MinHeap(tamanho);
        else if (estrutura.equals("TreeMap"))
            return new TreeMapPriorityQueue();
        throw new IllegalArgumentException("Estrutura não suportada");
    }

    /**
     * Calcula a média e o desvio padrão dos tempos medidos e imprime os resultados formatado em CSV.
     * 
     * @param estrutura Nome da estrutura testada.
     * @param cenario   Cenário de distribuição dos dados.
     * @param tamanho   Tamanho da entrada.
     * @param operacao  Operação realizada.
     * @param tempos    Vetor contendo os tempos de execução em nanossegundos.
     */
    private static void imprimirEstatisticas(String estrutura, String cenario, int tamanho, String operacao, long[] tempos) {
        double media = 0;
        for (long t: tempos)
            media += t;
        media /= tempos.length;
        
        double somaDiferencas = 0;
        for (long t: tempos)
            somaDiferencas += Math.pow(t - media, 2);
        // Calcula o desvio padrão
        double desvioPadrao = Math.sqrt(somaDiferencas / tempos.length);
        
        System.out.printf(java.util.Locale.US, "%s,%s,%d,%s,%.2f,%.2f\n", estrutura, cenario, tamanho, operacao, media, desvioPadrao);
    }
}
