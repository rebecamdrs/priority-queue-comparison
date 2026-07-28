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
Rscript scripts/R/plot_combined.R

echo [3/4] Gerando graficos individuais...
Rscript scripts/R/plot_individual.R

echo [4/4] Gerando tabelas comparativas...
Rscript scripts/R/generate_tables.R

echo.
echo ===========================================
echo SUCESSO! Verifique a pasta 'results'.
echo ===========================================
pause