# ==============================================================================
# Ler os dados de benchmark e gerar gráficos de linha comparativos.
# Utiliza 'facet_wrap' para agrupar todos os cenários de uma mesma operação
# em uma única imagem.
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

# Obtém todas as operações testadas (Add e RemoveMin)
operacoes <- unique(dados$operacao)

# ------------------------------------------------------------------------------
# Geração dos gráficos
# ------------------------------------------------------------------------------
# Para cada operação é criado um gráfico contendo todos os cenários, permitindo
# comparar o desempenho das implementações MinHeap e TreeMap.
for (op in operacoes) {

  # Filtra apenas os resultados da operação atual
  df <- subset(dados, operacao == op)

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
    filename = paste0("results/plots/combined/", op, ".png"),
    plot = grafico,
    width = 10,
    height = 8
  )
}

print("Gráficos gerados com sucesso!")
