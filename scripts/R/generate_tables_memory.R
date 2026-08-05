# ==============================================================================
# Gerar tabelas comparativas de consumo de memória (Heap vs TreeMap)
# ==============================================================================
library(gridExtra)
library(grid)
library(tidyr) 

# ------------------------------------------------------------------------------
# Leitura e limpeza dos dados
# ------------------------------------------------------------------------------
# Lê o arquivo CSV com os resultados dos experimentos de consumo de memória
# gerados pelo Benchmark.
dados_memoria <- read.csv(
  "results/data/executionMemory.csv",
  header = TRUE, sep = ","
)

# Converte as colunas númericas para o tipo adequado
dados_memoria$tamanho <- as.numeric(dados_memoria$tamanho)
dados_memoria$memoriaMedia_b <- round(as.numeric(dados_memoria$memoriaMedia_b), 2)
dados_memoria$desvioPadrao <- round(as.numeric(dados_memoria$desvioPadrao), 2)

# Cenários presentes no arquivo
cenarios_memoria <- unique(dados_memoria$cenario)

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
# Geração das tabelas de memória
# ------------------------------------------------------------------------------
# Para cada combinação de cenário é criada uma tabela contendo: 
# Consumo médio de memória; Desvio padrão do consumo e 
# Comparação entre as duas estruturas para cada tamanho de entrada.
for (cen in cenarios_memoria) {
  tabela_memoria <- subset(dados_memoria, cenario == cen)

  if (nrow(tabela_memoria) == 0) next

    # Seleciona apenas as colunas relevantes
    tabela_memoria <- tabela_memoria[, c("tamanho", "estrutura", "memoriaMedia_b", "desvioPadrao")]
    
    # Coloca Heap e TreeMap lado a lado
    tabela_larga_memoria <- pivot_wider(
      tabela_memoria,
      names_from = estrutura,
      values_from = c(memoriaMedia_b, desvioPadrao)
    )
    
    # Ordena pelo tamanho da entrada
    tabela_larga_memoria <- tabela_larga_memoria[order(tabela_larga_memoria$tamanho), ]
    
    # Calcula quantas vezes o TreeMap foi mais lento (ou mais rápido)
    tabela_larga_memoria$razao <- tabela_larga_memoria$memoriaMedia_b_TreeMap / tabela_larga_memoria$memoriaMedia_b_MinHeap
    
    # Formata a razão com 2 casas decimais
    tabela_larga_memoria$razao <- paste0(format(round(tabela_larga_memoria$razao, 2), nsmall = 2, decimal.mark = ","), "x")

    # Reordena as colunas
    tabela_larga_memoria <- tabela_larga_memoria[, c(
      "tamanho",
      "memoriaMedia_b_MinHeap", 
      "memoriaMedia_b_TreeMap", 
      "razao", 
      "desvioPadrao_MinHeap", 
      "desvioPadrao_TreeMap"
    )]

    # Formatação  do tamanho da entrada em potência de 10
    tabela_larga_memoria$tamanho <- paste0("10^", log10(tabela_larga_memoria$tamanho))

    # Formata tempos e desvios
    colunas_valores <- setdiff(names(tabela_larga_memoria), "tamanho")
    for (col in colunas_valores) {
      tabela_larga_memoria[[col]] <- prettyNum(tabela_larga_memoria[[col]], big.mark = ".", decimal.mark = ",", scientific = FALSE)
    }
    
    # Substitui valores ausentes
    tabela_larga_memoria[is.na(tabela_larga_memoria)] <- "-"
    
    # Renomeia as colunas
    colnames(tabela_larga_memoria) <- c(
      "Tamanho (n)",
      "Memória Média (b)\nHeap",
      "Memória Média (b)\nTreeMap",
      "Razão\n(TreeMap / Heap)",
      "Desvio Padrão (b)\nHeap",
      "Desvio Padrão (b)\nTreeMap"
    )
    
    # ------------------------------------------------------------------------------
    # Exportação para PNG
    # ------------------------------------------------------------------------------
    nome_arquivo <- paste0(
      "results/tables/memory/",
      gsub(" ", "_", cen), "_",
      "memoria_tabela.png"
    )
    
    # Cria a tabela gráfica
    tabela_grob_memoria <- tableGrob(tabela_larga_memoria, rows = NULL, theme = tema_estilizado)
    tabela_grob_memoria$widths[4] <- unit(2.2, "in")

    # Título da tabela
    titulo_texto <- paste("Cenário:", cen)
    titulo_grob <- textGrob(
      titulo_texto, 
      gp = gpar(fontsize = 16, fontface = "bold", col = "#333333")
    )

    # Salva a imagem
    png(filename = nome_arquivo, width = 1280, height = 280, res = 100)
    
    grid.arrange(titulo_grob, tabela_grob_memoria, nrow = 2, heights = c(0.15, 0.85))
    
    dev.off()
}

print("Tabelas de memória geradas com sucesso!")
