package experiment;

import model.PriorityQueue;
import treemap.TreeMapPriorityQueue;
import heap.MinHeap;

/**
 * Classe utilitária que contém os métodos que são compartilhados entre as classes MemoryBenchmark e TimeBenchmark.
 * 
 */
public class Benchmark {

    // Configurações Globais do Experimento
    protected static final int[] TAMANHOS = {1000, 10000, 100000, 1000000};
    protected static final String[] CENARIOS = {"Aleatorio", "Crescente", "Decrescente", "Repetido"};
    protected static final String[] ESTRUTURAS = {"MinHeap", "TreeMap"};
    protected static final int REPETICOES = 10;
    protected static final int WARMUP_ROUNDS = 3;

    
    /**
     * Instancia dinamicamente a implementação de fila de prioridade correspondente ao nome informado.
     * 
     * @param estrutura Nome da estrutura de dados desejada.
     * @param tamanho   Capacidade inicial da fila.
     * @return          Uma instância da fila de prioridade correspondente.
     */
    protected static PriorityQueue instanciarFila(String estrutura, int tamanho) {
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
     * @param valores   Vetor contendo os valores medidos (tempo em nanossegundos e memória em bytes).
     */
    protected static void imprimirEstatisticas(String estrutura, String cenario, int tamanho, String operacao, long[] valores) {
        double media = 0;
        for (long t: valores)
            media += t;
        media /= valores.length;
        
        double somaDiferencas = 0;
        for (long t: valores)
            somaDiferencas += Math.pow(t - media, 2);
        // Calcula o desvio padrão
        double desvioPadrao = Math.sqrt(somaDiferencas / valores.length);
        
        System.out.printf(java.util.Locale.US, "%s,%s,%d,%s,%.2f,%.2f%n", estrutura, cenario, tamanho, operacao, media, desvioPadrao);
    }
}
