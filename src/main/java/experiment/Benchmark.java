package experiment;

import model.PriorityQueue;
import treemap.TreeMapPriorityQueue;
import heap.MinHeap;

public class Benchmark {
    //Configurações
    private static final int[] TAMANHOS = {1000, 10000, 100000, 1000000};
    private static final String[] CENARIOS = {"Aleatorio", "Crescente", "Decrescente", "Repetido"};
    // private static final String[] ESTRUTURAS = {"MinHeap"};
    private static final String[] ESTRUTURAS = {"MinHeap", "TreeMap"};
    private static final int REPETICOES = 10;
    private static final int WARMUP_ROUNDS = 3;

    public static void main(String[] args) {
        //Cabeçalho do CSV
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

    private static void medirInsercao(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] tempos = new long[REPETICOES];

        //Warm-up
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);
            for (int valor: dados)
                queue.add(valor);
        }

        //Medição real
        for (int i = 0; i < REPETICOES; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);

            //Tenta limpar a memória antes de medir
            System.gc();

            long inicio = System.nanoTime();
            for (int valor: dados)
                queue.add(valor);
            long fim = System.nanoTime();

            tempos[i] = fim - inicio;
        }

        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "Add", tempos);
    }

    private static void medirRemoveMin(String nomeEstrutura, String cenario, int tamanho, int[] dados) {
        long[] tempos = new long[REPETICOES];

        //Warm-up
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);
            for (int valor: dados)
                queue.add(valor);
            
            while(!queue.isEmpty())
                queue.removeMin();
        }

        //Medição real
        for (int i = 0; i < REPETICOES; i++) {
            PriorityQueue queue = instanciarFila(nomeEstrutura, tamanho);

            //Setup: preencher a fila não entra no cronometro
            for (int valor: dados)
                queue.add(valor);

            //Tenta limpar a memória antes de medir
            System.gc();

            long inicio = System.nanoTime();
            while (!queue.isEmpty())
                queue.removeMin();
            long fim = System.nanoTime();

            tempos[i] = fim - inicio;
        }

        imprimirEstatisticas(nomeEstrutura, cenario, tamanho, "RemoveMin", tempos);
    }


    //Instancia dinamicamente a estrutura baseada na string
    private static PriorityQueue instanciarFila(String estrutura, int tamanho) {
        if (estrutura.equals("MinHeap"))
            return new MinHeap(tamanho);
        else if (estrutura.equals("TreeMap"))
            return new TreeMapPriorityQueue();
        throw new IllegalArgumentException("Estrutura não suportada");
    }

    private static void imprimirEstatisticas(String estrutura, String cenario, int tamanho, String operacao, long[] tempos) {
        double media = 0;
        for (long t: tempos)
            media += t;
        media /= tempos.length;
        
        double somaDiferencas = 0;
        for (long t: tempos)
            somaDiferencas += Math.pow(t - media, 2);
        double desvioPadrao = Math.sqrt(somaDiferencas / tempos.length);
        
        System.out.printf(java.util.Locale.US, "%s,%s,%d,%s,%.2f,%.2f\n", estrutura, cenario, tamanho, operacao, media, desvioPadrao);
    }
}
