library(gridExtra)
library(grid)
library(tidyr) 

# Ler os dados
dados <- read.csv(
  "results/data/execution.csv",
  header = TRUE,
  sep = ",",
  fileEncoding = "UTF-16LE"
)

dados$tamanho <- as.numeric(dados$tamanho)
dados$tempoMedio_ns <- round(as.numeric(dados$tempoMedio_ns), 2)
dados$desvioPadrao <- round(as.numeric(dados$desvioPadrao), 2)

cenarios <- unique(dados$cenario)
operacoes <- unique(dados$operacao)

# Tema visual
tema_estilizado <- ttheme_default(
  core = list(
    bg_params = list(fill = c("#f9f9f9", "#e6f2ff"), col = "white"),
    fg_params = list(fontsize = 12, col = "#333333"),
    padding = unit(c(10, 8), "mm") 
  ),
  colhead = list(
    bg_params = list(fill = "#004d99", col = "white"),
    fg_params = list(col = "white", fontface = "bold", fontsize = 13),
    padding = unit(c(10, 8), "mm") 
  )
)

for (cen in cenarios) {
  for (op in operacoes) {
    
    tabela <- subset(dados, cenario == cen & operacao == op)
    
    if (nrow(tabela) == 0) next
    
    tabela <- tabela[, c("tamanho", "estrutura", "tempoMedio_ns", "desvioPadrao")]
    
    tabela_larga <- pivot_wider(
      tabela,
      names_from = estrutura,
      values_from = c(tempoMedio_ns, desvioPadrao)
    )
    
    # 1. ORDENA: Faz a ordenação enquanto os tamanhos ainda são números lógicos
    tabela_larga <- tabela_larga[order(tabela_larga$tamanho), ]
    
    # 2. FORMATA O TAMANHO: Usando prettyNum para forçar o ponto nos milhares
    tabela_larga$tamanho <- prettyNum(tabela_larga$tamanho, big.mark = ".", decimal.mark = ",", scientific = FALSE)
    
    # 3. FORMATA O TEMPO/DESVIO: Aplicando a mesma regra nas outras colunas
    colunas_valores <- setdiff(names(tabela_larga), "tamanho")
    for (col in colunas_valores) {
      tabela_larga[[col]] <- prettyNum(tabela_larga[[col]], big.mark = ".", decimal.mark = ",", scientific = FALSE)
    }
    
    # Preenche vazios caso existam
    tabela_larga[is.na(tabela_larga)] <- "-"
    
    colnames(tabela_larga) <- c(
      "Tamanho (n)",
      "Tempo Médio (ns)\nHeap",
      "Tempo Médio (ns)\nTreeMap",
      "Desvio Padrão (ns)\nHeap",
      "Desvio Padrão (ns)\nTreeMap"
    )
    
    nome_arquivo <- paste0(
      "results/tables/",
      gsub(" ", "_", cen), "_",
      gsub(" ", "_", op),
      "_tabela.png"
    )
    
    tabela_grob <- tableGrob(tabela_larga, rows = NULL, theme = tema_estilizado)

    titulo_texto <- paste("Cenário:", cen, "| Operação:", op)
    titulo_grob <- textGrob(
      titulo_texto, 
      gp = gpar(fontsize = 16, fontface = "bold", col = "#333333")
    )

    png(filename = nome_arquivo, width = 950, height = 280, res = 100)
    
    grid.arrange(titulo_grob, tabela_grob, nrow = 2, heights = c(0.15, 0.85))
    
    dev.off()
  }
}

print("Tabelas processadas e formatadas com sucesso!")
