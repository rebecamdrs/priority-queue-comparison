# ==============================================================================
# Ler os dados de benchmark e gerar gráficos de linha isolados.
# Cria uma imagem dedicada para cada combinação de operação e cenário.
# ==============================================================================

library(ggplot2)

# ------------------------------------------------------------------------------
# Leitura dos dados
# ------------------------------------------------------------------------------
# Lê o arquivo CSV com os resultados dos experimentos gerados pelo Benchmark.
# A codificação UTF-16LE é utilizada porque o arquivo foi gerado no PowerShell.
dados <- read.csv(
  "results/data/execution.csv",
  header = TRUE,
  sep = ",",
  fileEncoding = "UTF-16LE"
)

# Converte as colunas númericas para o tipo adequado
dados$tamanho <- as.numeric(dados$tamanho)
dados$tempoMedio_ns <- as.numeric(dados$tempoMedio_ns)
dados$desvioPadrao <- as.numeric(dados$desvioPadrao)

# Obtém todas as operações e cenários testadas
operacoes <- unique(dados$operacao)
cenarios <- unique(dados$cenario)

# ------------------------------------------------------------------------------
# Geração dos gráficos
# ------------------------------------------------------------------------------
# Para cada combinação de operação e cenário é criado um gráfico independente,
# permitindo comparar o desempenho das implementações MinHeap e TreeMap.
for (op in operacoes) {
  
  # Percorre todos os cenários
  for (cen in cenarios) {

    # Filtra apenas os dados correspondentes à operação e ao cenário atuais
    df <- subset(dados, operacao == op & cenario == cen)
    
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
      filename = paste0("results/plots/individual/", op, "_", cen, ".png"),
      plot = grafico,
      width = 8, 
      height = 6
    )
  }
}

print("Gráficos gerados com sucesso!")
