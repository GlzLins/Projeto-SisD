#!/bin/bash

echo "🧹 Limpando processos antigos..."
killall java 2>/dev/null

echo "⚙️ Compilando o projeto com o Driver do Banco de Dados..."
# O comando agora inclui o driver SQLite no Classpath (separado por : no Linux)
if ! javac -cp "sqlite-jdbc-3.42.0.0.jar:src" -d bin src/*.java; then
    echo "❌ Erro na compilação!"
    exit 1
fi

echo "🚀 Iniciando o Servidor (Painel do Gerente)..."
# Executa o servidor carregando a pasta bin E o driver do SQLite
java -cp "bin:sqlite-jdbc-3.42.0.0.jar" ServidorGUI &
SERVER_PID=$!

echo "⏳ Aguardando o servidor subir..."
sleep 2

echo "🛒 Abrindo o aplicativo do Cliente..."
# O cliente não liga direto ao banco, só precisa da pasta bin
java -cp bin ClienteGUI

echo "🛑 Fechando o Servidor em segundo plano..."
kill $SERVER_PID 2>/dev/null

echo "✅ Sistema encerrado."