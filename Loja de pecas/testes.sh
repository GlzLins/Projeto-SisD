#!/bin/bash

echo "🧪 Compilando o projeto e os Testes Unitários..."
# Compila incluindo o JUnit e o Banco de Dados no Classpath
javac -cp "sqlite-jdbc-3.42.0.0.jar:junit-4.13.2.jar:src" -d bin src/*.java

echo "⚙️ Rodando a Bateria de Testes..."
echo "---------------------------------------------------"
# Executa a ferramenta JUnitRunner apontando para a nossa classe de teste
java -cp "bin:sqlite-jdbc-3.42.0.0.jar:junit-4.13.2.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore EstoqueDAOTest
echo "---------------------------------------------------"