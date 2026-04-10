package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoCustomizacao;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Borda;
import com.dsys.appfood.domain.model.Cliente;
import com.dsys.appfood.domain.model.Ingrediente;
import com.dsys.appfood.domain.model.ItemCustomizacao;
import com.dsys.appfood.domain.model.ItemPedido;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.Produto;
import com.dsys.appfood.domain.model.SubItemSabor;
import com.dsys.appfood.domain.model.Tamanho;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.exception.PedidoNaoEncontradoException;
import com.dsys.appfood.repository.ItemPedidoRepository;
import com.dsys.appfood.repository.PedidoRepository;

import java.math.BigDecimal;

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

	private final BordaService bordaService;
	private final IngredienteService ingredienteService;
	private final PedidoRepository pedidoRepository;
	private final TamanhoService tamanhoService;
	private final ProdutoService produtoService;
	private final UsuarioService usuarioService;
	private final ClienteService clienteService;

	public PedidoService(ItemPedidoRepository itemPedidoRepository, ClienteService clienteService,
			UsuarioService usuarioService, ProdutoService produtoService, TamanhoService tamanhoService,
			PedidoRepository pedidoRepository, IngredienteService ingredienteService, BordaService bordaService) {

		this.clienteService = clienteService;
		this.usuarioService = usuarioService;
		this.produtoService = produtoService;
		this.tamanhoService = tamanhoService;
		this.pedidoRepository = pedidoRepository;
		this.ingredienteService = ingredienteService;
		this.bordaService = bordaService;

	}

	// =============================================================
	// 1. NASCIMENTO DO PEDIDO (ABERTURA)
	// =============================================================
	@Transactional
	public Pedido iniciarPedido(Integer clienteId, Integer operadorId, TipoPedido tipo, String nomeBalcao) {

		// 1. Busca o Operador
		Usuario operador = usuarioService.buscaPorId(operadorId);

		// 2. Validação de Entrega
		if (tipo == TipoPedido.ENTREGA && clienteId == null) {
			throw new NegocioException("Para pedidos de entrega, é obrigatório informar o cliente.");
		}

		// 3. Validação de Balcão (Nome é obrigatório se não houver ID)
		if (tipo == TipoPedido.BALCAO && clienteId == null && (nomeBalcao == null || nomeBalcao.isEmpty())) {

			throw new NegocioException("Para pedidos de balcão sem cadastro, informe o nome do cliente");
		}

		// 4. Busca o Cliente apenas se o ID foi enviado (Evita erro de busca nula)
		Cliente cliente = null;
		if (clienteId != null) {
			cliente = clienteService.buscarClientePorId(clienteId);
		}

		// 5. Instancia e configura
		Pedido pedido = new Pedido();
		pedido.setTipo(tipo);
		pedido.setOperador(operador);
		pedido.setCliente(cliente);
		pedido.setNomeBalcao(nomeBalcao);

		// 6. Registra o status inicial (Usando seu método da Model que já grava
		// histórico)
		pedido.alteraStatus(StatusPedido.PEDIDO_INICIADO, operador);

		// Salvar no repository e retornar.
		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// 2. ADICIONANDO CORPO (ITENS E SABORES)
	// =============================================================

	@Transactional
	public Pedido adicionarItemAoPedido(Integer pedidoId, Integer tamanhoId) {

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

		// Adicionar o item ao pedido
		pedido.adicionarItem(item);

		// TODO 6: Salvar o Pedido. O CascadeType.ALL que você colocou na Model vai
		// salvar o ItemPedido automaticamente!
		return pedidoRepository.save(pedido);
	}

	@Transactional
	public Pedido adicionarSaborAoItem(Integer pedidoId, Integer itemId, Integer produtoId) {

		Pedido pedido = buscarPorId(pedidoId);

		if (pedido.getStatus() != StatusPedido.PEDIDO_INICIADO && pedido.getStatus() != StatusPedido.PENDENTE) {
			throw new NegocioException("Esse Status não permite adicionar sabor ao item ");
		}

		ItemPedido itemEncontrado = pedido.getItens().stream().filter(item -> item.getId().equals(itemId)).findFirst()
				.orElseThrow(() -> new NegocioException(
						"Item ID " + itemId + " não pertence ao pedido ID " + pedido.getId()));

		Produto produto = produtoService.buscarProdutoPorId(produtoId);

		BigDecimal precoSabor = produto.obterPrecoParaTamanho(itemEncontrado.getTamanho());

		SubItemSabor subItem = new SubItemSabor(produto, precoSabor);

		itemEncontrado.adicionarSabor(subItem);

		pedido.calcularTotal();

		return pedidoRepository.save(pedido);
	}

	// =============================================================
	// CUSTOMIZAÇÕES
	// =============================================================
	
	// --- 1. ADICIONAR INGREDIENTE AO SABOR ---
	@Transactional
	public Pedido adicionarCustomizacao(Integer pedidoId, Integer itemId, Integer subItemId,
			Integer ingredienteId, TipoCustomizacao tipo) {
		
		//Verifica se o tipo de customização é Adição ou Remoção 
			if(tipo != TipoCustomizacao.ADICIONAL && tipo != TipoCustomizacao.REMOCAO) {
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
				.filter(sub ->sub.getId().equals(subItemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException("Sabor não encontrado nesse item"));
		
		// Buscar o Ingrediente
		Ingrediente ingrediente = ingredienteService.buscarIngredientePorId(ingredienteId);
		
		// Definir o valor.
		BigDecimal preco = (tipo == TipoCustomizacao.REMOCAO) ? BigDecimal.ZERO : ingrediente.getValorAdicional();
		
		// Instanciar a Customizacao
		ItemCustomizacao custimizacao = new ItemCustomizacao();
		custimizacao.associarSubItem(saborEncontrado);
		custimizacao.setIngrediente(ingrediente);
		custimizacao.setTipoCustomizacao(tipo);
		custimizacao.adicionarValorACustomizacao(preco);

		//Adicionar na Lista
		saborEncontrado.adicionarCustomizacao(custimizacao);
		
		//Recalcular e Salvar
		pedido.calcularTotal();
		return pedidoRepository.save(pedido);
	}
	
	// --- 2. ADICIONAR BORDA AO ITEM --- 
	@Transactional
	public Pedido adicionarBordaAoItem(Integer pedidoId, Integer itemId, Integer bordaId) {
		
		//Buscar Pedido
		Pedido pedido = buscarPorId(pedidoId);
		
		//Achar o Item 
		ItemPedido itemEncontrado = pedido.getItens().stream()
				.filter(item -> item.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new NegocioException("Item não encontrado"));
		
		// Buscar o Ingrediete da borda
		Borda borda =  bordaService.buscarBordaPorId(bordaId);
		
		// Instanciar a customização ligada direto ao item (não ao sabor)
		ItemCustomizacao customizacao = new ItemCustomizacao();
		customizacao.associarItemPedido(itemEncontrado);
		customizacao.setBorda(borda);
		customizacao.setTipoCustomizacao(TipoCustomizacao.BORDA);
		customizacao.adicionarValorACustomizacao(borda.getValorAdicional());
		
		// Adicionar ao item 
		itemEncontrado.adicionarCustomizacaoGlobal(customizacao);
		
		//Calcular Total 
		pedido.calcularTotal();
		
		//Salvar e retornar
		return pedidoRepository.save(pedido);
	}
	
	// --- 3. REMOVER INGREDIENTE DO SABOR ---
		@Transactional
		public Pedido removerCustomizacao(Integer pedidoId, Integer itemId, Integer subItemId,
				Integer ingredienteId) {
			
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
					.filter(sub ->sub.getId().equals(subItemId))
					.findFirst()
					.orElseThrow(() -> new NegocioException("Sabor não encontrado nesse item"));
			
			//Encontrar a customização REAL que está na lista para poder remover
			ItemCustomizacao customizacaoExistente = saborEncontrado.getCustomizacoes().stream()
					.filter(c -> c.getIngrediente().getId().equals(ingredienteId))
					.findFirst()
					.orElseThrow(() -> new NegocioException("Esta customização não existe neste sabor."));

			//Remover da Lista
			saborEncontrado.removerCustomizacao(customizacaoExistente);
			
			//Recalcular e Salvar
			pedido.calcularTotal();
			return pedidoRepository.save(pedido);
		}
		
		// --- 4. REMOVER BORDA DO ITEM --- 
		@Transactional
		public Pedido removerBordaDoItem(Integer pedidoId, Integer itemId, Integer bordaId) {
			
			//Buscar Pedido
			Pedido pedido = buscarPorId(pedidoId);
			
			//Achar o Item 
			ItemPedido itemEncontrado = pedido.getItens().stream()
					.filter(item -> item.getId().equals(itemId))
					.findFirst()
					.orElseThrow(() -> new NegocioException("Item não encontrado"));
			
			//Encontrar a customização REAL que está na lista para poder remover
			ItemCustomizacao bordaExistente = itemEncontrado.getCustomizacoesGlobais().stream()
					.filter(c -> c.getBorda().getId().equals(bordaId))
					.findFirst()
					.orElseThrow(() -> new NegocioException("Esta borda não existe neste Item."));
			
			// Adicionar ao item 
			itemEncontrado.removerCustomizacaoGlobal(bordaExistente);
			
			//Calcular Total 
			pedido.calcularTotal();
			
			//Salvar e retornar
			return pedidoRepository.save(pedido);
		}
	
	

	// =============================================================
	// MÉTODOS PRIVADOS AUXILIARES
	// =============================================================
	@Transactional(readOnly = true)
	private Pedido buscarPorId(Integer id) {
		return pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));
	}

}
