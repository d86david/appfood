# 🍕 AppFood — Sistema de Gestão para Pizzaria

> Sistema backend para administração completa de uma pizzaria: pedidos, cardápio, caixa, entregas e muito mais.

---

## 📋 Sobre o Projeto

O **AppFood** é uma API REST desenvolvida em **Java com Spring Boot**, criada para gerenciar as operações do dia a dia de uma pizzaria. O sistema cobre desde o controle do cardápio e pedidos até o gerenciamento de caixa com autorização de gerente.

O projeto foi desenvolvido com foco em boas práticas de arquitetura, separação em camadas (Model → Repository → Service → Controller) e aplicação de princípios como responsabilidade única (SRP) e domínio rico (Rich Domain Model).

---

## ✨ Funcionalidades

### 🛒 Pedidos
- Abertura de pedidos para **balcão** (com ou sem cadastro de cliente) e **delivery**
- Suporte a pizzas com **até 3 sabores** (cobrança pelo maior valor ou média)
- Personalização de ingredientes por sabor (adição e remoção)
- Opção de **borda recheada** com acréscimo de valor
- Histórico de status do pedido com rastreabilidade completa
- Fluxo de status: `PEDIDO_INICIADO` → `PENDENTE` → `EM_PREPARAÇÃO` → `PRONTO` → `SAIU_PARA_ENTREGA` → `FINALIZADO`
- Cancelamento de pedidos em aberto
- Aplicação de descontos por item com autorização do gerente

### 🍕 Cardápio
- Gerenciamento de **categorias** (Pizzas, Fogazzas, Esfihas, Lanches, Porções, Bebidas)
- Cadastro de **produtos** com preços variáveis por tamanho
- Controle de **ingredientes** com valor adicional para extras
- Composição padrão por produto (receita base)
- Flag `imprimeCozinha` — controla se o item vai para a impressora da cozinha

### 💰 Caixa
- Abertura e fechamento com **autenticação do gerente**
- Registro de vendas vinculadas ao caixa do operador
- **Sangria** autorizada por gerente com motivo obrigatório
- Sangria automática no fechamento do caixa
- Histórico completo de movimentações (entradas e saídas)

### 👥 Usuários
- Perfis: `OPERADOR`, `GERENTE`
- Autenticação com senha protegida por **BCrypt**
- Controle de autorização por perfil nas operações sensíveis

### 🚴 Entrega
- Cadastro de entregadores com valor de taxa por entrega
- Relatório de entregas por entregador no fechamento do caixa

---

## 🏗️ Arquitetura

O projeto segue a arquitetura em camadas do Spring Boot:

```
┌─────────────────────────────────────────────┐
│                  Controller                 │  ← Recebe requisições HTTP
├─────────────────────────────────────────────┤
│                   Service                   │  ← Regras de negócio
├─────────────────────────────────────────────┤
│                  Repository                 │  ← Acesso ao banco de dados
├─────────────────────────────────────────────┤
│               Model / Entity                │  ← Estrutura dos dados
└─────────────────────────────────────────────┘
```

### Princípios aplicados

- **SRP** — cada Service tem uma única responsabilidade
- **Rich Domain Model** — as entidades conhecem suas próprias regras
- **Injeção de dependência via construtor** — campos `final`, testabilidade garantida
- **DTOs** — separação entre o modelo interno e o contrato público da API
- **Fail Fast** — validações baratas antes de consultar o banco

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/dsys/appfood/
│
├──📂 confi/
│  └── SecurityConfig.java            # BCrypt + configuração de segurança
│
├──📂controller/                      # Endpoints HTTP (em construção)
│   ├── BordaController.java      
│   ├── CaixaController.java
│   ├── CategoriaController.java
│   ├── ContaController.java
│   ├── ProdutoController.java
│   ├── TamanhoController.java   
│   └── UsuarioController.java
│   
│
├── 📂domain/
│   ├── 📂enums/
│   │   ├── FormaPagamento.java      
│   │   ├── StatusCaixa.java         
│   │   ├── StatusPedido.java  
│   │   ├── TipoConta.java         
│   │   ├── TipoCustomizacao.java
│   │   ├── TipoDocumento.java       
│   │   ├── TipoMovimentacao.java    
│   │   ├── TipoPedido.java          
│   │   └── TipoUsuario.java         
│   │
│   └── 📂model/
│       ├── Caixa.java              	# ciclo de vida do caixa
│       ├── Categoria.java          	# agrupamento de produtos
│       ├── Cliente.java            	# dados do cliente delivery
│       ├── ComposicaoPadrao.java    	# receita base do produto
│       ├── Endereco.java            	# endereço de entrega
│       ├── Entregador.java          	# entregador e taxa
│       ├── Ingrediente.java         	# ingrediente com valor adicional
│       ├── ItemCustomizacao.java    	# modificação em um sabor
│       ├── ItemPedido.java          	# uma pizza dentro do pedido
│       ├── MovimentacaoCaixa.java   	# entrada/saída no caixa
│       ├── Pagamento.java           	# pagamento vinculado ao pedido
│       ├── Pedido.java              	# pedido completo com histórico
│       ├── PrecoVariavel.java       	# preço do produto por tamanho
│       ├── Produto.java             	# produto do cardápio
│       ├── StatusPedidoHistorico.java 	# histórico de mudanças de status
│       ├── SubItemSabor.java        	# sabor dentro de um item
│       ├── Tamanho.java             	# tamanho do produto
│       └── Usuario.java             	# usuário do sistema
│
├── 📂dto/                             	# Data Transfer Objects
│   ├── 📂request/     	
│   │	├── BordaRequest.java
│   │	├── CaixaAbrirRequest.java
│   │	├── CaixaEstornoRequest.java
│   │	├── CaixaFecharRequest.java
│   │	├── CaixaSangriaRequest.java
│   │	├── CategoriaRequest.java
│   │	├── ContaCorrenteEstornoRequest.java
│   │	├── ContaCorrenteRequest.java
│   │	├── ContaStatusRequest.java
│   │	├── PagamentoRequest.java
│   │	├── PrecoTamanhoRequest.java
│   │	├── ProdutoRequest.java
│   │	├── TamanhoRequest.java
│   │	├── UsuarioAlterarSenhaRequest.java
│   │	├── UsuarioCadastroRequest.java
│   │	├── UsuarioEdicaoRequest.java
│   │	├── UsuarioStatusRequest.java
│   │	└── UsuarioLoginRequest.java
│   │
│   └── 📂response/
│    	├── BordaResponse.java
│    	├── CaixaResponse.java
│    	├── CaixaStatusResponse.java
│    	├── CategoriaResponse.java
│    	├── ContaCorrenteResponse.java
│    	├── DesempenhoEntregadorResponse.java
│    	├── IndicadoresDiariosResponse.java
│    	├── MovimentacaoCaixaResponse.java
│    	├── MovimentacaoContaResponse.java
│    	├── PagamentoComTrocoResponse.java
│    	├── PagamentoResponse.java
│    	├── PedidoResumoResponse.java
│    	├── PrecoTamanhoResponse.java
│    	├── ProdutoResponse.java
│    	├── ProdutoVendidoResponse.java
│    	├── ResumoCaixaResponse.java
│    	├── ResumoContaCorrenteResponse.java
│    	├── TamanhoResponse.java
│    	├── UsuarioResponse.java
│    	└── VendasPeriodoResponse.java
│
├── 📂exception/                      
│   ├── BordaJaCadastradaException.java
│   ├── BordaNaoEncontradaException.java
│   ├── CaixaFechadoException.java
│   ├── CaixaNaoEncontradoException.java
│   ├── CategoriaJaCadastradaException.java
│   ├── CategoriaNaoEncontradaException.java
│   ├── ClienteJaCadastradoException.java
│   ├── ClienteNaoEncontradoException.java
│   ├── EnderecoNaoEncontradoException.java
│   ├── EntidadeJaCadastradaException.java
│   ├── EntidadeNaoEncontradaException.java
│   ├── EntregadorNaoEncontradoException.java
│   ├── ErroResponse.java
│   ├── GlobalExceptionHandler.java
│   ├── IngredienteJaCadastradoException.java
│   ├── IngredienteNaoEncontradoException.java
│   ├── MesaJaCadastradaException.java
│   ├── MesaNaoEncontradaException.java
│   ├── NegocioException.java
│   ├── NenhumCaixaAbertoException.java
│   ├── PagamentoNaoEncontradoException.java
│   ├── PedidoNaoEncontradoException.java
│   ├── PrecoVariavelNaoEncontradoException.java
│   ├── ProdutoNaoEncontradoException.java
│   ├── TamanhoJaCadastradoException.java
│   ├── TamanhoNaoEncontradoException.java
│   └── UsuarioNaoEncontradoException.java
│
├── 📂mapper/                          # Conversores Entity ↔ DTO (em construção)
│
├── 📂repository/                      # Interfaces Spring Data JPA
│   ├── BordaRepository.java
│   ├── CaixaRepository.java
│   ├── CategoriaRepository.java
│   ├── ClienteRepository.java
│   ├── ComposicaoPadraoRepository.java
│   ├── ConfiguracaoPagamentoRepository.java
│   ├── ContaCorrenteRepository.java
│   ├── EnderecoRepository.java
│   ├── EntregadorRepository.java
│   ├── IngredienteRepository.java
│   ├── ItemCustomizacaoRepository.java
│   ├── ItemPedidoRepository.java
│   ├── MesaRepository.java
│   ├── MovimentacaoCaixaRepository.java
│   ├── MovimentacaoContaCorrenteRepository.java
│   ├── PagamentoRepository.java
│   ├── PedidoRepository.java
│   ├── PrecoVariavelRepository.java
│   ├── ProdutoRepository.java
│   ├── StatusPedidoHistoricoRepository.java
│   ├── SubItemSaborRepository.java
│   ├── TamanhoRepository.java
│   └── UsuarioRepository.java
│
└── 📂service/                       # Camada de negócio
     ├── BordaService.java   
     ├── CaixaService.java 	
     ├── CategoriaService.java
     ├── ClienteService.java
     ├── ComposicaoPadraoService.java
     ├── ContaCorrenteService.java
     ├── DashboardService.java
     ├── EnderecoService.java
     ├── EntregadorService.java
     ├── IngredienteService.java
     ├── MesaService.java
     ├── MovimentacaoCaixaService.java
     ├── MovimentacaoContaCorrenteService.java
     ├── PagamentoService.java
     ├── PedidoService.java
     ├── PrecoVariavelService.java	 
     ├── ProdutoService.java      
     ├── RelatorioService.java
     ├── StatusPedidoHistoricoService.java	 
     ├── TamanhoService.java          
     └── UsuarioService.java         
```

---

## 🛠️ Tecnologias

| Tecnologia  	  | Versão | Uso 						   |
|-----------------|--------|-------------------------------|
| Java 		  	  | 17 	   | Linguagem principal 		   |
| Spring Boot 	  | 4.0.3  | Framework principal 		   |
| Spring Data JPA | — 	   | Persistência e repositórios   |
| Spring Security | —      | BCrypt para senhas     	   |
| Hibernate 	  | 7.2.4  | ORM 						   |
| MySQL 		  | 5.7+   | Banco de dados 		       |
| HikariCP 		  | — 	   | Pool de conexões 			   |
| Maven  	 	  | — 	   | Gerenciamento de dependências |

---

## ⚙️ Como executar

### Pré-requisitos

- Java 17 ou superior
- MySQL 5.7 ou superior
- Maven 3.8+

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/appfood.git
cd appfood
```

### 2. Configure o banco de dados

Crie o banco no MySQL:

```sql
CREATE DATABASE appfood;
```

### 3. Configure o `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/appfood?useSSL=false&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### 4. Execute o projeto

```bash
mvn spring-boot:run
```

O servidor sobe em `http://localhost:8080`

---

## 📐 Modelo de Dados — Principais Relacionamentos

```
Categoria ──< Produto ──< PrecoVariavel >── Tamanho
                │
                └──< ComposicaoPadrao >──< Ingrediente

Pedido ──< ItemPedido ──< SubItemSabor ──< ItemCustomizacao >── Ingrediente
  │              └── Tamanho
  │
  ├──< Pagamento >── Caixa
  ├──< StatusPedidoHistorico
  ├── Cliente >── Endereco
  ├── Entregador
  └── Usuario (operador / gerente)

Caixa ──< MovimentacaoCaixa
  ├── Usuario (operador)
  └── Usuario (gerente autorizador)
```

---

## 🔐 Segurança

- Senhas protegidas com **BCrypt** (fator de custo 12)
- Operações sensíveis (abertura/fechamento de caixa, sangria, descontos) requerem **autenticação do gerente** no momento da ação
- Nenhuma senha trafega em texto puro em nenhuma camada do sistema

---

## 🚧 Status do Projeto

O projeto está em desenvolvimento ativo. Camadas concluídas:

- [x] Model — todas as entidades mapeadas com JPA
- [x] Repository — todos os repositórios com queries customizadas
- [x] Services — todos os services completos com as regras de negócio
- [ ] DTOs completos (em andamento)
- [ ] Controllers (em andamento)
- [x] Tratamento global de exceções
- [ ] Documentação da API (Swagger)

---

## 👨‍💻 Autor

Desenvolvido por **David** como projeto de estudo de Java com Spring Boot.

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
