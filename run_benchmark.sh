#!/bin/bash

echo "==========================================="
echo "INICIANDO BENCHMARK DAS FILAS DE PRIORIDADE"
echo "==========================================="
echo ""

echo "[0/4] Preparando estrutura de pastas..."
mkdir -p out
mkdir -p lib
mkdir -p results/data
mkdir -p results/plots/time/combined
mkdir -p results/plots/time/individual
mkdir -p results/plots/memory/combined
mkdir -p results/plots/memory/individual
mkdir -p results/tables/time
mkdir -p results/tables/memory

# Baixa o JOL (Java Object Layout) se ainda não existir. Usado só pelo MemoryBenchmark,
# pra medir o tamanho real dos objetos sem depender do Garbage Collector.
JOL_JAR="lib/jol-core-0.17.jar"
if [ ! -f "$JOL_JAR" ]; then
    echo "[0.5/4] Baixando dependencia JOL..."
    curl -sL -o "$JOL_JAR" https://repo1.maven.org/maven2/org/openjdk/jol/jol-core/0.17/jol-core-0.17.jar
fi

echo "[1/4] Compilando arquivos Java..."
javac -cp "$JOL_JAR" -d out src/main/java/model/*.java src/main/java/heap/*.java src/main/java/treemap/*.java src/main/java/experiment/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] Falha na compilacao do Java! Verifique seu codigo."
    read -p "Pressione Enter para sair..."
    exit 1
fi

echo "[1.5/4] Executando testes e gerando CSV..."
java -cp "out:$JOL_JAR" experiment.TimeBenchmark > results/data/executionTime.csv
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] O TimeBenchmark falhou durante a execucao!"
    read -p "Pressione Enter para sair..."
    exit 1
fi

java -cp "out:$JOL_JAR" experiment.MemoryBenchmark | grep -v '^#' > results/data/executionMemory.csv
JAVA_EXIT=${PIPESTATUS[0]}

if [ $JAVA_EXIT -ne 0 ]; then
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