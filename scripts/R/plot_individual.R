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

# Pegar as operações e os cenários únicos que existem no arquivo
operacoes <- unique(dados$operacao)
cenarios <- unique(dados$cenario)

# Primeiro laço: Percorre as operações (ex: "Insert", "ExtractMin")
for (op in operacoes) {
  
  # Segundo laço: Percorre os cenários (ex: "Aleatorio", "Crescente")
  for (cen in cenarios) {

    # Filtra os dados especificamente para essa combinação de operação e cenário
    df <- subset(dados, operacao == op & cenario == cen)
    
    # Se por acaso alguma combinação estiver vazia, pula para a próxima
    if (nrow(df) == 0) next

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
      
      geom_errorbar(
        aes(
          ymin = tempoMedio_ns - desvioPadrao,
          ymax = tempoMedio_ns + desvioPadrao
        ),
        width = 0.1 
      ) +
      
      scale_x_log10(
        breaks = c(1000, 10000, 100000, 1000000), 
        labels = c("1K", "10K", "100K", "1M")
      ) +
      
      # Título atualizado para mostrar a operação e o cenário do gráfico
      labs(
        title = paste("Benchmark -", op, "-", cen),
        x = "Tamanho da entrada",
        y = "Tempo médio (ns)",
        color = "Estrutura"
      ) +
      theme_minimal()

    # Salva o gráfico com o nome composto (ex: "results/Insert_Aleatorio.png")
    ggsave(
      filename = paste0("results/plots/individual/", op, "_", cen, ".png"),
      plot = grafico,
      width = 8,  # Reduzido, pois agora é apenas um gráfico por imagem
      height = 6
    )
  }
}

print("Gráficos gerados com sucesso!")
