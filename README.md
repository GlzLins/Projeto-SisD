`# 🚗 AutoPeças Express - Sistema Distribuído

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Sockets](https://img.shields.io/badge/Sockets-TCP%2FUDP-blue?style=for-the-badge)
![JUnit](https://img.shields.io/badge/JUnit-Automated%20Tests-green?style=for-the-badge)

Um sistema de gerenciamento e vendas de peças automotivas com arquitetura Cliente-Servidor. Desenvolvido para demonstrar conceitos avançados de **Sistemas Distribuídos**, **Persistência de Dados**, **Segurança** e **Padrões de Projeto**.

## 📌 Principais Funcionalidades e Arquitetura

O projeto foi construído do zero, evoluindo de arquivos de texto simples para uma arquitetura robusta baseada em mercado:

* **Comunicação Híbrida (Redes):** * **TCP (Unicast):** Para transações seguras de login, listagem de catálogo e processamento do carrinho de compras em formato **JSON**.
    * **UDP (Multicast):** Rádio de transmissão em **Tempo Real (Real-Time)**. Quando o gerente altera o estoque ou envia um aviso, todos os clientes logados têm suas telas atualizadas simultaneamente.
* **Segurança (Cibersegurança):** O sistema não salva senhas em texto limpo. Utiliza o algoritmo de hash **SHA-256** (Efeito Avalanche) para proteger os dados dos usuários.
* **Persistência Relacional:** Implementação da biblioteca **JDBC** conectada nativamente a um banco de dados **SQLite** (`loja_pecas.db`).
* **Padrões de Projeto (Design Patterns):** * `MVC (Model-View-Controller)` no aplicativo do cliente para separar regras de negócio da Interface Gráfica (Swing).
    * `DAO (Data Access Object)` no servidor para isolar a manipulação de SQL.
* **Testes Automatizados (CI/CD):** Bateria de testes unitários construída com **JUnit 4**, validando inserções, leituras e remoções do banco de dados em milissegundos.
* **Manipulação de Arquivos (I/O):** Geração automática de recibos fiscais detalhados em `.txt` na máquina do cliente.

## 🖼️ Telas do Sistema


| Área de Compras (Cliente) | <img width="1920" height="975" alt="cliente" src="https://github.com/user-attachments/assets/fa062599-38d4-450e-98d7-eef3e1175bf4" />

| Painel do Gerente (Servidor)  | <img width="1920" height="975" alt="servidor" src="https://github.com/user-attachments/assets/5ec9827b-0023-4658-b011-ed23b44cc85c" />

 |

## ⚙️ Como Executar o Projeto

O projeto foi construído em ambiente Linux e automatizado via **Shell Scripts** para facilitar o build.

**Pré-requisitos:**
- Java Development Kit (JDK) instalado.
- Bibliotecas JDBC e JUnit (já inclusas via script ou devem estar na raiz).

**1. Para rodar a aplicação completa (Servidor + Cliente):**
No terminal, dê permissão de execução e rode o script:
```bash
chmod +x iniciar_loja.sh
./iniciar_loja.sh`

**2. Para rodar a Bateria de Testes Unitários:**

Bash

# 

`chmod +x testes.sh
./testes.sh`

## 👨‍💻 Autor

Desenvolvido com dedicação por **Gleydson Rodrigues Lins*.




