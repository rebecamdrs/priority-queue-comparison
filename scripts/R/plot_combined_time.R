# ==============================================================================
# Ler os dados de tempo e gerar gráficos de linha comparativos.
# Utiliza 'facet_wrap' para agrupar todos os cenários de uma mesma operação
# em uma única imagem.
# ==============================================================================

library(ggplot2)

# ------------------------------------------------------------------------------
# Leitura dos dados
# ------------------------------------------------------------------------------
# Lê o arquivo CSV com os resultados dos experimentos de tempo de execução
# gerados pelo Benchmark.
dados_tempo <- read.csv(
  "results/data/executionTime.csv",
  header = TRUE, sep = ","
)

# Converte as colunas númericas para o tipo adequado
dados_tempo$tamanho <- as.numeric(dados_tempo$tamanho)
dados_tempo$tempoMedio_ns <- as.numeric(dados_tempo$tempoMedio_ns)
dados_tempo$desvioPadrao <- as.numeric(dados_tempo$desvioPadrao)

# Obtém todas as operações testadas
operacoes <- unique(dados_tempo$operacao)

# ------------------------------------------------------------------------------
# Geração dos gráficos de tempo
# ------------------------------------------------------------------------------
# Para cada operação é criado um gráfico contendo todos os cenários, permitindo
# comparar o desempenho das implementações MinHeap e TreeMap.
for (op in operacoes) {

  # Filtra apenas os resultados da operação atual
  df <- subset(dados_tempo, operacao == op)

  # Cria o gráfico
  grafico <- ggplot(
    df,
    aes(
      x = tamanho,
      y = tempoMedio_ns,
      color = estrutura,
      group = estrutura
    )
  ) +
    geom_line(linewidth = 1) +
    geom_point(size = 2) +
    
    # Adiciona barras de erro vertical representando um desvio padrão, indicando a 
    # variabilidade das medições.
    geom_errorbar(
      aes(
        ymin = tempoMedio_ns - desvioPadrao,
        ymax = tempoMedio_ns + desvioPadrao
      ),
      width = 0.1 
    ) +
    
    # Utiliza escala logarítmica na base 10 para o eixo X. Como os tamanhos crescem de forma
    # exponencial, essa escala facilita a visualização do crescimento das estruturas.
    scale_x_log10(
      breaks = c(1000, 10000, 100000, 1000000), 
      labels = c("1K", "10K", "100K", "1M")
    ) +
    
    # Divide o gráfico em quatro painéis, um para cada cenário.
    # O parâmetro scales = "free_y" permite que cada painel utilize sua própria escala, 
    # facilitando a comparação visual.
    facet_wrap(~ cenario, ncol = 2, scales = "free_y") +
    
    # Define títulos, rótulos dos eixos e legenda.
    labs(
      title = paste("Benchmark -", op),
      x = "Tamanho da entrada",
      y = "Tempo médio (ns)",
      color = "Estrutura"
    ) +
    # Aplica um tema limpo ao gráfico.
    theme_minimal()

  # ------------------------------------------------------------------------------
  # Salva o gráfico em formato PNG.
  # Um arquivo é gerado para cada operação avaliada.
  # ------------------------------------------------------------------------------
  ggsave(
    filename = paste0("results/plots/time/combined/", op, ".png"),
    plot = grafico,
    width = 10,
    height = 8
  )
}

print("Gráficos combinados de tempo gerados com sucesso!")
