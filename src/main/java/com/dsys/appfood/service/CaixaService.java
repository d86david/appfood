package com.dsys.appfood.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.dsys.appfood.domain.enums.StatusCaixa;
import com.dsys.appfood.domain.enums.TipoUsuario;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.MovimentacaoCaixa;
import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.repository.CaixaRepository;
import com.dsys.appfood.repository.MovimentacaoCaixaRepository;
import com.dsys.appfood.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

/**
 * Classe responsavel por orquestar todas as regras de negócio relacionadas ao Caixa
 * 
 * Responsabilidade ÚNICA: gerencias o ciclo de vida do Caixa (abertura -> movimentação -> fechamento)
 * 
 * Este Service NÃO sabe nada sobre HTTP, Apenas processa e lança exceções de negócio.
 */
@Service
public class CaixaService {
	
	//INJEÇÃO DAS DEPENDENCIAS usadas no Service, apenas 3 
	private final CaixaRepository caixaRepository;
	private final UsuarioRepository usuarioRepository;
	private final MovimentacaoCaixaRepository movimentacaoRepository;
	
	//Injeção via construtor - campos final garatem a imutabilidade
	public CaixaService(CaixaRepository caixaRepository, 
						UsuarioRepository usuarioRepository, 
						MovimentacaoCaixaRepository movimentacaoRepository ) {
		this.caixaRepository = caixaRepository;
		this.usuarioRepository = usuarioRepository;
		this.movimentacaoRepository = movimentacaoRepository;
	}
	
	//============================================================================
	// 1º Ação do CAIXA - Abrir Caixa 
	// Regaras da Ação: gerente Autoriza | operador sem caixa aberto | valor >= 0
	//============================================================================
	
	@Transactional
	public Caixa abrirCaixa(Integer operadorId, Integer gerenteId, BigDecimal valorInicial) {
		//REGRA: valor inicial não pode ser negativo
		// (pode ser zerado - caixa abre sem troco)
		if(valorInicial == null) {
			valorInicial = BigDecimal.ZERO; //se o valor não for informado, assume zero
		}
		
		if(valorInicial.signum() == -1) {
			throw new IllegalArgumentException("O valor inicial do caixa não pode ser negativo.");
		}
		
		//Busca o Operador - Lança exeção clara se não existir
		Usuario operador = usuarioRepository.findById(operadorId)
				.orElseThrow(() -> new IllegalArgumentException(
						"Operador não encontrado "+ operadorId));
		
		//Busca o gerente autorizador
		Usuario gerente = usuarioRepository.findById(gerenteId)
				.orElseThrow(() -> new IllegalArgumentException(
						"Gerente não encontrado " + gerenteId));
		
		//REGRA: quem autoriza é precisa ser Gerente ou ADM
		//Sem isso, qualquer operador poderia "autorizar" a si mesmo
		if(gerente.getTipo() != TipoUsuario.GERENTE && gerente.getTipo() != TipoUsuario.ADM) {
			throw new IllegalArgumentException("Apenas gerentes podem autorizar abertura de caixa.");
		}
		
		//REGRA: operador não pode abrir um segundo caixa se ja tem um aberto
		boolean temCaixaAberto = caixaRepository
				.findByOperadorAndStatus(operador, StatusCaixa.ABERTO)
				.isPresent();
		
		if(temCaixaAberto) {
			throw new IllegalStateException(
					"O operador "+ operador.getNome() +" já possui um caixa aberto.");
		}
		
		//Cria um novo caixa, que começa fechado por padrão
		Caixa caixa = new Caixa();
		
		//Chama o método de Abrir caixa da Model
		caixa.abrirCaixa(valorInicial, gerente);
		caixa.setOperador(operador);
		
		// Persiste e retorna
		return caixaRepository.save(caixa);	
	}
	
	//============================================================================
	// 2º Ação do CAIXA - Registrar Venda (Entrada no Caixa)
	// Regaras da Ação: O caixa deve estar aberto | valor > 0
	//============================================================================

	@Transactional
	public MovimentacaoCaixa registrarVenda(Integer caixaId, Integer pedidoId,
											BigDecimal valorPago) {
		//Validação sem acessar o banco 
		if(valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor do pagamento deve ser positivo.");
		}
		
		//Busca o caixa
		Caixa caixa = buscaCaixaAberto(caixaId); //Utilizando o método privado
		
		// Cria a movimentação usando o Static Factory Method da Model
		MovimentacaoCaixa entrada = MovimentacaoCaixa.criarEntradaCaixa(caixa, valorPago, pedidoId);
		
		// Atualiza o saldo — chama o método da Model (que já valida se está aberto)
		caixa.atualizarSaldo(valorPago, entrada.getTipo());
		
		// Salva a movimentação e o caixa atualizado
		caixaRepository.save(caixa);
		return movimentacaoRepository.save(entrada);
	}
	
	//=========================================================
	//MÉTODO PRIVADO - reutilizado internamente, 
	//evita repetição nos métodos que precisam dessa verificação
	//=========================================================
	private Caixa buscaCaixaAberto(Integer caixaId) {
		Caixa caixa = caixaRepository.findById(caixaId)
				.orElseThrow(() -> new IllegalArgumentException(
						"Caixa não encontrado " + caixaId));
		if(caixa.getStatus() != StatusCaixa.ABERTO) {
			throw new IllegalStateException(
					"Esta operação requer um caixa aberto. Status atual: " + caixa.getStatus());
		}
		
		return caixa;
	}
}
