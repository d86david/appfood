package com.dsys.appfood.service;

import com.dsys.appfood.domain.enums.TipoDocumento;
import com.dsys.appfood.domain.model.Cliente;
import com.dsys.appfood.domain.model.Endereco;
import com.dsys.appfood.exception.ClienteJaCadastradoException;
import com.dsys.appfood.exception.ClienteNaoEncontradoException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.ClienteRepository;
import com.dsys.appfood.repository.EnderecoRepository;

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

	private final EnderecoRepository enderecoRepository;
	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {

		this.clienteRepository = clienteRepository;
		this.enderecoRepository = enderecoRepository;
	}

	// =============================================================
	// CADASTRAR
	// =============================================================

	@Transactional
	public Cliente cadastrarCliente(String nome, String telefone, Endereco endereco) {

		// VALIDAÇÕES SEM BANCO
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (telefone == null || telefone.isBlank()) {
			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		// VALIDAÇÕES COM BANCO

		// Busca e trata o cenário de cliente já existente
		Optional<Cliente> clienteExistente = clienteRepository.findByTelefone(telefone);

		if (clienteExistente.isPresent()) {
			Cliente existente = clienteExistente.get();

			if (existente.isAtivo()) {
				throw new ClienteJaCadastradoException(telefone);
			}

			// Reativa e atualiza os dados
			existente.ativar();
			existente.setNome(nome);
			existente.setEndereco(endereco);

			return existente;

		}

		// MONTAR E SALVAR

		// Se não existe, cria um novo
		Cliente cliente = new Cliente(nome, telefone, endereco);

		return clienteRepository.save(cliente);
	}

	// =============================================================
	// EDITAR
	// =============================================================

	@Transactional
	public Cliente editarCliente(Integer id, String novoNome, String novoTelefone, TipoDocumento novoTpDocumento,
			String novoDocumento, Endereco novoEndereco) {

		// VALIDAÇÕES SEM BANCO
		if (novoNome == null || novoNome.isBlank()) {
			throw new IllegalArgumentException("O nome deve ser informado");
		}

		if (novoTelefone == null || novoTelefone.isBlank()) {

			throw new IllegalArgumentException("O telefone deve ser informado");
		}

		String nomePadronizado = novoNome.trim();
		String telefonePadronizado = novoTelefone.trim();
		String documentoPadronizado = novoDocumento.trim();

		Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));

		clienteRepository.findByTelefone(telefonePadronizado).ifPresent(existente -> {
			if (!existente.getTelefone().equals(telefonePadronizado)) {
				throw new IllegalStateException("Ja existe um cliente com o telefone: " + telefonePadronizado);
			}
		});

		if (novoEndereco != null) {
			if (enderecoRepository.existsById(novoEndereco.getId())) {
				throw new IllegalStateException("Ja existe um cliente com esse endereço");
			}

			cliente.setEndereco(novoEndereco);
		}

		cliente.setNome(nomePadronizado);
		cliente.setTelefone(telefonePadronizado);
		cliente.setTipoDocumento(novoTpDocumento);
		cliente.setDocumento(documentoPadronizado);

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
	public void inativarCliente(Integer id) {

		// Busca Cliente e lança Exceção se não existir
		Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));

		// Confirma se usuario não está ativo
		if (!cliente.isAtivo()) {
			throw new NegocioException("Cliente já ativo id: " + id);
		}

		cliente.ativar();

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
	public Cliente buscarClientePeloTelefone(String telefone){
		return clienteRepository.findByTelefone(telefone)
				.orElseThrow(() -> new ClienteNaoEncontradoException(telefone));
	}
	
	@Transactional(readOnly = true)
	public Cliente buscarClientePorId(Integer id) {
		return clienteRepository.findById(id).orElseThrow(
				() -> new ClienteNaoEncontradoException(id));
	}

}
