package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoCustomizacao;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Borda;
import com.dsys.appfood.domain.model.Cliente;
import com.dsys.appfood.domain.model.ComposicaoPadrao;
import com.dsys.appfood.domain.model.Entregador;
import com.dsys.appfood.domain.model.Ingrediente;
import com.dsys.appfood.domain.model.ItemCustomizacao;
import com.dsys.appfood.domain.model.ItemPedido;
import com.dsys.appfood.domain.model.Mesa;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.SubItemSabor;
import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.dto.request.AdicionarItemRequest;
import com.dsys.appfood.dto.request.AdicionarSaborRequest;
import com.dsys.appfood.dto.request.BordaItemRequest;
import com.dsys.appfood.dto.request.CancelarPedidoRequest;
import com.dsys.appfood.dto.request.CustomizacaoRequest;
import com.dsys.appfood.dto.request.PedidoRequest;
import com.dsys.appfood.dto.request.ReabrirPedidoRequest;
import com.dsys.appfood.dto.request.StatusPedidoRequest;
import com.dsys.appfood.dto.request.VincularEntregadorRequest;
import com.dsys.appfood.dto.response.PedidoResponse;
import com.dsys.appfood.dto.response.PedidoResumoResponse;
import com.dsys.appfood.event.PedidoStatusChangeEvent;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.PedidoNaoEncontradoException;
import com.dsys.appfood.repository.ItemPedidoRepository;
import com.dsys.appfood.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsável pelo coração do sistema: O Pedido.
 * 
 * Responsabilidade ÚNICA: gerenciar o ciclo de vida do Pedido (criação, edição
 * de itens, customizações e fluxo de status)
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class PedidoService {

	private final ComposicaoPadraoService composicaoService;
	private final EntregadorService entregadorService;
	private final MesaService mesaService;
	private final BordaService bordaService;
	private final IngredienteService ingredienteService;
	private final PedidoRepository pedidoRepository;
	private final TamanhoService tamanhoService;
	private final ProdutoService produtoService;
	private final UsuarioService usuarioService;
	private final ClienteService clienteService;
	private final ApplicationEventPublisher eventPublisher;

	public PedidoService(ItemPedidoRepository itemPedidoRepository, ClienteService clienteService,
			UsuarioService usuarioService, ProdutoService produtoService, TamanhoService tamanhoService,
			PedidoRepository pedidoRepository, IngredienteService ingredienteService, BordaService bordaService,
			MesaService mesaService, EntregadorService entregadorService, ComposicaoPadraoService composicaoService, 
			ApplicationEventPublisher eventPublisher) {

		this.clienteService = clienteService;
		this.usuarioService = usuarioService;
		this.produtoService = produtoService;
		this.tamanhoService = tamanhoService;
		this.pedidoRepository = pedidoRepository;
		this.ingredienteService = ingredienteService;
		this.bordaService = bordaService;
		this.mesaService = mesaService;
		this.entregadorService = entregadorService;
		this.composicaoService = composicaoService;
		this.eventPublisher = eventPublisher;
		

	}

	// =============================================================
	// 1. NASCIMENTO DO PEDIDO (ABERTURA)
	// =============================================================
	@Transactional
	public Pedido iniciarPedido(Integer clienteId, Integer operadorId, TipoPedido tipo, String nomeBalcao,
			Integer numeroMesa, String obsPedido) {

		// 1. Busca o Operador
		Usuario operador = usuarioService.buscaPorId(operadorId);

		// 2. Validação para mesa
		if (tipo == TipoPedido.MESA && numeroMesa == null) {
			throw new NegocioException("Para pedidos de mesa, é obrigatório informar o número da mesa.");
		}

		// 2.1 Se for mesa verificar se a mesa existe e está livre
		if (tipo == TipoPedido.MESA) {
			Mesa mesa = mesaService.buscarPorNumero(numeroMesa);
			if (mesa.isOcupada()) {
				throw new NegocioException("A mesa " + numeroMesa + " já está ocupada.");
			}

			mesaService.ocuparMesa(numeroMesa);
		}

		// 3. Validação de Entrega
		if (tipo == TipoPedido.ENTREGA && clienteId == null) {
			throw new NegocioException("Para pedidos de entrega, é obrigatório informar o cliente.");
		}

		// 4. Validação de Balcão (Nome é obrigatório se não houver ID)
		if (tipo == TipoPedido.BALCAO && clienteId == null && (nomeBalcao == null || nomeBalcao.isEmpty())) {

			throw new NegocioException("Para pedidos de balcão sem cadastro, informe o nome do cliente");
		}

		// 5. Busca o Cliente apenas se o ID foi enviado (Evita erro de busca nula)
		Cliente cliente = null;
		if (clienteId != null) {
			cliente = clienteService.buscarClientePorId(clienteId);
		}

		// 6. Instancia e configura
		Pedido pedido = new Pedido();
		pedido.setTipo(tipo);
		pedido.setOperador(operador);
		pedido.setCliente(cliente);
		pedido.setNomeBalcao(nomeBalcao);
		pedido.setNumeroMesa(numeroMesa);
		pedido.setObsPedido(obsPedido);

		// 7. Registra o status inicial (Usando seu método da Model que já grava
		// histórico)
		pedido.alteraStatus(StatusPedido.PEDIDO_INICIADO, operador);

		// 8. Salvar no repository e retornar.
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 2. ADICIONANDO CORPO (ITENS E SABORES)
	// =============================================================

	@Transactional
	public Pedido adicionarItemAoPedido(Integer pedidoId, Integer tamanhoId, Integer produtoId) {

		// Buscar o Pedido pelo ID.
		Pedido pedido = buscarPorId(pedidoId);

		// Validar o Status.
		if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {
			throw new NegocioException("O status atual (" + pedido.getStatus() + ") não permite adicionar itens.");
		}

		// Buscar o Tamanho
		Tamanho tamanho = tamanhoService.buscarPorId(tamanhoId);

		// Instanciar um novo ItemPedido
		ItemPedido item = new ItemPedido(tamanho);
		
		// Se for produto Simples (Bebida, Porção, etc.)
		if (produtoId != null) {
			
			//Busca o produto (bebida, porção)
			Produto produto = produtoService.buscarProdutoPorId(produtoId);
			
			// VALIDAÇÃO: Verifica se a categoria NÃO é personalizavel (ex: Bebidas)
			// Se for personalizavel (Pizza), não deve entrar aqui, pois precisa de Multiplos sabores.
			if(produto.getCategoria().isPersonalizavel()) {
				throw new NegocioException("Produtos personalizáveis devem ser adicionados usando o endpoint específico de sabores.");
			}
			
			// Busca o preço do produto para o tamanho informado (Ex: Coca-Cola 600ml)
			BigDecimal preco = produto.obterPrecoParaTamanho(tamanho);
			
			// Cria o SubItemSabor (que aqui representa o próprio produto)
			SubItemSabor subItem = new SubItemSabor(produto, preco);
			
			// Adiciona o sabor(Produto) ao item

			item.adicionarSabor(subItem);
		}
		// Se for PIZZA (produtoId == null), apenas criamos o container vazio.
	    // O frontend chamará /pedidos/{id}/sabores depois.

		// Adicionar o item ao pedido
		pedido.adicionarItem(item);

		// Salvar o Pedido. O CascadeType.ALL da Model vai salvar o ItemPedido automaticamente!
		return pedidoRepository.save(pedido);
	}

	@Transactional
	public Pedido adicionarSaborAoItem(Integer pedidoId, Integer itemId, Integer produtoId) {

		Pedido pedido = buscarPorId(pedidoId);

		// Status permitidos: apenas pedidos em andamento podem receber alterações
		if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {
			throw new NegocioException("O status atual (" + pedido.getStatus() + ") não permite adicionar sabor ao item.");
		}

		// Localizar o item dentro do pedido
		ItemPedido itemEncontrado = pedido.getItens().stream()
				.filter(item -> item.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException(
						"Item ID " + itemId + " não pertence ao pedido ID " + pedido.getId()));

		// Buscar o produto que será adicionado como sabor
		Produto produto = produtoService.buscarProdutoPorId(produtoId);
		
		// Verifica se o item possui sabores 
		if(!itemEncontrado.getSubItens().isEmpty()) {
			// Pega o primeiro sabor para verificar a categoria
			SubItemSabor primeiroSabor = itemEncontrado.getSubItens().get(0);
			
			// Se o primeiro sabor NÃO for personalizável, o item é fixo (bebidas, porções)
			// → não pode receber sabores adicionais
			if(!primeiroSabor.getProduto().getCategoria().isPersonalizavel()) {
				throw new NegocioException("Este item contém um produto fixo (" + primeiroSabor.getProduto().getNome() + 
		                ") e não pode receber sabores adicionais. Para adicionar múltiplos itens, crie um novo item.");
			}
			
			// Se o primeiro sabor é personalizável, verifica se o novo produto também é personalizável
			// → não permite misturar produtos fixos com pizzas no mesmo item
			if(!produto.getCategoria().isPersonalizavel()) {
				throw new NegocioException("Produtos fixos (como " + produto.getNome() + ") não "
						+ "podem ser adicionados como sabores adicionais em uma pizza.");
			}
			
			// Se passou pelas verificações, o item já é uma pizza e o novo produto também é personalizável → OK
		}else {
			// Se o item está VAZIO (recém-criado), o primeiro sabor DEVE ser personalizável
	        // Isso impede que uma bebida seja adicionada a um item vazio usando o endpoint /sabores
	        // (bebidas devem ser adicionadas diretamente via /itens com produtoId)
			if(!produto.getCategoria().isPersonalizavel()) {
				throw new NegocioException("Itens vazios só podem receber produtos personalizáveis (pizzas). " +
		                "Para adicionar produtos fixos, utilize o endpoint /itens informando o produtoId.");
			}
			
			// Se o produto é personalizável → OK (primeiro sabor de uma pizza)
		}

		// Obtém o preço do produto para o tamanho do item (P, M, G)
	    // Se o produto não tiver preço para esse tamanho, o método já lança exceção
		BigDecimal precoSabor = produto.obterPrecoParaTamanho(itemEncontrado.getTamanho());

		// Criar e adicionar o SubItem (Sabor)
		SubItemSabor subItem = new SubItemSabor(produto, precoSabor);
		itemEncontrado.adicionarSabor(subItem);

		// Recalcular Total e persistir
		pedido.calcularTotal();
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 3. CUSTOMIZAÇÕES
	// =============================================================

	// --- 1. ADICIONAR INGREDIENTE AO SABOR ---
	@Transactional
	public Pedido adicionarCustomizacao(Integer pedidoId, Integer itemId, Integer subItemId, Integer ingredienteId,
			TipoCustomizacao tipo) {

		// Verifica se o tipo de customização é Adição ou Remoção
		if (tipo != TipoCustomizacao.ADICIONAL && tipo != TipoCustomizacao.REMOCAO) {
			throw new NegocioException("Customização " + tipo + "não permitida em sabores.");
		}

		// Buscar Pedido
		Pedido pedido = buscarPorId(pedidoId);

		// Validar Status
		if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {

			throw new NegocioException("O status atual (" + pedido.getStatus() + ") não permite customizações.");
		}

		// Achar o Item do Pedido
		ItemPedido itemEncontrado = pedido.getItens().stream()
				.filter(item -> item.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException(
						"Item ID " + itemId + " não encontrado no pedido ID " + pedido.getId()));

		// Achar o sabor dentro do item
		SubItemSabor saborEncontrado = itemEncontrado.getSubItens().stream()
				.filter(sub -> sub.getId().equals(subItemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException("Sabor não encontrado nesse item"));

		// Buscar o Ingrediente
		Ingrediente ingrediente = ingredienteService.buscarIngredientePorId(ingredienteId);
		
		// O sabor deve pertencer a um produto PERONALIZÁVEL 
		// → Não faz sentido adicionar/remover ingredientes de uma Coca-Cola
		if(!saborEncontrado.getProduto().getCategoria().isPersonalizavel()) {
			throw new NegocioException( 
					"O produto '" + saborEncontrado.getProduto().getNome() + 
		            "' não é personalizável e não aceita customizações de ingredientes. " +
		            "Apenas produtos da personalizáveis permitem esta operação."
		            );
		}
		
		// Validar o Tipo de Customização (garantir que é ADICIONAL ou REMOCAO)
	    // → Se o DTO enviar outro valor, bloqueia (segurança extra)
		if(tipo != TipoCustomizacao.ADICIONAL && tipo != TipoCustomizacao.REMOCAO) {
			throw new NegocioException("Tipo de customização inválido. Use 'ADICIONAL' ou 'REMOCAO'.");
		}

		// Se for REMOCAO, verificar se o ingrediente realmente faz parte da composição padrão
	    //  → Isso evita que o cliente "remova" algo que já não está na pizza
		if(tipo == TipoCustomizacao.REMOCAO) {
			ComposicaoPadrao composicao = composicaoService.buscarReceitaDoProduto(saborEncontrado.getProduto().getId());
			if(!composicao.getIngredientes().contains(ingrediente)) {
				throw new NegocioException("O ingrediente '" + ingrediente.getNome() + 
		                "' não faz parte da composição padrão deste produto e não pode ser removido.");
			}
		}
		
		// Definir o valor.
		// Se for REMOCAO, o valor é ZERO (não cobra para remover)
	    // Se for ADICIONAL, cobra o valor adicional do ingrediente
		BigDecimal preco = (tipo == TipoCustomizacao.REMOCAO) ? BigDecimal.ZERO : ingrediente.getValorAdicional();

		// Instanciar a Customizacao
		ItemCustomizacao custimizacao = new ItemCustomizacao();
		custimizacao.associarSubItem(saborEncontrado);
		custimizacao.setIngrediente(ingrediente);
		custimizacao.setTipoCustomizacao(tipo);
		custimizacao.adicionarValorACustomizacao(preco);

		// Adicionar na Lista
		saborEncontrado.adicionarCustomizacao(custimizacao);

		// Recalcular e Salvar
		pedido.calcularTotal();
		return pedidoRepository.save(pedido);
	}

	// --- 2. ADICIONAR BORDA AO ITEM ---
	@Transactional
	public Pedido adicionarBordaAoItem(Integer pedidoId, Integer itemId, Integer bordaId) {

		// Buscar Pedido
		Pedido pedido = buscarPorId(pedidoId);
		
		// Validar Status (apenas pedidos em andamento)
	    if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {
	        throw new NegocioException("O status atual (" + pedido.getStatus() + ") não permite adicionar bordas.");
	    }

		// Achar o Item
		ItemPedido itemEncontrado = pedido.getItens().stream()
				.filter(item -> item.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException("Item ID " + itemId + " não encontrado no pedido ID " + pedido.getId()));

		// Buscar a borda
		Borda borda = bordaService.buscarBordaPorId(bordaId);
		
		//  O item deve ter pelo menos UM sabor
	    //   → Não faz sentido adicionar borda a um item vazio
		if(itemEncontrado.getSubItens().isEmpty()) {
			throw new NegocioException(
					"Não é possível adicionar borda a um item sem sabores. " +
		            "Adicione pelo menos um sabor antes de aplicar a borda."
					);
		}
		
		//   O produto do primeiro sabor deve ser PERSONALIZÁVEL (Pizza)
	    //     → Como validamos no adicionarSaborAoItem que um item não pode misturar
	    //       produtos fixos e personalizáveis, verificar o primeiro sabor é suficiente.
		SubItemSabor primeiroSabor = itemEncontrado.getSubItens().get(0);
		if(!primeiroSabor.getProduto().getCategoria().isPersonalizavel()) {
			throw new NegocioException(
					"O item contém o produto '" + primeiroSabor.getProduto().getNome() + 
		            "', que não é personalizável. Borda só pode ser adicionada a produtos do tipo Pizza."
					);
		}
		
		// Verificar se o item já possui esta borda
	    // → Evita duplicidade de borda no mesmo item
		boolean bordaJaExiste = itemEncontrado.getCustomizacoesGlobais().stream()
				.anyMatch(c -> c.getBorda() != null && c.getBorda().getId().equals(bordaId));
		if(bordaJaExiste) {
			throw new NegocioException("A borda '" + borda.getNome() + "' já foi adicionada a este item.");
		}

		// Instanciar a customização ligada direto ao item (não ao sabor)
		ItemCustomizacao customizacao = new ItemCustomizacao();
		customizacao.associarItemPedido(itemEncontrado);
		customizacao.setBorda(borda);
		customizacao.setTipoCustomizacao(TipoCustomizacao.BORDA);
		customizacao.adicionarValorACustomizacao(borda.getValorAdicional());

		// Adicionar ao item
		itemEncontrado.adicionarCustomizacaoGlobal(customizacao);

		// Calcular Total
		pedido.calcularTotal();

		// Salvar e retornar
		return pedidoRepository.save(pedido);
	}

	// --- 3. REMOVER INGREDIENTE DO SABOR ---
	@Transactional
	public Pedido removerCustomizacao(Integer pedidoId, Integer itemId, Integer subItemId, Integer ingredienteId) {

		// Buscar Pedido
		Pedido pedido = buscarPorId(pedidoId);

		// Validar Status
		if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {

			throw new NegocioException("O status atual (" + pedido.getStatus() + ") não permite customizações.");
		}

		// Achar o Item do Pedido
		ItemPedido itemEncontrado = pedido.getItens().stream().filter(item -> item.getId().equals(itemId)).findFirst()
				.orElseThrow(() -> new NegocioException(
						"Item ID " + itemId + " não encontrado no pedido ID " + pedido.getId()));

		// Achar o sabor dentro do item
		SubItemSabor saborEncontrado = itemEncontrado.getSubItens().stream()
				.filter(sub -> sub.getId().equals(subItemId)).findFirst()
				.orElseThrow(() -> new NegocioException("Sabor não encontrado nesse item"));

		// Encontrar a customização REAL que está na lista para poder remover
		ItemCustomizacao customizacaoExistente = saborEncontrado.getCustomizacoes().stream()
				.filter(c -> c.getIngrediente().getId().equals(ingredienteId)).findFirst()
				.orElseThrow(() -> new NegocioException("Esta customização não existe neste sabor."));

		// Remover da Lista
		saborEncontrado.removerCustomizacao(customizacaoExistente);

		// Recalcular e Salvar
		pedido.calcularTotal();
		return pedidoRepository.save(pedido);
	}

	// --- 4. REMOVER BORDA DO ITEM ---
	@Transactional
	public Pedido removerBordaDoItem(Integer pedidoId, Integer itemId, Integer bordaId) {

		// Buscar Pedido
		Pedido pedido = buscarPorId(pedidoId);

		// Achar o Item
		ItemPedido itemEncontrado = pedido.getItens().stream().filter(item -> item.getId().equals(itemId)).findFirst()
				.orElseThrow(() -> new NegocioException("Item não encontrado"));

		// Encontrar a customização REAL que está na lista para poder remover
		ItemCustomizacao bordaExistente = itemEncontrado.getCustomizacoesGlobais().stream()
				.filter(c -> c.getBorda().getId().equals(bordaId)).findFirst()
				.orElseThrow(() -> new NegocioException("Esta borda não existe neste Item."));

		// Adicionar ao item
		itemEncontrado.removerCustomizacaoGlobal(bordaExistente);

		// Calcular Total
		pedido.calcularTotal();

		// Salvar e retornar
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 4. FINALIZAR PEDIDO
	// =============================================================

	public Pedido finalizarPedido(Integer pedidoId, Integer operadorId) {

		// BUSCAR E VALIDAR ENTIDADES

		Pedido pedido = buscarPorId(pedidoId);
		Usuario operador = usuarioService.buscaPorId(operadorId);

	    // VALIDAÇÃO: Pedido já está finalizado ou cancelado?
	    if (pedido.getStatus() == StatusPedido.FINALIZADO) {
	        throw new NegocioException("O pedido #" + pedidoId + " já está finalizado.");
	    }
	    if (pedido.getStatus() == StatusPedido.CANCELADO) {
	        throw new NegocioException("O pedido #" + pedidoId + " está cancelado e não pode ser finalizado.");
	    }

	    // VALIDAÇÃO: O pedido deve estar pago (regra de ouro!)
	    if (!pedido.isPago()) {
	        BigDecimal valorRestante = pedido.getValorRestante();
	        throw new NegocioException(
	            String.format("O pedido #%d não pode ser finalizado. Valor restante a pagar: R$ %.2f",
	                pedidoId, valorRestante)
	        );
	    }

	    // VALIDAÇÃO: O status deve ser PRONTO ou SAIU_PARA_ENTREGA
	    StatusPedido statusAtual = pedido.getStatus();
	    if (statusAtual != StatusPedido.PRONTO && statusAtual != StatusPedido.SAIU_PARA_ENTREGA) {
	        throw new NegocioException(
	            String.format("Pedido #%d está com status '%s'. Para finalizar, o pedido deve estar PRONTO ou SAIU_PARA_ENTREGA.",
	                pedidoId, statusAtual)
	        );
	    }

	    // VALIDAÇÃO: Se for ENTREGA, deve ter entregador vinculado
	    if (pedido.getTipo() == TipoPedido.ENTREGA) {
	        if (pedido.getEntregador() == null) {
	            throw new NegocioException(
	                "Pedido de ENTREGA #" + pedidoId + " não possui entregador vinculado. " +
	                "Vincule um entregador antes de finalizar."
	            );
	        }
	    }

	    // VALIDAÇÃO: Se for MESA, deve ter número da mesa
	    if (pedido.getTipo() == TipoPedido.MESA && pedido.getNumeroMesa() == null) {
	        throw new NegocioException(
	            "Pedido de MESA #" + pedidoId + " não possui número da mesa informado."
	        );
	    }

		// FINALIZAR PEDIDOS, AS VALIDAÇÕES SÃO FEITAS NA MODEL
		pedido.finalizarPedido(operador);

		// LIBERAR RECURSOS (pós-finalização)

		// Se for MESA, libera a mesa para novos clientes
		if (pedido.getTipo() == TipoPedido.MESA && pedido.getNumeroMesa() != null) {
			try {
				mesaService.liberarMesa(pedido.getNumeroMesa());
			} catch (Exception e) {
				// Log do erro, mas não impede a finalização do pedido
				// (a mesa pode já ter sido liberada manualmente)
				System.err.println(
						"Aviso: Não foi possível liberar a mesa " + pedido.getNumeroMesa() + ": " + e.getMessage());
			}
		}

		// Se for ENTREGA, o entregador fica disponível novamente
		// (não precisa fazer nada, o entregador não tem estado "ocupado" no modelo
		// atual)

		// PERSISTIR E RETORNAR

		return pedidoRepository.save(pedido);

	}

	// =============================================================
	// 5. VINCULA UM ENTREGADOR AO PEDIDO
	// =============================================================
	@Transactional
	public Pedido vincularEntregador(Integer pedidoId, Integer entregadorId, Integer operadorId) {

		Pedido pedido = buscarPorId(pedidoId);
		Entregador entregador = entregadorService.buscarEntregadorPorId(entregadorId);
		
	    // VALIDAÇÃO: Apenas pedidos do tipo ENTREGA podem ter entregador
	    if (pedido.getTipo() != TipoPedido.ENTREGA) {
	        throw new NegocioException("Apenas pedidos do tipo ENTREGA podem ter entregador vinculado.");
	    }

	    // VALIDAÇÃO 3: Entregador deve estar ativo
	    if (!entregador.isAtivo()) {
	        throw new NegocioException("O entregador " + entregador.getNome() + " está inativo.");
	    }

		// Vincula o entregador as validações são feitas na model
		pedido.vincularEntregador(entregador);

		// Registra no histórico (opcional, mas útil para auditoria)
		// Poderia criar um StatusPedidoHistorico com observação

		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 6. CANCELAR PEDIDO
	// =============================================================
	@Transactional
	public Pedido cancelarPedido(Integer pedidoId, Integer gerenteId, String motivo, Integer operadorId) {

		// Busca entidades
		Pedido pedido = buscarPorId(pedidoId);
		Usuario operador = usuarioService.buscaPorId(operadorId);
		Usuario gerente = usuarioService.buscaPorId(gerenteId);

		// Cancela pedido, as validações são feitas na model
		pedido.cancelarPedido(operador, gerente, motivo);

		// Libera a mesa se for o caso
		if (pedido.getTipo() == TipoPedido.MESA && pedido.getNumeroMesa() != null) {
			mesaService.liberarMesa(pedido.getNumeroMesa());
		}

		// Salva e retorna
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 7. REABRIR PEDIDO PEDIDO CANCELADO
	// =============================================================
	@Transactional
	public Pedido reabrirPedidoCancelado(Integer pedidoId, Integer gerenteId, Integer operadorId) {
		
		// Busca entidades
		Pedido pedido = buscarPorId(pedidoId);
		Usuario operador = usuarioService.buscaPorId(operadorId);
		Usuario gerente = usuarioService.buscaPorId(gerenteId);
		
		// Reabrir pedido, as validações são feitas na model
		pedido.reabrirPedidoCancelado(gerente, operador);
		
		// Se for MESA, verifica se a mesa está livre e ocupa novamente
	    if (pedido.getTipo() == TipoPedido.MESA && pedido.getNumeroMesa() != null) {
	        Mesa mesa = mesaService.buscarPorNumero(pedido.getNumeroMesa());
	        if (mesa.isOcupada()) {
	            throw new NegocioException(
	                "Não é possível reabrir o pedido. A mesa " + 
	                pedido.getNumeroMesa() + " já está ocupada por outro pedido."
	            );
	        }
	        mesaService.ocuparMesa(pedido.getNumeroMesa());
	    }
		
	    // Salva e Retorna 
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 8. FLUXO DE STATUS
	// =============================================================
	@Transactional
	public Pedido mudarStatus(Integer pedidoId, StatusPedido novoStatus, Integer operadorId) {
		Pedido pedido = buscarPorId(pedidoId);
		Usuario operador = usuarioService.buscaPorId(operadorId);
		

		if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.FINALIZADO) {
			throw new NegocioException("Não é possível alterar o status de um pedido já encerrado.");
		}
		
		StatusPedido statusAnterior = pedido.getStatus();
		
		
		// VALIDAÇÃO: Pedidos encerrados não podem mudar de status
		if(statusAnterior == StatusPedido.CANCELADO || statusAnterior == StatusPedido.FINALIZADO) {
			throw new NegocioException("Não é possivel alterar um pedido já encerrado");
		}
		
		// VALIDAÇÃO: Ninguém pode mudar para finalizado via este método
		if(novoStatus == StatusPedido.FINALIZADO) {
			throw new NegocioException("Para finalizar um pedido, utilize o endpoint específico POST /pedidos/{id}/finalizar. " +
		            					"Este método valida pagamento, entregador e outras regras de negócio.");
		}
		
	    // VALIDAÇÃO: Para mudar para SAIU_PARA_ENTREGA, precisa ter entregador
	    if (novoStatus == StatusPedido.SAIU_PARA_ENTREGA) {
	        if (pedido.getEntregador() == null) {
	            throw new NegocioException(
	                "Não é possível despachar o pedido para entrega sem vincular um entregador. " +
	                "Use o endpoint PUT /pedidos/{id}/entregador primeiro."
	            );
	        }
	        // Verifica se o entregador está ativo
	        if (!pedido.getEntregador().isAtivo()) {
	            throw new NegocioException(
	                "O entregador " + pedido.getEntregador().getNome() + " está inativo e não pode realizar entregas."
	            );
	        }
	    }
		
		//Altera o status (já registra o histórico)
		pedido.alteraStatus(novoStatus, operador);
		
		// Persiste antes de publicar o evento
		Pedido pedidoSalvo = pedidoRepository.save(pedido);

		// Publica o evento para quem estiver ouvindo
		eventPublisher.publishEvent(new PedidoStatusChangeEvent(this, pedidoSalvo, statusAnterior, statusAnterior));
		
		return pedidoSalvo;
	}

	// =============================================================
	// 9. BUSCAS
	// =============================================================
	@Transactional(readOnly = true)
	public Pedido buscarPorId(Integer id) {
		return pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));
	}
	
	@Transactional(readOnly = true)
	public List<Pedido> buscarPedidosAtivosDaMesa(Integer numeroMesa) {

		// Definindo aqui o que NÃO queremos pedidos encerrados
		List<StatusPedido> statusFechados = List.of(StatusPedido.FINALIZADO, StatusPedido.CANCELADO,
				StatusPedido.SAIU_PARA_ENTREGA);

		// Chamando o repository passando os filtros "fixos" da regra de negócio
		return pedidoRepository.findByTipoAndNumeroMesaAndStatusNotIn(TipoPedido.MESA, numeroMesa, statusFechados);
	}
	
	// =============================================================
	//  MÉTODOS DTO (conversão dentro da transação)
	// =============================================================
	@Transactional
	public PedidoResponse iniciarPedidoResponse (PedidoRequest request, Integer operadorId) {
		
		Pedido pedido = iniciarPedido(
				request.clienteId(), 
				operadorId, 
				request.tipo(), 
				request.nomeBalcao(), 
				request.numeroMesa(), 
				request.obsPedido());
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse adicionarItemAoPedidoResponse(Integer pedidoId, 
			AdicionarItemRequest request) {
		
		// Passamos o produtoId (que pode ser null para Pizzas)
		Pedido pedido = adicionarItemAoPedido(
				pedidoId, 
				request.tamanhoId(), 
				request.produtoId()); // pode ser null
		
		return PedidoResponse.from(pedido);
	}
	
	@Transactional
	public PedidoResponse adicionarSaborAoItemResponse(Integer pedidoId, AdicionarSaborRequest request) {
		
		Pedido pedido = adicionarSaborAoItem(
				pedidoId, 
				request.itemId(), 
				request.produtoId()
				);
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse adicionarCustomizacaoResponse (Integer pedidoId, CustomizacaoRequest request) {
		
		Pedido pedido = adicionarCustomizacao(
				pedidoId, 
				request.itemId(), 
				request.sbItemId(), 
				request.ingredienteId(), 
				request.tipo()
				);
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse adicionarBordaAoItemResponse(Integer pedidoId, BordaItemRequest request) {
		
		Pedido pedido = adicionarBordaAoItem(
				pedidoId, 
				request.itemId(), 
				request.bordaId()
				); 
		return PedidoResponse.from(pedido);
	}
	
	@Transactional
	public PedidoResponse vincularEntregadorResponse (Integer pedidoId, VincularEntregadorRequest request, Integer operadorId ) {
		
		Pedido pedido = vincularEntregador(
				pedidoId, 
				request.entregadorId(), 
				operadorId
				);
		return PedidoResponse.from(pedido);
	}
	
	@Transactional
	public PedidoResponse mudarStatusResponse (
				Integer pedidoId, 
				StatusPedidoRequest request, 
				Integer operadorId) {
		
		Pedido pedido = mudarStatus(pedidoId, request.status(), operadorId);
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse finalizarPedidoResponse(Integer pedidoId, Integer operadorId) {
		
		Pedido pedido = finalizarPedido(pedidoId, operadorId);
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse cancelarPedidoResponse (Integer pedidoId, CancelarPedidoRequest request, Integer operadorId) {
		
		Pedido pedido = cancelarPedido(pedidoId, request.gerenteId(), request.motivo(), operadorId);
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse reabrirPedidoCanceladoResponse (
			Integer pedidoId, 
			ReabrirPedidoRequest request, 
			Integer operadorId
			) {
		
		Pedido pedido = reabrirPedidoCancelado(pedidoId, request.gerenteId(), operadorId);
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional(readOnly = true)
	public PedidoResponse buscarPorIdResponse(Integer pedidoId) {
		
		Pedido pedido = buscarPorId(pedidoId);
		
		return PedidoResponse.from(pedido);
		
	}
	
	@Transactional
	public PedidoResponse removerCustomizacaoResponse (Integer pedidoId, Integer itemId, 
			Integer subItemId, Integer ingredienteId) {
		
		Pedido pedido = removerCustomizacao(pedidoId, itemId, subItemId, ingredienteId);
		
		return PedidoResponse.from(pedido);	
	}
	
	@Transactional
	public PedidoResponse removerBordaDoItemResponse (Integer pedidoId, Integer itemId, Integer bordaId) {
		
		Pedido pedido = removerBordaDoItem(pedidoId, itemId, bordaId);
		
		return PedidoResponse.from(pedido);
	}
	
	@Transactional(readOnly = true)
	public Page<PedidoResumoResponse> listarPedidosResponse(StatusPedido status, TipoPedido tipo, Pageable pageable){
		
		Page<Pedido> paginaPedidos;
		
		// Lógica de decisão: Se ambos os filtros foram informados, aplica os dois.
	    // Se apenas um foi informado, aplica apenas ele.
	    // Se nenhum foi informado, lista todos.
		if(status!= null && tipo != null) {
			paginaPedidos = pedidoRepository.finByStatusAndTipo(status, tipo, pageable);
		}else if (status != null ) {
			paginaPedidos = pedidoRepository.findByStatus(status, pageable);
		}else if ( tipo != null ) {
			paginaPedidos = pedidoRepository.findByTipo(tipo, pageable);
		}else {
			paginaPedidos = pedidoRepository.findAll(pageable);
		}
		
		// Converte cada Pedido para PedidoResumoResponse usando o método from
		return paginaPedidos.map(PedidoResumoResponse::from);
	}
	
	@Transactional(readOnly = true)
	public Page<PedidoResumoResponse> listarPedidosAbertosResponse (Pageable pageable){
		
		//Define os status que ENCERRAM o pedido (não são abertos )
		List<StatusPedido> statusFechado = List.of(
				StatusPedido.FINALIZADO,
				StatusPedido.CANCELADO
				);
		
		// Busca todos os pedido que NÃO estão nesses status 
		Page<Pedido> paginaPedidos = pedidoRepository.findByStatusNotIn(statusFechado, pageable);
		
		// Converte para DTO de resumo
		return paginaPedidos.map(PedidoResumoResponse::from);
		
		
	}

}
