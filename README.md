# Comparação de Fila de Prioridade Mínima com TreeMap e Heap

Filas de prioridade organizam elementos por relevância para permitir acesso e remoção eficientes do item de maior precedência, sendo essenciais em sistemas de alto desempenho.

Embora o MinHeap (baseado em array) seja a estrutura clássica e especializada para essa finalidade por focar em ordenação parcial, estruturas de propósito geral com ordenação total, como o **TreeMap** (árvore rubro-negra), também podem ser adaptadas.

Esse estudo realiza uma comparação empírica entre MinHeap e TreeMap, avaliando tempo de execução (inserção, remoção e busca em diferentes cenários) e consumo de memória (com variação no volume de dados e na proporção de duplicatas). 

> **Objetivo: quantificar o custo de tempo e memória de utilizar uma estrutura de ordenação total (TreeMap) no contexto de uma fila de prioridade, comparando-a com uma estrutura especializada (MinHeap).**

## Comparação teórica das estruturas
 
A tabela abaixo mostra as complexidades assintóticas das operações realizadas e analisadas pelo MinHeap e TreeMap.
 
| Operação | MinHeap | TreeMap (Árvore Rubro-Negra) |
|---|---|---|
| Inserção | O(log n) | O(log n) |
| Remoção do menor elemento | O(log n) | O(log n) |
| Busca por um elemento | O(n) | O(log n) |
| Consulta ao menor elemento | O(1) | O(log n) |

## Sobre o projeto

Ambas as estruturas implementam a mesma interface (`PriorityQueue`), oferecendo `add`, `removeMin` e `search`. O objetivo é comparar seu comportamento prático, para além da complexidade assintótica teórica, em:

- **Tempo de execução** das operações de inserção, remoção do mínimo e busca, sob os cenários `Aleatorio`, `Crescente`, `Decrescente` e `Repetido`.
- **Consumo de memória**, sob cenários com diferentes percentuais de valores duplicados (`0%`, `25%`, `50%`, `75%`, `100%`).

O relatório completo, com a fundamentação teórica, metodologia, resultados e discussão, está disponível [aqui.](https://docs.google.com/document/d/1wDGolAAYQ0DbdY7WYPRp6U4n4gpdKcyXToS_H52ECYI/edit?tab=t.0)

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

### Inserção

O MinHeap foi superior em todos os cenários testados, apresentando desempenho muito mais estável à medida que o volume de dados cresceu.

### Remoção do mínimo
O TreeMap superou o MinHeap em volumes maiores de dados. Embora o MinHeap devesse ser teoricamente superior por manter uma árvore de menor altura, sua implementação recursiva gerou *overhead* na pilha de chamadas, enquanto o TreeMap utilizou uma abordagem iterativa (detalhes na Seção 6.2).

### Busca
O TreeMap foi mais rápido em entradas de $10^6$ elementos, resultado esperado devido à sua complexidade $O(\log n)$ frente à busca linear $O(n)$ do MinHeap.

### Consumo de Memória

O MinHeap manteve uso constante de memória independente da taxa de duplicação. O TreeMap consumiu mais memória no geral, mas apresentou redução expressiva do consumo conforme o percentual de duplicatas aumentou, por encadear valores repetidos no mesmo nó em vez de instanciar novos nós.

## Limitações

Os resultados são específicos das implementações analisadas neste projeto, obtidos em uma única máquina e versão de JVM, não devendo ser generalizados como comparação universal entre `TreeMap` e `MinHeap`. Detalhes na Seção 7 do relatório.