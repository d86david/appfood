package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.TipoDocumento;
import com.dsys.appfood.domain.model.Cliente;
import com.dsys.appfood.domain.model.Endereco;
import com.dsys.appfood.exception.ClienteJaCadastradoException;
import com.dsys.appfood.exception.ClienteNaoEncontradoException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel pela autenticação e validações relacionadas ao cliente
 * 
 * Responsabilidade ÚNICA: autenticação e validações relacionadas de clientes
 * 
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class ClienteService {

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {

		this.clienteRepository = clienteRepository;
	}

	// =============================================================
	// CADASTRO RAPIDO
	// =============================================================

	/**
	 * Cadastro de cliente rápido apenas com as informações importante para o pedido
	 * Usado na tela de pedido quando não tiver o cliente cadastrado e precisar
	 * realizar um cadastro mais rapido com o minimo de informação possivel
	 * 
	 */
	@Transactional
	public Cliente cadastrarClienteRapido(String nome, String telefonePrincipal, String observacaoCliente,
			Endereco endereco) {

		// VALIDAÇÕES SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (telefonePrincipal == null || telefonePrincipal.isBlank()) {
			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		String nomePadronizado = nome.trim();

		// VALIDAÇÕES COM BANCO

		// Busca e trata o cenário de cliente já existente
		Optional<Cliente> clienteExistente = clienteRepository.findByTelefonePrincipal(telefonePrincipal);

		if (clienteExistente.isPresent()) {
			Cliente existente = clienteExistente.get();

			if (existente.isAtivo()) {
				throw new ClienteJaCadastradoException(telefonePrincipal);
			}

			// Reativa e atualiza os dados
			existente.ativar();
			existente.setNome(nomePadronizado);
			existente.setEndereco(endereco);
			existente.setObservacaoCliente(observacaoCliente);

			return clienteRepository.save(existente);

		}

		// MONTAR E SALVAR

		// Se não existe, cria um novo
		Cliente cliente = new Cliente(nomePadronizado, telefonePrincipal, observacaoCliente, endereco);

		return clienteRepository.save(cliente);
	}

	// =============================================================
	// CADASTRO COMPLETO
	// =============================================================

	/**
	 * Cadastro de cliente completo com todas as informações 
	 * Usado na tela de cadastros quando o operador não estiver em atendimento
	 * 
	 */
	@Transactional
	public Cliente cadastrarClienteCompleto(String nome, String telefonePrincipal, String telefoneSecundario, TipoDocumento tipoDocumento,
			String documento, String email, String observacaoCliente, Endereco endereco) {

		// VALIDAÇÕES SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (telefonePrincipal == null || telefonePrincipal.isBlank()) {
			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		String nomePadronizado = nome.trim();
		String emailPadronizado = email.trim();

		// VALIDAÇÕES COM BANCO

		// Busca e trata o cenário de cliente já existente
		Optional<Cliente> clienteExistente = clienteRepository.findByTelefonePrincipal(telefonePrincipal);

		if (clienteExistente.isPresent()) {
			Cliente existente = clienteExistente.get();

			if (existente.isAtivo()) {
				throw new ClienteJaCadastradoException(telefonePrincipal);
			}

			// Reativa e atualiza os dados
			existente.ativar();
			existente.setNome(nomePadronizado);
			existente.setTelefoneSecundario(telefoneSecundario);
			existente.setTipoDocumento(tipoDocumento);
			existente.setDocumento(documento);
			existente.setEmail(emailPadronizado);
			existente.setEndereco(endereco);
			existente.setObservacaoCliente(observacaoCliente);

			return clienteRepository.save(existente);

		}

		// MONTAR E SALVAR

		// Se não existe, cria um novo
		Cliente cliente = new Cliente(nomePadronizado, telefonePrincipal, telefoneSecundario, tipoDocumento, 
				documento, emailPadronizado, observacaoCliente, endereco);

		return clienteRepository.save(cliente);
	}

	// =============================================================
	// EDITAR
	// =============================================================

	@Transactional
	public Cliente editarCliente(Integer id, String novoNome, String novoTelefonePrincipal, String novoTelefoneSecundario, TipoDocumento novoTipoDocumento,
			String novoDocumento, String novoEmail, String novaObservacaoCliente, Endereco novoEndereco) {

		// VALIDAÇÕES SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (novoTelefonePrincipal == null || novoTelefonePrincipal.isBlank()) {

			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		String nomePadronizado = novoNome.trim();
		String emailPadronizado = novoEmail.trim();

		Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));

		clienteRepository.findByTelefonePrincipal(novoTelefonePrincipal).ifPresent(existente -> {
			if (!existente.getId().equals(id)) {
				throw new IllegalStateException("Ja existe um cliente com o telefone: " + novoTelefonePrincipal);
			}
		});

		if (novoEndereco != null) {
			cliente.setEndereco(novoEndereco);
		}

		cliente.setNome(nomePadronizado);
		cliente.alterarTelefonePrincipal(novoTelefonePrincipal);
		cliente.setTelefoneSecundario(novoTelefoneSecundario);
		cliente.setTipoDocumento(novoTipoDocumento);
		cliente.setDocumento(novoDocumento);
		cliente.setEmail(emailPadronizado);
		cliente.setObservacaoCliente(novaObservacaoCliente);

		return clienteRepository.save(cliente);

	}

	// =============================================================
	// ATIVAR
	// =============================================================
	@Transactional
	public void ativarCliente(Integer id) {

		// Busca Cliente e lança Exceção se não existir
		Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));

		// Confirma se usuario está ativo
		if (cliente.isAtivo()) {
			throw new NegocioException("Cliente já ativo id: " + id);
		}

		cliente.ativar();

		clienteRepository.save(cliente);
	}

	// =============================================================
	// INATIVAR
	// =============================================================
	@Transactional
	public void inativarCliente(Integer id) {

		// Busca Cliente e lança Exceção se não existir
		Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));

		// Confirma se usuario não está ativo
		if (!cliente.isAtivo()) {
			throw new NegocioException("Cliente já ativo id: " + id);
		}

		cliente.inativar();

		clienteRepository.save(cliente);
	}

	// =============================================================
	// BUSCAR
	// =============================================================

	@Transactional(readOnly = true)
	public List<Cliente> listarTodosClientes() {
		return clienteRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Cliente> buscarClientePeloNome(String nome) {
		// Validações
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O Nome deve ser informado");
		}

		return clienteRepository.findByNomeContainingIgnoreCase(nome);
	}

	@Transactional(readOnly = true)
	public Cliente buscarClientePeloTelefone(String telefone) {
		return clienteRepository.findByTelefonePrincipal(telefone)
				.orElseThrow(() -> new ClienteNaoEncontradoException(telefone));
	}

	@Transactional(readOnly = true)
	public Cliente buscarClientePorId(Integer id) {
		return clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));
	}

}
