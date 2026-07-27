# ==============================================================================
# Ler os dados brutos do Benchmark, reestruturá-los para comparação lado a lado 
# (Heap vs TreeMap) e exportar tabelas em formato PNG.
# ==============================================================================
library(gridExtra)
library(grid)
library(tidyr) 

# ------------------------------------------------------------------------------
# Leitura e limpeza dos dados
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
dados$tempoMedio_ns <- round(as.numeric(dados$tempoMedio_ns), 2)
dados$desvioPadrao <- round(as.numeric(dados$desvioPadrao), 2)

# Cenários e operções presentes no arquivo
cenarios <- unique(dados$cenario)
operacoes <- unique(dados$operacao)

# ------------------------------------------------------------------------------
# Tema visual utilizado nas tabelas
# ------------------------------------------------------------------------------
tema_estilizado <- ttheme_default(
  core = list(
    bg_params = list(fill = c("#f9f9f9", "#e6f2ff"), col = NA),
    fg_params = list(fontsize = 12, col = "#333333"),
    padding = unit(c(10, 8), "mm") 
  ),
  colhead = list(
    bg_params = list(fill = "#004d99", col = NA),
    fg_params = list(col = "white", fontface = "bold", fontsize = 13),
    padding = unit(c(10, 8), "mm") 
  )
)

# ------------------------------------------------------------------------------
# Geração das tabelas
# ------------------------------------------------------------------------------
# Para cada combinação de cenário e operação é criada uma tabela contendo: Tempo 
# médio de execução; Desvio padrão e Comparação entre MinHeap e TreeMap.
for (cen in cenarios) {
  for (op in operacoes) {
    
    # Filtra apenas os resultados do cenário e operação atuais
    tabela <- subset(dados, cenario == cen & operacao == op)
    
    if (nrow(tabela) == 0) next
    
    # Seleciona apenas as colunas relevantes
    tabela <- tabela[, c("tamanho", "estrutura", "tempoMedio_ns", "desvioPadrao")]
    
    # Coloca Heap e TreeMap lado a lado
    tabela_larga <- pivot_wider(
      tabela,
      names_from = estrutura,
      values_from = c(tempoMedio_ns, desvioPadrao)
    )
    
    # Ordena pelo tamanho da entrada
    tabela_larga <- tabela_larga[order(tabela_larga$tamanho), ]
    
    # Formatação  do tamanho da entrada em potência de 10
    tabela_larga$tamanho <- paste0("10^", log10(tabela_larga$tamanho))

    # Formata tempos e desvios
    colunas_valores <- setdiff(names(tabela_larga), "tamanho")
    for (col in colunas_valores) {
      tabela_larga[[col]] <- prettyNum(tabela_larga[[col]], big.mark = ".", decimal.mark = ",", scientific = FALSE)
    }
    
    # Substitui valores ausentes
    tabela_larga[is.na(tabela_larga)] <- "-"
    
    # Renomeia as colunas
    colnames(tabela_larga) <- c(
      "Tamanho (n)",
      "Tempo Médio (ns)\nHeap",
      "Tempo Médio (ns)\nTreeMap",
      "Desvio Padrão (ns)\nHeap",
      "Desvio Padrão (ns)\nTreeMap"
    )
    
    # ------------------------------------------------------------------------------
    # Exportação para PNG
    # ------------------------------------------------------------------------------
    nome_arquivo <- paste0(
      "results/tables/",
      gsub(" ", "_", cen), "_",
      gsub(" ", "_", op),
      "_tabela.png"
    )
    
    # Cria a tabela gráfica
    tabela_grob <- tableGrob(tabela_larga, rows = NULL, theme = tema_estilizado)

    # Título da tabela
    titulo_texto <- paste("Cenário:", cen, "| Operação:", op)
    titulo_grob <- textGrob(
      titulo_texto, 
      gp = gpar(fontsize = 16, fontface = "bold", col = "#333333")
    )

    # Salva a imagem
    png(filename = nome_arquivo, width = 950, height = 280, res = 100)
    
    grid.arrange(titulo_grob, tabela_grob, nrow = 2, heights = c(0.15, 0.85))
    
    dev.off()
  }
}

print("Tabelas processadas e formatadas com sucesso!")
