package experiment;

import java.util.Random;

/**
 * Classe utilitária responsável por gerar os dados de entrada para os
 * experimentos comparativos entre Heap e TreeMap.
 * 
 * Garante que ambas as estruturas de dados processem exatamente a mesma massa
 * de dados em cada rodada de execução através do uso de uma seed fixa.
 * Considera vários cenários de distribuição dos dados.
 */
public class DataGenerator {
    // Seed fixa utilizada para o gerador de números aleatórios.
    // Garante que, para um dado tamanho e cenário, o array gerado sera sempre idêntico,
    // permitindo uma comparação justa e o controle do ambiente de teste.
    private static final long SEED = 42L;
    
    /**
     * Gera um array de inteiros baseado no tamanho e no cenário especificado.
     * 
     * @param tamanho O número de elementos que o array de teste deve conter
     * @param cenario O nome do cenário de distribuição de dados desejado
     * @return Um array contendo as prioridades geradas de acordo com as regras do cenário.
     */
    public static int[] gerar (int tamanho, String cenario) {
        int[] dados = new int[tamanho];
        Random rand = new Random(SEED);

        switch (cenario) {
            case "Aleatorio":
                // Gera uma distribuição uniforme de prioridades.
                for (int i = 0; i < tamanho; i++)
                    dados[i] = rand.nextInt(tamanho * 10);
                break;
            
            case "Crescente":
                // Gera dados ordenados sequencialmente.
                for (int i = 0; i < tamanho; i++)
                    dados[i] = i;
                break;
            
            case "Decrescente":
                // Gera dados em ordem reversa.
                for (int i = 0; i < tamanho; i++)
                    dados[i] = tamanho - i;
                break;
            
            case "Repetido":
                // Gera dados com prioridades limitadas (baixa cardinalidade).
                for (int i = 0; i < tamanho; i++)
                    // Limita as prioridades de 0 a 9, forçando colisões e repetições massivas
                    dados[i] = rand.nextInt(10);
                break;
            
            default:
                throw new IllegalArgumentException("Cenário desconhecido: " + cenario);
        }

        return dados;
    }
}
