library(ggplot2)

# Ler os dados
dados <- read.csv(
  "results/execution.csv",
  header = TRUE,
  sep = ",",
  fileEncoding = "UTF-16LE"
)

# Ajustar tipos
dados$tamanho <- as.numeric(dados$tamanho)
dados$tempoMedio_ns <- as.numeric(dados$tempoMedio_ns)
dados$desvioPadrao <- as.numeric(dados$desvioPadrao)

operacoes <- unique(dados$operacao)
cenarios <- unique(dados$cenario)

for (op in operacoes) {
  for (cen in cenarios) {

    df <- subset(dados, operacao == op & cenario == cen)

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
        width = 500
      ) +
      labs(
        title = paste(op, "-", cen),
        x = "Tamanho da entrada",
        y = "Tempo médio (ns)",
        color = "Estrutura"
      ) +
      theme_minimal()

    ggsave(
      filename = paste0(
        "results/",
        gsub(" ", "_", op), "_",
        gsub(" ", "_", cen),
        ".png"
      ),
      plot = grafico,
      width = 8,
      height = 5
    )
  }
}