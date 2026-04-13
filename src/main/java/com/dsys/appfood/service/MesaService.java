package com.dsys.appfood.service;

import com.dsys.appfood.domain.model.Mesa;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.exception.MesaJaCadastradaException;
import com.dsys.appfood.exception.MesaNaoEncontradaException;
import com.dsys.appfood.exception.NegocioException;
import com.dsys.appfood.repository.MesaRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsável por gerenciar o ciclo de vida das mesas do estabelecimento.
 * 
 * Responsabilidade ÚNICA: gerenciar o ciclo de vida do das mesas 
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de
 * negócio.
 */
@Service
public class MesaService {
	
	
	private final PedidoService pedidoService;
	private final MesaRepository mesaRepository;

	public MesaService(MesaRepository mesaRepository, PedidoService pedidoService) {
		
		this.mesaRepository = mesaRepository;
		this.pedidoService = pedidoService;
	}
	
	// ============================================================
    // CADASTRO DE MESA
    // ============================================================
	@Transactional
	public Mesa cadastrarMesa(Integer numero, Integer capacidade) {
		
		// VALIDAÇÕES SEM ACESSO AO BANCO
		if(numero == null || numero <= 0) {
			throw new IllegalArgumentException("O numero da mesa deve ser um valor positivo");
		}
		
		/*
		 * Capacidade pode ser null (não informada), 
		 * aí assumimos a capacidade padrão de 4 lugares
		 */
		if(capacidade == null ) {
			capacidade = 4;
		}
		
		 // VALIDAÇÕES COM BANCO DE DADOS
		mesaRepository.findByNumero(numero).ifPresent(mesaExistente -> {
			throw new MesaJaCadastradaException(numero);
		});
		
		//Instaciar e popular objeto com dados validados
		Mesa mesa = new Mesa();
		mesa.setNumero(numero);
		mesa.setCapacidade(capacidade);
		mesa.setAtiva(true);
		
		return mesaRepository.save(mesa);
	}
	
	// ============================================================
    // OCUPAR MESA
    // ============================================================
	@Transactional
	public Mesa ocuparMesa(Integer numeroMesa) {
		
		// Fazer busca pelo numero
		Mesa mesa = buscarPorNumero(numeroMesa);
		
		//Verifica se a mesa está ativa
		if(!mesa.isAtiva()) {
			throw new NegocioException("A mesa" + numeroMesa + " está inativa e não pode ser ocupada.");
		}
		
		//Chama Metodo da Model que sabe como ocupar uma mesa
		mesa.ocupar();
		
		// Persiste a mudança de estado
		return mesaRepository.save(mesa);
	}
	
	// ============================================================
    // LIBERAR MESA
    // ============================================================
	@Transactional
	public Mesa liberarMesa(Integer numeroMesa) {
		
		Mesa mesa = buscarMesaPorId(numeroMesa);
		
		//Verificar Pedido Pendentes nessa mesa
		List<Pedido> pedidosAtivos = pedidoService.buscarPedidosAtivosDaMesa(numeroMesa);
		
		if(!pedidosAtivos.isEmpty()) {
			throw new NegocioException("Não é possivel liberar a mesa "+ numeroMesa +
					". Existe(m) " + pedidosAtivos.size() + " pedido(s) em andamento");
		}
		
		mesa.liberar();
		
		return mesaRepository.save(mesa);
		
	}
	
	
	
	// ============================================================
    // CONSULTAS 
    // ============================================================
	
	//Busca Mesa por numero
	@Transactional(readOnly = true)
	public Mesa buscarPorNumero(Integer numero) {
		return mesaRepository.findByNumero(numero)
				.orElseThrow(() -> new MesaNaoEncontradaException(numero));
	}
	
	//Busca Mesa por ID
	@Transactional(readOnly = true)
	public Mesa buscarMesaPorId(Integer id) {
		return mesaRepository.findById(id)
				.orElseThrow(() -> new MesaNaoEncontradaException(id));
	}
	
	// Listar todas as mesas
	@Transactional(readOnly = true)
	public List<Mesa> listarTodasAsMesas(){
		return mesaRepository.findAllByOrderByNumeroAsc();
	}
	
	//Listar mesas livres
	@Transactional(readOnly = true)
	public List<Mesa> listarMesasLivres(){
		return mesaRepository.findByOcupadaFalseAndAtivaTrueOrderByNumeroAsc();
	}
	
	//Listar mesas ocupadas
	@Transactional(readOnly = true)
	public List<Mesa> listarMesasOcupadas(){
		return mesaRepository.findAllByOrderByNumeroAsc().stream()
				.filter(mesa -> mesa.isOcupada() && mesa.isAtiva())
				.collect(Collectors.toList());
	}
	
	
	// ============================================================
    // MANUTENÇÃO DE MESAS
    // ============================================================
    
    /**
     * ATIVAR/DESATIVAR MESA (manutenção)
     * 
     * Mesas podem ser desativadas temporariamente (ex: quebrou uma cadeira)
     * sem precisar excluir do sistema (mantém histórico).
     */
    @Transactional
    public Mesa ativarMesa(Integer numero) {
        Mesa mesa = buscarPorNumero(numero);
        mesa.setAtiva(true);
        return mesaRepository.save(mesa);
    }
    
    @Transactional
    public Mesa inativarMesa(Integer numero) {
        Mesa mesa = buscarPorNumero(numero);
        
        // Não pode inativar mesa ocupada
        if (mesa.isOcupada()) {
            throw new NegocioException(
                "Não é possível inativar a mesa " + numero + " pois está ocupada."
            );
        }
        
        mesa.setAtiva(false);
        return mesaRepository.save(mesa);
    }
    
    // ============================================================
    // VERIFICAÇÕES RÁPIDAS
    // ============================================================
    
   
    /**
     * VERIFICAR SE MESA EXISTE
     */
    @Transactional(readOnly = true)
    public boolean existeMesa(Integer numero) {
        return mesaRepository.existsByNumero(numero);
    }
}
