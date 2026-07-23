package com.dsys.appfood.service;

import java.math.BigDecimal;
//import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.config.FormatadorCupomUtil;
import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.enums.TipoCustomizacao;
import com.dsys.appfood.domain.model.Impressora;
//import com.dsys.appfood.domain.model.ItemCustomizacao;
import com.dsys.appfood.domain.model.ItemPedido;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.SubItemSabor;
import com.dsys.appfood.dto.response.ImpressaoBalcaoResponse;
import com.dsys.appfood.dto.response.ImpressaoCozinhaResponse;
import com.dsys.appfood.dto.response.ImpressaoResponse;

/**
 * Serviço de impressão com roteamento de cupons.
 * 
 * CONCEITO: Separação de Responsabilidades (SRP - SOLID) Esta classe é
 * responsável APENAS por formatar textos de cupons e roteá-los para as
 * impressoras corretas. Ela NÃO sabe como salvar o pedido no banco, nem como
 * processar pagamentos.
 * 
 * REGRAS DE NEGÓCIO IMPLEMENTADAS AQUI: 1. Roteamento por Categoria: Pizzas vão
 * para a impressora "PIZZAS", Bebidas vão para "BALCAO" (ou "GERAL"). 2. Filtro
 * de Cozinha: Itens sem impressora definida ou marcados como "GERAL" NÃO são
 * impressos na cozinha, apenas no recibo do balcão. 3. Regra do Maior Valor: No
 * balcão, o valor da pizza é o do sabor mais caro + adicionais (delegado para
 * ItemPedido.calcularPrecoFinal). 4. Fração de Sabores: Pizzas com 2 ou mais
 * sabores exibem "1/2", "1/3".
 */
@Service
public class ImpressaoService {

	private final ImpressoraCanalService impressoraCanalService;
	private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private final PedidoService pedidoService;

	public ImpressaoService(PedidoService pedidoService, ImpressoraCanalService impressoraCanalService) {
		this.pedidoService = pedidoService;
		this.impressoraCanalService = impressoraCanalService;
	}

	// =============================================================
	// CONSTANTES DE FORMATAÇÃO
	// =============================================================
	//private static final String FORMATO_MOEDA = "R$%6.2f"; // Sempre 8 caracteres

	// =============================================================
	// MÉTODOS PÚBLICOS (FACHADA)
	// =============================================================

	/**
	 * Gera e envia as impressões completas do pedido (Cozinha + Balcão). Disparado
	 * automaticamente quando o pedido muda para PENDENTE.
	 */

	@Transactional(readOnly = true)
	public ImpressaoResponse imprimirPedido(Integer pedidoId) {

		// Busca o pedido com dados específicos para impressão
		Pedido pedido = pedidoService.buscarPedidoCompleto(pedidoId);

		// 1. Imprime a cozinha (roteada por categoria)
		Map<String, String> conteudosCozinha = imprimirCupomCozinha(pedido);

		// 2. Imprime o balcão (via única)
		String conteudoBalcao = imprimirCupomBalcao(pedido);

		// enviarParaImpressora(conteudoBalcao, "BALCÃO");

		return new ImpressaoResponse(conteudosCozinha, conteudoBalcao);
	}

	// IMPRIMIR PEDIDO BALCÃO
	/**
	 * Gera e envia a impressão do pedido para o balcão: uma única via com todos os
	 * dados.
	 */
	@Transactional(readOnly = true)
	public ImpressaoBalcaoResponse imprimirPedidoBalcao(Integer pedidoId) {
		Pedido pedido = pedidoService.buscarPedidoCompleto(pedidoId);

		// Imprime o balcão (via única)
		String conteudoBalcao = imprimirCupomBalcao(pedido);

		return new ImpressaoBalcaoResponse(conteudoBalcao);
	}

	// IMPRIMIR PEDIDO COZINHA
	/**
	 * Gera e envia a impressão do pedido para a cozinha: roteada por categoria
	 * (cada categoria vai para sua impressora).
	 *
	 */
	@Transactional(readOnly = true)
	public ImpressaoCozinhaResponse imprimirPedidoCozinha(Integer pedidoId) {
		Pedido pedido = pedidoService.buscarPedidoCompleto(pedidoId);

		// Imprime a cozinha (roteada por categoria)
		Map<String, String> conteudosCozinha = imprimirCupomCozinha(pedido);

		return new ImpressaoCozinhaResponse(conteudosCozinha);
	}

	// =============================================================
	// ORQUESTRAÇÃO DA COZINHA (ROTEAMENTO)
	// =============================================================
	/**
	 * Gera e envia a impressão do pedido para a cozinha, separando por
	 * categoria/impressora.
	 *
	 * @param pedido Pedido a ser impresso.
	 * @return Mapa com a impressora como chave e o conteúdo como valor (para
	 *         debug).
	 */
	private Map<String, String> imprimirCupomCozinha(Pedido pedido) {

		// 1. Agrupa os itens por impressora (baseado na categoria do produto)
		Map<String, List<SubItemSabor>> itensPorImpressora = agruparItensPorImpressora(pedido);

		Map<String, String> conteudos = new LinkedHashMap<>();

		// 2. Para cada impressora, gera o conteúdo do cupom e envia
		for (Map.Entry<String, List<SubItemSabor>> entry : itensPorImpressora.entrySet()) {
			String nomeCanal = entry.getKey();
			List<SubItemSabor> itens = entry.getValue();
			
			// CONCEITO: Busca Dinâmica com Fallback
	        // Busca a impressora configurada para este canal. 
	        // Se não encontrar (null), usa 48 como valor padrão de segurança.
	        Impressora impressora = impressoraCanalService.buscarImpressoraPorCanal(nomeCanal);
	        int largura = (impressora != null) ? impressora.getLarguraColunas() : 48;

			// Gera o cupom específico para aquela impressora
			String conteudo = gerarCupomCozinha(pedido, nomeCanal, itens, largura);

			// Envia para a impressora
			enviarParaImpressora(conteudo, nomeCanal);

			// Armazena para retorno (debug)
			conteudos.put(nomeCanal, conteudo);
		}

		return conteudos;
	}

	// IMPRESSÃO DO BALCÃO (VIA ÚNICA)
	/**
	 * Gera e envia a impressão do pedido para o balcão (via única).
	 *
	 * @param pedido Pedido a ser impresso.
	 * @return Conteúdo do cupom do balcão (para debug).
	 */
	private String imprimirCupomBalcao(Pedido pedido) {
		String conteudo = gerarCupomBalcao(pedido);
		enviarParaImpressora(conteudo, "BALCAO");
		return conteudo;
	}

	// =============================================================
	// AGRUPAMENTO INTELIGENTE (FILTRO DE IMPRESSORAS)
	// =============================================================

	/**
	 * Agrupa os sabores (SubItemSabor) do pedido por canal de impressão.
	 * O canal é definido no campo 'impressora' da Categoria do produto.
	 * 
	 * CONCEITO: Granularidade
	 * - Retornamos List<SubItemSabor> em vez de List<ItemPedido> porque o 
	 *   gerarCupomCozinha já sabe como reagrupar esses sabores por ItemPedido 
	 *   internamente (usando Collectors.groupingBy).
	 * - Isso garante que customizações e bordas sejam processadas corretamente.
	 *
	 * @param pedido O pedido completo
	 * @return Mapa onde a chave é o nome do canal (ex: "PIZZAS") e o valor é a lista de sabores.
	 */
	private Map<String, List<SubItemSabor>> agruparItensPorImpressora(Pedido pedido) {
		// Usamos LinkedHashMap para manter a ordem de inserção
		Map<String, List<SubItemSabor>> resultado = new LinkedHashMap<>();

		for (ItemPedido item : pedido.getItens()) {
			
			// Se o item não tiver sabores, não há o que imprimir na cozinha
			if (item.getSubItens().isEmpty()) {
				continue;
			}
			
			// CONCEITO: Ponto de Ancoragem 
			// Pegamos o nome do canal da categoria do PRIMEIRO sabor
			// Assumimos que todos os sabores de uma pizza pertencem à mesma categoria/canal
			String nomeCanal = item.getSubItens().get(0).getProduto().getCategoria().getImpressora();

			// REGRA DE NEGÓCIO: Filtro de Cozinha
	        // Se o canal for nulo, vazio ou "GERAL", este item NÃO vai para a cozinha.
	        // Ele será impresso apenas no cupom do balcão.
			if (nomeCanal == null || nomeCanal.isBlank() || "GERAL".equalsIgnoreCase(nomeCanal)) {
				continue; // Pula este item - será impresso apenas balcão
			}

			// CONCEITO: Desconstrução e Agrupamento
	        // Como o mapa agora guarda List<SubItemSabor>, precisamos iterar sobre 
	        // os sabores do item e adicioná-los individualmente à lista do canal.
			for (SubItemSabor sub : item.getSubItens()) {
				// computeIfAbsent: Se o canal não existe no mapa, cria uma nova ArrayList.
	            // Depois, adiciona o sabor (sub) a essa lista.
			resultado.computeIfAbsent(nomeCanal, k -> new ArrayList<>()).add(sub);
			
			}
		}
		return resultado;
	}

	// =============================================================
	// GERADOR DE CUPOM: COZINHA
	// =============================================================
	/**
	 * Gera o conteúdo do cupom para a COZINHA (específico para uma impressora).
	 */
	private String gerarCupomCozinha(Pedido pedido, String impressora, List<SubItemSabor> itens, int largura) {

		// CONCEITO: StringBuilder
		// Strings em Java são imutáveis. Usar "+" em loops cria lixo na memória.
		// StringBuilder é mutável e feito para performance em concatenações.
		StringBuilder sb = new StringBuilder();

		String obsPedido = pedido.getObsPedido();

		// --- CABEÇALHO ---
		// 1. Usando o método auxiliar
		sb.append(formatarCabecalho(pedido, largura));
		
	    // CONCEITO: Reagrupamento (Child-to-Parent Grouping)
	    // Como recebemos uma lista de SubItemSabor, precisamos agrupá-los 
	    // pelo ItemPedido pai. Isso nos permite imprimir o Tamanho e a Borda 
	    // apenas UMA VEZ por pizza, e listar os sabores logo abaixo.
		Map<ItemPedido, List<SubItemSabor>> itensPorItem = itens.stream()
				.collect(Collectors.groupingBy(SubItemSabor::getItem));
		
		
		// --- ITENS ---
		// Agora iteramos sobre o Mapa (ItemPedido -> Lista de Sabores)
		for (Map.Entry<ItemPedido, List<SubItemSabor>> entry : itensPorItem.entrySet()) {
			
			ItemPedido item = entry.getKey();   // A pizza inteira (pai)
			List<SubItemSabor> sabores = entry.getValue(); // Os sabores da Pizza
			
			int qtd = item.getQuantidade();
			
			// Pegamos a categoria do primeiro sabor (todos pertencem a mesma categoria)
			String nomeCategoria = item.getSubItens().get(0).getProduto().getCategoria().getNome();
			String tamanho = item.getTamanho().getNome();

			// Imprime o cabeçalho do Item
			sb.append(qtd).append(" ").append(nomeCategoria).append("  (" + tamanho.toUpperCase() + ")").append("\n");

			boolean multiplosSabores = item.getSubItens().size() > 1;

			// Loop pelos sabores desta pizza específica
			for (SubItemSabor sub : sabores) {

				// CONCEITO: Contagem Dinâmica
				// Não criamos variáveis "contador". Usamos o tamanho da própria lista.
				String prefixo = "    ";
				if (multiplosSabores) {
					prefixo =  "  1/" + sabores.size() + " ";
				} 
				String prod =  prefixo + sub.getProduto().getNome();
				sb.append(FormatadorCupomUtil.formatarLinhaSemValor(prod, largura));

				// Customizações do sabor (ADICIONAL ou REMOCAO)
				for (var c : sub.getCustomizacoes()) {
					String acao = (c.getTipoCustomizacao() == TipoCustomizacao.ADICIONAL) ? "+ " : "SEM ";
					String linhaFormatada = "     " + acao + c.getIngrediente().getNome().toUpperCase();
					sb.append(FormatadorCupomUtil.formatarLinhaSemValor(linhaFormatada, largura));

				}
			}

			// Bordas (customizações globais do item) - só uma vez por item
			for (var b : item.getCustomizacoesGlobais()) {
				String nome =  "  + Borda: " + b.getBorda().getNome(); 
				sb.append(FormatadorCupomUtil.formatarLinhaSemValor(nome, largura));
			}

			
			// ---OBSERVAÇÃO DO PEDIDO (se houver) ---
			if(obsPedido != null && !obsPedido.isBlank()) {
				sb.append(gerarLinhaSimples(largura));
				// Usa o método auxiliar para quebrar a linha
				sb.append(FormatadorCupomUtil.centralizar("OBSERVAÇÂO:", largura)).append("\n");
				String obsFormatada = FormatadorCupomUtil.quebrarLinha(obsPedido, largura);
				sb.append(obsFormatada).append("\n");
			}

		}
		// --- RODAPÉ ---
		// sb.append("TOTAL: R$ ").append(pedido.calcularTotal()).append("\n");
		sb.append(gerarLinhaDupla(largura));
		sb.append("    *** ENVIAR PARA ").append(impressora.toUpperCase()).append(" ***    \n");
		sb.append(gerarLinhaDupla(largura));

		return sb.toString();
	}

	// =============================================================
	// GERADOR DE CUPOM: BALCÃO (RECIBO DO CLIENTE)
	// =============================================================
	/**
	 * CONCEITO: Separação Visual de Preços - Mostra o preço BASE da pizza (sem
	 * adicionais) - Lista os adicionais (customizações e bordas) separadamente -
	 * Calcula o total do item (base + adicionais) * quantidade - Isso dá
	 * transparência ao cliente sobre o que está pagando
	 */
	private String gerarCupomBalcao(Pedido pedido) {
		
		// CONCEITO: O canal do balcão é fixo como "BALCAO"
		Impressora impressoraDoCanal = impressoraCanalService.buscarImpressoraPorCanal("BALCAO");
		StringBuilder sb = new StringBuilder();
		String obsPedido = pedido.getObsPedido();
		int largura = (impressoraDoCanal != null) ? impressoraDoCanal.getLarguraColunas(): 48; // 48 é o fallback de segurança

		// --- CABEÇALHO ---
		// 1. Usando o método auxiliar
		sb.append(formatarCabecalho(pedido, largura));

		// --- PRODUTOS ---
		sb.append(formatarCabecalhoProdutos(largura));
		sb.append("-".repeat(largura)).append("\n");

		for (ItemPedido item : pedido.getItens()) {

			int qtd = item.getQuantidade();
			
			String tamanho;
			
			// CONCEITO:  Se o pedido for de Tamanho "UNICO" no recibo será impresso o nome do produto sem o tamanho 
			// Se o produto tiver tamanho no recibo será impresso a Categoria com o tamanho e o nome do produto abaixo
			String nomeTamanho = item.getTamanho().getNome().trim().toUpperCase();
			boolean tamanhoUnico = nomeTamanho.equals("UNICO") || nomeTamanho.equals("ÚNICO");
			boolean imprimeLinhadetalhe = !tamanhoUnico;
			
			if(tamanhoUnico) {
				nomeTamanho = item.getSubItens().get(0).getProduto().getNome();
				tamanho = " ";
			}else {
				nomeTamanho = item.getSubItens().get(0).getProduto().getCategoria().getNome();
				String tamanhoFormato = item.getTamanho().getNome().toUpperCase();
				tamanho = " (" + tamanhoFormato + ")";
			}
			
			// CONCEITO: Preço Base vs Preço Final
			// - precoBase: apenas o maior sabor (sem adicionais)
			BigDecimal precoBase = calcularPrecoBase(item);
			BigDecimal totalItem = precoBase.multiply(BigDecimal.valueOf(qtd));

			// CONCEITO: String.format para alinhamento de colunas
			// %-20s = String alinhada à esquerda ocupando 20 caracteres
			// %6.2f = Número com 6 espaços totais e 2 casas decimais
			//String categoriaFormato = String.format("%-20s", nomeCategoria + " (" + tamanho + ")");
			
			sb.append(formatarLinhaProduto(nomeTamanho,tamanho ,qtd, precoBase, totalItem, largura));

			List<SubItemSabor> sabores = item.getSubItens();
			boolean multiplosSabores = sabores.size() > 1;

			for (SubItemSabor sub : sabores) {

				sb.append("  ");
				if (multiplosSabores) {
					sb.append("1/").append(sabores.size()).append(" ");
				} 
				String nomeProduto ;
				
				// Se imprimeLinhadetalhe for true então o produto tem tamanho, será impresso o detalhe 
				// Se for false o produto é de tamanho UNICO não imprime a linha de detalhe
				if(imprimeLinhadetalhe) {
					nomeProduto = sub.getProduto().getNome();
					sb.append("  " + nomeProduto).append("\n");
				}
				
				

				// Customizações do sabor (adicionados/removidos)
				for (var c : sub.getCustomizacoes()) {
					String acao = (c.getTipoCustomizacao() == TipoCustomizacao.ADICIONAL) ? "+ " : "SEM ";
					String nome = c.getIngrediente().getNome().toUpperCase();
					String linhaFormatada = "    " + acao + nome;
					
					// Usa o método auxiliar para alinha automaticamente
					sb.append(FormatadorCupomUtil.formatarLinhaComValor(linhaFormatada, c.getValorCobrado(), largura ));
				}

			}

			// Bordas como itens extras
			for (var b : item.getCustomizacoesGlobais()) {
				String prefixo = "  + Borda: ";
				String nome = prefixo + b.getBorda().getNome();
				
				// Usa o mesmo método auxiliar
				sb.append(FormatadorCupomUtil.formatarLinhaComValor(nome, b.getValorCobrado(), largura));
			}
			sb.append("\n");
		}
		sb.append(gerarLinhaSimples(largura));
		// ---OBSERVAÇÃO DO PEDIDO (se houver) ---
		if(obsPedido != null && !obsPedido.isBlank()) {
		
			// Usa o método auxiliar para quebrar a linha
			sb.append(FormatadorCupomUtil.centralizar("OBSERVAÇÃO:", largura)).append("\n");
			String obsFormatada = FormatadorCupomUtil.quebrarLinha(obsPedido, largura);
			sb.append(obsFormatada).append("\n");
			
			sb.append(gerarLinhaSimples(largura));
		}


		// Rodapé (usando método auxiliar)
		sb.append(formatarRodape(pedido, largura));

		return sb.toString();
	}

	// =============================================================
	// ENVIO PARA IMPRESSORA (SIMULAÇÃO)
	// =============================================================

	/**
	 * Simula o envio do conteúdo para uma impressora. Em produção, substituir pela
	 * integração real.
	 */
	private void enviarParaImpressora(String conteudo, String nomeCanal) {	
		try {
			//Busca a impressora responsavel pelo canal
			Impressora impressora = impressoraCanalService.buscarImpressoraPorCanal(nomeCanal);
			
			String nomeArquivo;
			
			if(impressora != null) {
				// Usa o nome da impressora no arquivo
				nomeArquivo = "impressao_" + impressora.getNome().replaceAll("\\s+", "_")
						+ "_" + System.currentTimeMillis() + ".txt";
				
				System.out.println(">>> Canal '" + nomeCanal + "' roteado para impressora: "
						+ impressora.getNome() + " (" + impressora.getLarguraColunas() + " colunas)");
			}else {
				// Fallback: usa o nome do canal
				nomeArquivo = "impressao_" + nomeCanal + "_" + System.currentTimeMillis() + ".txt";
	            System.out.println(">>> Canal '" + nomeCanal + "' sem impressora configurada. Usando fallback.");
			}
			
			java.nio.file.Path path = java.nio.file.Paths.get("impressoes", nomeArquivo);
	        java.nio.file.Files.createDirectories(path.getParent());
	        java.nio.file.Files.writeString(path, conteudo);
	        System.out.println(">>> Impressão salva em: " + path.toAbsolutePath());
			
		} catch (Exception e) {
			System.err.println("Erro ao simular impressão (" + nomeCanal + "): " + e.getMessage());
			// Fallback: imprime no console
			System.out.println("=== IMPRESSÃO " + nomeCanal + " ===\n" + conteudo);
		}
	}

	// =============================================================
	// UTILITÁRIOS
	// =============================================================
	private String obterNomeCliente(Pedido pedido) {
		if (pedido.getCliente() != null) {
			return pedido.getCliente().getNome();
		}
		return pedido.getNomeBalcao() != null ? pedido.getNomeBalcao() : "NÃO INFORMADO";
	}

	// =============================================================
	// MÉTODOS AUXILIARES PARA CÁLCULO DE PREÇOS
	// =============================================================

	/**
	 * Calcula o preço BASE do item (apenas o maior sabor, sem adicionais).
	 * 
	 * CONCEITO: Transparência de Preços - O cliente vê quanto custa a pizza "pura"
	 * - Depois vê quanto custam os adicionais separadamente - Isso evita a sensação
	 * de "preço inflado"
	 */
	private BigDecimal calcularPrecoBase(ItemPedido item) {
		BigDecimal precoMaiorSabor = BigDecimal.ZERO;

		for (SubItemSabor sabor : item.getSubItens()) {
			BigDecimal precoSabor = sabor.getPrecoSabor();
			if (precoSabor.compareTo(precoMaiorSabor) > 0) {
				precoMaiorSabor = precoSabor;
			}
		}

		return precoMaiorSabor;
	}

	// =============================================================
	// MÉTODO AUXILIAR: FORMATA LINHAS DE CUSTOMIZAÇÃO
	// =============================================================
	
	/**
	 * Formata a linha principal de um produto (ex: PIZZA, BEBIDA).
	 * 
	 * CONCEITO: Alinhamento de colunas com String.format()
	 * - %-20s = String alinhada à esquerda, ocupando 20 caracteres
	 * - %3d   = Número inteiro ocupando 3 caracteres (alinhado à direita)
	 * - R$%6.2f = Valor monetário ocupando 6 caracteres com 2 casas decimais
	 * 
	 * @param nomeProduto  Nome do produto com tamanho (ex: "PIZZA (MÉDIA)")
	 * @param quantidade   Quantidade do item
	 * @param precoUnitario Preço unitário
	 * @param precoTotal   Preço total (unitário × quantidade)
	 * @return Linha formatada com quebra de linha
	 */
	private String formatarLinhaProduto(String nomeProduto,String tamanho ,int quantidade, BigDecimal precoUnitario, BigDecimal precoTotal, int largura) {
		StringBuilder sb = new StringBuilder();
		
		// Larguras fixas das colunas numericas
		int larguraQtd = 5; // "  QTD"
		int larguraValor = 12; //"     R$ 0,00"
		int larguraTotal = 12; //"     R$ 0,00"
		int espacosSeparadores = 4;// Espaços entre colunas
				
		// Calcula a largura da coluna ITEM (o que sobrar) 
		int larguraItem = largura - larguraQtd - larguraValor - larguraTotal - espacosSeparadores;
	    
	    // %-20s = String alinhada à esquerda, ocupando exatamente 20 caracteres
	    // Se o nome for menor que 20, preenche com espaços à direita
	    // Se for maior, não trunca (mas pode quebrar a formatação)
		
		
	    String nomeFormato = nomeProduto + tamanho ;
	    
	    sb.append(String.format("%-" + larguraItem + "s",nomeFormato));
	    sb.append(" ");	 // Espaço 1
	    
	    // Coluna QTD (Alinhada a direita )
	    String qntd = String.format("%3d", quantidade);
	 	sb.append(String.format("%" + larguraQtd + "s", qntd));
	 	sb.append(" ");	 // Espaço 2
	    
	    
	 	// COLUNA VALOR (alinhada à direita)
	    // R$%6.2f = Valor monetário com 6 caracteres totais e 2 casas decimais
	    // Ex: "R$ 40,00" (com 1 espaço após R$) ou "R$  8,00" (com 2 espaços)
	 	String vlUnitario = String.format("R$%6.2f", precoUnitario);
	    sb.append(String.format("%" + larguraValor + "s",vlUnitario));
	    
	    // Espaço fixo entre valor unitário e total
	    sb.append(" ");
	    
	    // Mesmo formato para o total
	    String vlTotal = String.format("R$%6.2f", precoTotal);
	    sb.append(String.format("%" + larguraTotal + "s", vlTotal));
	    sb.append("\n");
	    
	    return sb.toString();
	}
	
	/**
	 * Formata o cabeçalho do cupom (informações do pedido e cliente).
	 * 
	 * CONCEITO: Centralização de texto
	 * - Usamos cálculo manual para centralizar o título
	 * - LINHA_DUPLA é uma constante que define a largura do separador
	 * 
	 * 
	 * @param pedido O pedido completo
	 * @return String com o cabeçalho formatado
	 */
	private String formatarCabecalho(Pedido pedido, int largura) {
		StringBuilder sb = new StringBuilder();
		
		
		
		
		// Titulo centralizado (Opcional)
		String titulo = "PEDIDO " + pedido.getTipo().toString().toUpperCase();
		sb.append(FormatadorCupomUtil.centralizar(titulo, largura)).append("\n");
		
		// Linha dupla separada (constante LINHA_DUPLA)
		sb.append(gerarLinhaDupla(largura));
		
		// Numero do Pedido
		sb.append("PEDIDO #").append(pedido.getId()).append("\n");
		
		// Data/Hora de abertura
		sb.append("DATA: " ).append(
				pedido.getDtHoraAbertura() != null 
				? pedido.getDtHoraAbertura().format(FORMATTER_DATA)
				: "DATA NÃO INFORMADA")
		.append("\n");
		
		//Linha simples separadora
		sb.append(gerarLinhaSimples(largura));
		
		// Nome do Cliente 
		sb.append("CLIENTE: ").append(obterNomeCliente(pedido)).append("\n");
		
		// Se tiver cliente cadastrado, mostra telefone e endereço
		if(pedido.getCliente() != null) {
			var cliente = pedido.getCliente();
			
			if (cliente.getTelefonePrincipal() != null) {
				sb.append("TELEFONE: ").append(cliente.getTelefonePrincipal()).append("\n");
			}
			
			if(cliente.getEndereco() != null) {
				var end = cliente.getEndereco();
				sb.append("ENDEREÇO: ").append(end.getLogradouro())
				.append(", ").append(end.getNumero());
				
				if(end.getComplemento() != null && !end.getComplemento().isBlank()) {
					sb.append(" - ").append(end.getComplemento());
				}
				sb.append("\n");
				sb.append("BAIRRO: ").append(end.getBairro()).append("\n");
				sb.append("CIDADE: ").append(end.getCidade())
				.append("/").append(end.getUf());
			}
		}
		
		// Linha simples separada
		sb.append(gerarLinhaSimples(largura));
		
		return sb.toString();
	}
	
	/**
	 * Formata o rodapé do cupom (totais, pagamentos e mensagem final).
	 * 
	 * CONCEITO: Recálculo obrigatório
	 * - Sempre chamamos pedido.calcularTotal() ANTES de imprimir valores
	 * - Isso garante que valorBruto, descontos e taxas estejam atualizados
	 * 
	 * @param pedido O pedido completo
	 * @return String com o rodapé formatado
	 */
	private String formatarRodape(Pedido pedido, int largura) {
		StringBuilder sb = new StringBuilder();
		
		// CONCEITO: Recálculo obrigatório
		// Sempre recalcula o total ANTES de imprimir qualquer valor financeiro
		//BigDecimal totalAtualizado = pedido.calcularTotal();
		
		// Linha simples separadora
		sb.append("SUBTOTAL: ").append(String.format("R$%6.2f", pedido.getValorBruto())).append("\n");
		
		// Desconto (se houver)
		if(pedido.getDesconto() != null && pedido.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
			sb.append("DESCONTO: ").append(String.format("-R$%6.2f", pedido.getDesconto())).append("\n");
		}
		
		// Linha dupla antes do total
		sb.append(gerarLinhaDupla(largura));
		
		// Forma de Pagamento
		sb.append("FORMA DE PAGAMENTO:\n");
	    if (pedido.getPagamentos().isEmpty()) {
	        sb.append("  (Aguardando pagamento)\n");
	    } else {
	        for (var pag : pedido.getPagamentos()) {
	            sb.append("  ").append(pag.getFormaPagamento())
	              .append(": ").append(String.format("R$%6.2f", pag.getValor())).append("\n");
	            
	            // Se for DINHEIRO, calcula e mostra o troco
	            if (pag.getFormaPagamento() == FormaPagamento.DINHEIRO) {
	                BigDecimal troco = pedido.calcularTroco();
	                if (troco.compareTo(BigDecimal.ZERO) > 0) {
	                    sb.append("    TROCO: ").append(String.format("R$%6.2f", troco)).append("\n");
	                }
	            }
	        }
	    }
	    
	    // Mensagem final
	    sb.append(gerarLinhaDupla(largura));
	    sb.append("======= ESTE CUPOM NÃO TEM VALOR FISCAL ========\n");
	    sb.append("*** OBRIGADO PELA PREFERÊNCIA ***\n");
	    sb.append(gerarLinhaDupla(largura));
	    
	    return sb.toString();
	}
	
	
	// MÉTODO AUXILIAR: CABEÇALHO DE PRODUTOS (DINÂMICO)
	/**
	 * Gera a linha de cabeçalho da tabela de produtos com alinhamento dinâmico.
	 * 
	 * CONCEITO: Formatação Proporcional
	 * - A coluna ITEM é flexível (ocupa o espaço restante)
	 * - As colunas QTD, VALOR e TOTAL têm larguras fixas
	 * - O alinhamento é calculado com base na largura total da impressora
	 * 
	 * @param largura Largura total da impressora (ex: 48 ou 56)
	 * @return Linha formatada com quebra de linha
	 */
	private String formatarCabecalhoProdutos(int largura) {
		
		// Larguras fixas das colunas numericas
		int larguraQtd = 5; // "  QTD"
		int larguraValor = 12; //"     R$ 0,00"
		int larguraTotal = 12; //"     R$ 0,00"
		int espacosSeparadores = 4;// Espaços entre colunas
		
		// Calcula a largura da coluna ITEM (o que sobrar) 
		int larguraItem = largura - larguraQtd - larguraValor - larguraTotal - espacosSeparadores;
		
		// Monta a linha com String.format()
		StringBuilder sb = new StringBuilder();
		
		// Coluna ITEM (alinhada à esquerda)
		sb.append(String.format("%-" + larguraItem + "s", "ITEM"));
		sb.append(" "); // Espaço 1
		
		// Coluna QTD (Alinhada a direita)
		sb.append(String.format("%" + larguraQtd + "s", "QTD"));
		sb.append(" "); // Espaço 2
		
		// COLUNA VALOR (alinhada à direita)
	    sb.append(String.format("%" + larguraValor + "s", "VALOR"));
	    sb.append(" "); // Espaço 3
		
		// Coluna TOTAL (Alinhada a direita) 
		sb.append(String.format("%" + larguraTotal + "s", "TOTAL"));
		
		sb.append("\n");
		
		return sb.toString();
		
	}
	
	
	// MÉTODOS AUXILIARES: LINHAS DINÂMICAS
	/**
	 * Gera uma linha simples separadora com a largura especificada.
	 * 
	 * CONCEITO: Formatação Dinâmica
	 * - Em vez de usar uma constante fixa, gera a linha baseada na largura
	 * - Garante que o cupom seja consistente com a impressora configurada
	 * 
	 * @param largura Número de colunas da impressora
	 * @return Linha simples com quebra de linha
	 */
	private String gerarLinhaSimples(int largura) {
	    return "-".repeat(largura) + "\n";
	}
	
	/**
	 * Gera uma linha dupla separadora com a largura especificada.
	 * 
	 * @param largura Número de colunas da impressora
	 * @return Linha dupla com quebra de linha
	 */
	private String gerarLinhaDupla(int largura) {
	    return "=".repeat(largura) + "\n";
	}
	
	
}