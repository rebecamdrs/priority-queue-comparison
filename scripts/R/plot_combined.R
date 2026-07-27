library(ggplot2)

# Ler os dados
dados <- read.csv(
  "results/data/execution.csv",
  header = TRUE,
  sep = ",",
  fileEncoding = "UTF-16LE"
)

# Ajustar tipos
dados$tamanho <- as.numeric(dados$tamanho)
dados$tempoMedio_ns <- as.numeric(dados$tempoMedio_ns)
dados$desvioPadrao <- as.numeric(dados$desvioPadrao)

operacoes <- unique(dados$operacao)

for (op in operacoes) {

  df <- subset(dados, operacao == op)

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
    
    # Mantém as barras de erro para confiabilidade estatística
    geom_errorbar(
      aes(
        ymin = tempoMedio_ns - desvioPadrao,
        ymax = tempoMedio_ns + desvioPadrao
      ),
      width = 0.1 
    ) +
    
    # Mantém a escala logarítmica para visualização correta da complexidade
    scale_x_log10(
      breaks = c(1000, 10000, 100000, 1000000), 
      labels = c("1K", "10K", "100K", "1M")
    ) +
    
    # Separação por cenários com eixos independentes
    facet_wrap(~ cenario, ncol = 2, scales = "free_y") +
    
    labs(
      title = paste("Benchmark -", op),
      x = "Tamanho da entrada",
      y = "Tempo médio (ns)",
      color = "Estrutura"
    ) +
    theme_minimal()

  # Salva os gráficos dinamicamente com o nome da operação
  ggsave(
    filename = paste0("results/plots/combined/", op, ".png"),
    plot = grafico,
    width = 10,
    height = 8
  )
}

print("Gráficos gerados com sucesso!")
