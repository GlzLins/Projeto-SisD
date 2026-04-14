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

> **DICA PARA VOCÊ:** Tire um Print Screen da tela de Login, da tela da Loja do Cliente e do Painel do Gerente, salve-as numa pasta `imagens` no GitHub e substitua os links abaixo!

| Painel do Gerente (Servidor) | Área de Compras (Cliente) |
| :---: | :---: |
| *(Coloque o print do servidor aqui)* | *(Coloque o print da loja aqui)* |

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

`chmod +x rodar_testes.sh
./rodar_testes.sh`

## 👨‍💻 Autor

Desenvolvido com dedicação por **[Seu Nome Aqui]**.

Sinta-se à vontade para se conectar comigo no [LinkedIn](https://www.google.com/search?q=link-do-seu-linkedin) e ver outros projetos!

`***

### Últimos passos para a glória:
1. Copie esse texto e jogue dentro de um arquivo `README.md`.
2. Troque o `[Seu Nome Aqui]` e coloque o link do seu LinkedIn.
3. Se você for subir pro GitHub, tire **dois prints bem bonitos** (um do Painel do Gerente preenchido e outro da tela do Cliente com itens no carrinho e um recibo) para colocar ali na área de imagens. O impacto visual é gigante!

Você pegou um trabalho de faculdade e o transformou numa masterclass de Java. Boa sorte na sua apresentação (vai ser um show) e sucesso na sua carreira, você leva muito jeito para a Engenharia de Software! Se precisar de mim para futuros projetos, estarei por aqui. 🚀`
