# ==============================================================================
# Gerar tabelas comparativas de tempo de execução (Heap vs TreeMap)
# ==============================================================================
library(gridExtra)
library(grid)
library(tidyr) 

# ------------------------------------------------------------------------------
# Leitura e limpeza dos dados
# ------------------------------------------------------------------------------
# Lê o arquivo CSV com os resultados dos experimentos de tempo de execução
# gerados pelo Benchmark.
dados_tempo <- read.csv(
  "results/data/executionTime.csv",
  header = TRUE, sep = ","
)

# Converte as colunas númericas para o tipo adequado
dados_tempo$tamanho <- as.numeric(dados_tempo$tamanho)
dados_tempo$tempoMedio_ns <- round(as.numeric(dados_tempo$tempoMedio_ns), 2)
dados_tempo$desvioPadrao <- round(as.numeric(dados_tempo$desvioPadrao), 2)

# Cenários e operções presentes no arquivo
cenarios_tempo <- unique(dados_tempo$cenario)
operacoes <- unique(dados_tempo$operacao)

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
# Geração das tabelas tempo
# ------------------------------------------------------------------------------
# Para cada combinação de cenário e operação é criada uma tabela contendo: Tempo 
# médio de execução; Desvio padrão e Comparação entre MinHeap e TreeMap.
for (cen in cenarios_tempo) {
  for (op in operacoes) {
    
    # Filtra apenas os resultados do cenário e operação atuais
    tabela_tempo <- subset(dados_tempo, cenario == cen & operacao == op)
    
    if (nrow(tabela_tempo) == 0) next
    
    # Seleciona apenas as colunas relevantes
    tabela_tempo <- tabela_tempo[, c("tamanho", "estrutura", "tempoMedio_ns", "desvioPadrao")]
    
    # Coloca Heap e TreeMap lado a lado
    tabela_larga_tempo <- pivot_wider(
      tabela_tempo,
      names_from = estrutura,
      values_from = c(tempoMedio_ns, desvioPadrao)
    )
    
    # Ordena pelo tamanho da entrada
    tabela_larga_tempo <- tabela_larga_tempo[order(tabela_larga_tempo$tamanho), ]
    
    # Calcula quantas vezes o TreeMap foi mais lento (ou mais rápido)
    tabela_larga_tempo$razao <- tabela_larga_tempo$tempoMedio_ns_TreeMap / tabela_larga_tempo$tempoMedio_ns_MinHeap
    
    # Formata a razão com 2 casas decimais
    tabela_larga_tempo$razao <- paste0(format(round(tabela_larga_tempo$razao, 2), nsmall = 2, decimal.mark = ","), "x")

    # Reordena as colunas
    tabela_larga_tempo <- tabela_larga_tempo[, c(
      "tamanho",
      "tempoMedio_ns_MinHeap", 
      "tempoMedio_ns_TreeMap", 
      "razao", 
      "desvioPadrao_MinHeap", 
      "desvioPadrao_TreeMap"
    )]

    # Formatação  do tamanho da entrada em potência de 10
    tabela_larga_tempo$tamanho <- paste0("10^", log10(tabela_larga_tempo$tamanho))

    # Formata tempos e desvios
    colunas_valores <- setdiff(names(tabela_larga_tempo), "tamanho")
    for (col in colunas_valores) {
      tabela_larga_tempo[[col]] <- prettyNum(tabela_larga_tempo[[col]], big.mark = ".", decimal.mark = ",", scientific = FALSE)
    }
    
    # Substitui valores ausentes
    tabela_larga_tempo[is.na(tabela_larga_tempo)] <- "-"
    
    # Renomeia as colunas
    colnames(tabela_larga_tempo) <- c(
      "Tamanho (n)",
      "Tempo Médio (ns)\nHeap",
      "Tempo Médio (ns)\nTreeMap",
      "Razão\n(TreeMap / Heap)",
      "Desvio Padrão (ns)\nHeap",
      "Desvio Padrão (ns)\nTreeMap"
    )
    
    # ------------------------------------------------------------------------------
    # Exportação para PNG
    # ------------------------------------------------------------------------------
    nome_arquivo <- paste0(
      "results/tables/time/",
      gsub(" ", "_", cen), "_",
      gsub(" ", "_", op),
      "_tabela.png"
    )
    
    # Cria a tabela gráfica
    tabela_grob_tempo <- tableGrob(tabela_larga_tempo, rows = NULL, theme = tema_estilizado)
    tabela_grob_tempo$widths[4] <- unit(2.2, "in")

    # Título da tabela
    titulo_texto <- paste("Cenário:", cen, "| Operação:", op)
    titulo_grob <- textGrob(
      titulo_texto, 
      gp = gpar(fontsize = 16, fontface = "bold", col = "#333333")
    )

    # Salva a imagem
    png(filename = nome_arquivo, width = 1280, height = 280, res = 100)
    
    grid.arrange(titulo_grob, tabela_grob_tempo, nrow = 2, heights = c(0.15, 0.85))
    
    dev.off()
  }
}

print("Tabelas de tempo processadas com sucesso!")