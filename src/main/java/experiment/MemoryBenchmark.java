package experiment;

import heap.MinHeap;
import treemap.TreeMapPriorityQueue;
import model.PriorityQueue;
import static experiment.Benchmark.*;

import org.openjdk.jol.info.GraphLayout;

/**
 * Classe responsável por medir o consumo de memória das diferentes implementações de Fila de
 * Prioridade em diferentes tamanhos de entrada e cenários de dados.
 */
public class MemoryBenchmark {
    private static final double[] PERCENTUAIS_DUPLICATAS = {0.0, 0.25, 0.5, 0.75, 1};
    private static final int REPETICOES_MEMORIA = 3;

    /**
     * Executa todos os experimentos para cada combinação de estrutura, tamanho de entrada e cenário, 
     * imprimindo os resultados em formato CSV.
     * @param args
     */
    public static void main(String[] args) {
        //Cabeçalho do arquivo CSV de saída
        System.out.println("estrutura,cenario,tamanho,operacao,memoriaMedia_b,desvioPadrao");

        for (String estrutura: ESTRUTURAS) {
            for (int tamanho: TAMANHOS) {
                for (double percentual: PERCENTUAIS_DUPLICATAS) {
                    int[] dados = DataGenerator.gerarParaAnaliseMemoria(tamanho, percentual);
                    String cenario = "Duplicatas_" + percentual * 100;
                    medirMemoria(estrutura, cenario, tamanho, dados);
                }
            }
        }
    }

    /**
     * Mede o consumo de memória para uma determinada estrutura de dados, cenário e tamanho de entrada.
     * 
     * @param nomeEstrutura Nome da estrutura de dados utilizada.
     * @param cenario       Tipo de ordenação do conjunto de dados.
     * @param tamanho       Tamanho da entrada.
     * @param dados         Vetor contendo os elementos a serem inseridos.
     */
    private static void medirMemoria(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] memorias = new long[REPETICOES_MEMORIA];
 
        // Fase de Medição Real
        for (int i = 0; i < REPETICOES_MEMORIA; i++) {
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

            memorias[i] = GraphLayout.parseInstance(estrutura).totalSize();
        }
 
        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "Memoria", memorias);
    }
}