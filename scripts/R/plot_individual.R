# ==============================================================================
# Ler os dados de benchmark e gerar gráficos de linha isolados.
# Cria uma imagem dedicada para cada combinação de operação e cenário.
# ==============================================================================

library(ggplot2)

# ------------------------------------------------------------------------------
# Leitura dos dados
# ------------------------------------------------------------------------------
# Lê o arquivo CSV com os resultados dos experimentos gerados pelo Benchmark.
dados_tempo <- read.csv(
  "results/data/executionTime.csv",
  header = TRUE, sep = ","
)
dados_memoria <- read.csv(
  "results/data/executionMemory.csv",
  header = TRUE, sep = ","
)

# Converte as colunas númericas para o tipo adequado
dados_tempo$tamanho <- as.numeric(dados_tempo$tamanho)
dados_tempo$tempoMedio_ns <- as.numeric(dados_tempo$tempoMedio_ns)
dados_tempo$desvioPadrao <- as.numeric(dados_tempo$desvioPadrao)

dados_memoria$tamanho <- as.numeric(dados_memoria$tamanho)
dados_memoria$tempoMedio_ns <- as.numeric(dados_memoria$memoriaMedia_b)
dados_memoria$desvioPadrao <- as.numeric(dados_memoria$desvioPadrao)

# Obtém todas as operações e cenários testadas
operacoes <- unique(dados_tempo$operacao)
cenarios_tempo <- unique(dados_tempo$cenario)
cenarios_memoria <- unique(dados_memoria$cenario)

# ------------------------------------------------------------------------------
# Geração dos gráficos de tempo
# ------------------------------------------------------------------------------
# Para cada combinação de operação e cenário é criado um gráfico independente,
# permitindo comparar o desempenho das implementações MinHeap e TreeMap.
for (op in operacoes) {
  
  # Percorre todos os cenários
  for (cen in cenarios_tempo) {

    # Filtra apenas os dados correspondentes à operação e ao cenário atuais
    df <- subset(dados_tempo, operacao == op & cenario == cen)
    
    # Caso não existam dados para essa combinação, passa para a próxima
    if (nrow(df) == 0) next

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
      
      # Define títulos, rótulos dos eixos e legenda.
      labs(
        title = paste("Benchmark -", op, "-", cen),
        x = "Tamanho da entrada",
        y = "Tempo médio (ns)",
        color = "Estrutura"
      ) +
      # Aplica um tema limpo ao gráfico.
      theme_minimal()

    # ------------------------------------------------------------------------------
    # Salva o gráfico em formato PNG.
    # Um arquivo é gerado para cada combinação de operação e cenário.
    # ------------------------------------------------------------------------------
    ggsave(
      filename = paste0("results/plots/time/individual/", op, "_", cen, ".png"),
      plot = grafico,
      width = 8, 
      height = 6
    )
  }


# ------------------------------------------------------------------------------
# Geração dos gráficos de tempo
# ------------------------------------------------------------------------------
# Para cada combinação de operação e cenário é criado um gráfico independente,
# permitindo comparar o desempenho das implementações MinHeap e TreeMap.
# Percorre todos os cenários

for (cen in cenarios_memoria) {
  # Filtra apenas os dados correspondentes à operação e ao cenário atuais
  df <- subset(dados_memoria, operacao == op & cenario == cen)
  
  # Caso não existam dados para essa combinação, passa para a próxima
    if (nrow(df) == 0) next

  # Cria o gráfico
  grafico <- ggplot(
    df,
    aes(
      x = tamanho,
      y = memoriaMedia_b,
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
        ymin = memoriaMedia_b - desvioPadrao,
        ymax = memoriaMedia_b + desvioPadrao
      ),
      width = 0.1 
    ) +
    
    # Utiliza escala logarítmica na base 10 para o eixo X. Como os tamanhos crescem de forma
    # exponencial, essa escala facilita a visualização do crescimento das estruturas.
    scale_x_log10(
      breaks = c(1000, 10000, 100000, 1000000), 
      labels = c("1K", "10K", "100K", "1M")
    ) +
    
    # Define títulos, rótulos dos eixos e legenda.
    labs(
      title = paste("Benchmark -", cen),
      x = "Tamanho da entrada",
      y = "Memória (b)",
      color = "Estrutura"
    ) +
    # Aplica um tema limpo ao gráfico.
    theme_minimal()

  # ------------------------------------------------------------------------------
  # Salva o gráfico em formato PNG.
  # Um arquivo é gerado para cada combinação de operação e cenário.
  # ------------------------------------------------------------------------------
  ggsave(
    filename = paste0("results/plots/memory/individual/",cen, ".png"),
    plot = grafico,
    width = 8, 
    height = 6
  )
}

print("Gráficos gerados com sucesso!")
