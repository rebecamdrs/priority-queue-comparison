# Comparação de Fila de Prioridade: TreeMap vs MinHeap

Estudo empírico comparando duas implementações de fila de prioridade em Java — uma baseada em árvore rubro-negra (`TreeMap`) e outra baseada em array (`MinHeap`) — avaliando tempo de execução e consumo de memória sob diferentes cenários de entrada.

## Sobre o projeto

Ambas as estruturas implementam a mesma interface (`PriorityQueue`), oferecendo `add`, `removeMin` e `search`. O objetivo é comparar seu comportamento prático — para além da complexidade assintótica teórica — em:

- **Tempo de execução** das operações de inserção, remoção do mínimo e busca, sob os cenários `Aleatorio`, `Crescente`, `Decrescente` e `Repetido`.
- **Consumo de memória**, sob cenários com diferentes percentuais de valores duplicados (`0%`, `25%`, `50%`, `75%`, `100%`).

O relatório completo, com a fundamentação teórica, metodologia, resultados e discussão, está disponível em ------por aqui o "link"que leva ao docs no repo.
!!!!!!!!!!!!!lembrar de subir a versao final do relatorio aqui!!!!!!!

## Requisitos

- **JDK** 21 ou superior
- **R** com os pacotes `ggplot2`, `tidyr`, `gridExtra` e `grid` instalados
- Conexão com a internet na primeira execução, para baixar a dependência do JOL (Java Object Layout)

## Como executar

Na raiz do projeto:

```bash
./run_benchmark.sh
```

*(No Windows, fora do WSL/Git Bash, use `run_benchmark.bat`.)*

O script executa:

1. Compilação de todas as classes Java
2. Execução do `TimeBenchmark`, gerando `results/data/executionTime.csv`
3. Execução do `MemoryBenchmark`, gerando `results/data/executionMemory.csv`
4. Geração dos gráficos combinados e individuais (R)
5. Geração das tabelas comparativas em PNG (R)

Os resultados ficam disponíveis na pasta `results/`.

## Principais resultados

- **MinHeap** foi mais rápido na **inserção**, em todos os cenários testados.
- **TreeMap** foi mais rápido na **remoção do mínimo** e, de forma bem mais expressiva, na **busca** — resultado esperado nesse último caso, dado que o MinHeap não é uma estrutura de busca eficiente (O(n) contra O(log n) do TreeMap).
- **MinHeap** apresentou consumo de memória constante entre os cenários de duplicação, enquanto o **TreeMap** apresentou redução expressiva de memória conforme a taxa de duplicação aumentou, por agrupar valores repetidos em vez de criar novos nós.

Análise completa na Seção 5 do relatório; discussão dos resultados na Seção 6.

## Limitações

Os resultados são específicos das implementações analisadas neste projeto, obtidos em uma única máquina e versão de JVM, não devendo ser generalizados como comparação universal entre `TreeMap` e `MinHeap`. Detalhes na Seção 7 do relatório.
