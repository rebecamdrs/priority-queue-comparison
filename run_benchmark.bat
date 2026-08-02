@echo off
REM Força o terminal a usar UTF-8, garantindo que acentos funcionem perfeitamente
chcp 65001 > nul

echo ===========================================
echo INICIANDO BENCHMARK DAS FILAS DE PRIORIDADE
echo ===========================================

echo.
echo [0/4] Preparando estrutura de pastas...
if not exist "out" mkdir "out"
if not exist "results\data" mkdir "results\data"
if not exist "results\plots\combined" mkdir "results\plots\combined"
if not exist "results\plots\individual" mkdir "results\plots\individual"
if not exist "results\tables" mkdir "results\tables"

REM Baixa o JOL (Java Object Layout) se ainda nao existir. Usado so pelo MemoryBenchmark,
REM pra medir o tamanho real dos objetos sem depender do Garbage Collector.
set "JOL_JAR=lib\jol-core-0.17.jar"

if not exist "%JOL_JAR%" (
    echo [0.5/4] Baixando dependencia JOL...
    REM Cria a pasta lib caso ela ainda nao exista para evitar erro no download
    if not exist "lib" mkdir "lib"
    curl -sL -o "%JOL_JAR%" https://repo1.maven.org/maven2/org/openjdk/jol/jol-core/0.17/jol-core-0.17.jar
)

echo [1/4] Compilando arquivos Java...
javac -d out src/main/java/model/*.java src/main/java/heap/*.java src/main/java/treemap/*.java src/main/java/experiment/*.java

REM Se o javac falhar (código com erro), ele avisa e aborta antes de rodar os testes
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] Falha na compilacao do Java! Verifique seu codigo.
    pause
    exit /b %ERRORLEVEL%
)

echo [1.5/4] Executando testes e gerando CSV...
java -cp out experiment.Benchmark > results/data/execution.csv

REM Se o Java estourar alguma exceção durante o teste, ele também aborta
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] O Benchmark falhou durante a execucao!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/4] Gerando graficos agrupados...
Rscript scripts/R/plot_combined_time.R
Rscript scripts/R/plot_combined_memory.R

echo [3/4] Gerando graficos individuais...
Rscript scripts/R/plot_individual_time.R
Rscript scripts/R/plot_individual_memory.R

echo [4/4] Gerando tabelas comparativas...
Rscript scripts/R/generate_tables_time.R
Rscript scripts/R/generate_tables_memory.R

echo.
echo ===========================================
echo SUCESSO! Verifique a pasta 'results'.
echo ===========================================
pause