package experiment;

import heap.MinHeap;
import treemap.TreeMapPriorityQueue;
import model.PriorityQueue;
import static experiment.Benchmark.*;

/**
 * Classe responsável por medir o consumo de memória das diferentes implementações de Fila de
 * Prioridade em diferentes tamanhos de entrada e cenários de dados.
 */
public class MemoryBenchmark {
    /**
     * Executa todos os experimentos para cada combinação de estrutura, tamanho de entrada e cenário, 
     * imprimindo os resultados em formato CSV.
     * @param args
     */
    public static void main(String[] args) {
        //Cabeçalho do arquivo CSV de saída
        System.out.println("estrutura,cenario,tamanho,operacao,mediabytes,desvioPadraoBytes");

        for (String estrutura: ESTRUTURAS) {
            for (int tamanho: TAMANHOS) {
                for (String cenario: CENARIOS) {
                    int[] dados = DataGenerator.gerar(tamanho, cenario);
                    medirMemoria(estrutura, cenario, tamanho, dados);
                }
            }
        }
    }

    /**
     * Mede o consumo de memória para uma determinada estrutura de dados, cenário e tamanho de entrada.
     * 
     * (usar o runtime tá impreciso, o garbage colector ta atrapalhando, ver isso depois!!!)
     * 
     * @param nomeEstrutura Nome da estrutura de dados utilizada.
     * @param cenario       Tipo de ordenação do conjunto de dados.
     * @param tamanho       Tamanho da entrada.
     * @param dados         Vetor contendo os elementos a serem inseridos.
     */
    private static void medirMemoria(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] memorias = new long[REPETICOES];
        Runtime rt = Runtime.getRuntime();
 
        // Fase de Medição Real
        for (int i = 0; i < REPETICOES; i++) {
            System.gc();
            sleep();
            long memoriaAntes = rt.totalMemory() - rt.freeMemory();
 
            PriorityQueue estrutura;
            switch (nomeEstrutura) {
                case "TreeMap":
                    TreeMapPriorityQueue map = new TreeMapPriorityQueue();
                    for (int j = 0; j < tamanho; j++) {
                        map.add(dados[j]);
                    }
                    estrutura = map;
                    break;
                case "MinHeap":
                    MinHeap heap = new MinHeap(tamanho);
                    for (int j = 0; j < tamanho; j++) {
                        heap.add(dados[j]);
                    }
                    estrutura = heap;
                    break;
                default:
                    throw new IllegalArgumentException("tipo inválido");
            }
 
            System.gc();
            sleep();
            long memoriaDepois = rt.totalMemory() - rt.freeMemory();
 
            // Impede que o compilador "otimize" a referência antes da medição terminar
            if (estrutura.hashCode() == Integer.MIN_VALUE) System.out.println("nunca acontece");
 
            memorias[i] = memoriaDepois - memoriaAntes;
        }
 
        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "Memoria", memorias);
    }

    private static void sleep() {
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }
}