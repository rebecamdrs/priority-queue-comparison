#!/bin/bash

echo "==========================================="
echo "INICIANDO BENCHMARK DAS FILAS DE PRIORIDADE"
echo "==========================================="
echo ""

echo "[0/4] Preparando estrutura de pastas..."
mkdir -p out
mkdir -p results/data
mkdir -p results/plots/time/combined
mkdir -p results/plots/time/individual
mkdir -p results/plots/memory/combined
mkdir -p results/plots/memory/individual
mkdir -p results/tables/time
mkdir -p results/tables/memory

echo "[1/4] Compilando arquivos Java..."
javac -d out src/main/java/model/*.java src/main/java/heap/*.java src/main/java/treemap/*.java src/main/java/experiment/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] Falha na compilacao do Java! Verifique seu codigo."
    read -p "Pressione Enter para sair..."
    exit 1
fi

echo "[1.5/4] Executando testes e gerando CSV..."
java -cp out experiment.TimeBenchmark > results/data/executionTime.csv
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] O TimeBenchmark falhou durante a execucao!"
    read -p "Pressione Enter para sair..."
    exit 1
fi

java -cp out experiment.MemoryBenchmark > results/data/executionMemory.csv
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] O MemoryBenchmark falhou durante a execucao!"
    read -p "Pressione Enter para sair..."
    exit 1
fi

echo "[2/4] Gerando graficos agrupados..."
Rscript scripts/R/plot_combined.R

echo "[3/4] Gerando graficos individuais..."
Rscript scripts/R/plot_individual.R

echo "[4/4] Gerando tabelas comparativas..."
Rscript scripts/R/generate_tables.R

echo ""
echo "==========================================="
echo "SUCESSO! Verifique a pasta 'results'."
echo "==========================================="
read -p "Pressione Enter para sair..."