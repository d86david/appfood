package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Caixa;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.dto.CaixaStatusResponse;
import com.dsys.appfood.dto.IndicadoresDiariosResponse;
import com.dsys.appfood.dto.PedidoResumoResponse;
import com.dsys.appfood.dto.ProdutoVendidoResponse;
import com.dsys.appfood.exception.NenhumCaixaAbertoException;
import com.dsys.appfood.repository.PedidoRepository;

/**
 * Serviço para fornecimento de dados resumidos para dashboards.
 * 
 * CONCEITOS IMPORTANTES:
 * 
 * 1. Dashboard é uma visão consolidada e rápida:
 *    - Métodos retornam apenas números ou listas pequenas.
 *    - Deve ser extremamente performático.
 * 
 * 2. Métricas comuns:
 *    - Vendas do dia (hoje).
 *    - Pedidos em andamento (status abertos).
 *    - Produtos mais vendidos do dia.
 *    - Resumo de caixa do dia.
 * 
 * 3. Este serviço utiliza consultas específicas dos repositórios.
 *    - Podemos criar métodos de consulta customizados nos repositórios se necessário.
 */
@Service
public class DashboardService {

    private final CaixaService caixaService;
	private final PedidoRepository pedidoRepository;
    private final RelatorioService relatorioService; // reutilizamos alguns relatórios

    public DashboardService(PedidoRepository pedidoRepository,
                            RelatorioService relatorioService, CaixaService caixaService) {
        this.pedidoRepository = pedidoRepository;
        this.relatorioService = relatorioService;
        this.caixaService = caixaService;
    }

    /**
     * Retorna os indicadores principais do dia atual.
     */
    @Transactional(readOnly = true)
    public IndicadoresDiariosResponse indicadoresHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(LocalTime.MAX);

        List<Pedido> pedidosHoje = pedidoRepository.findByDtHoraAberturaBetween(inicio, fim);

        long totalPedidos = pedidosHoje.size();
        long pedidosFinalizados = pedidosHoje.stream()
                .filter(p -> p.getStatus() == StatusPedido.FINALIZADO).count();
        long pedidosCancelados = pedidosHoje.stream()
                .filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();
        long pedidosEmAndamento = totalPedidos - pedidosFinalizados - pedidosCancelados;

        BigDecimal faturamentoHoje = pedidosHoje.stream()
                .filter(p -> p.getStatus() == StatusPedido.FINALIZADO)
                .map(Pedido::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Ticket médio
        BigDecimal ticketMedio = pedidosFinalizados > 0
                ? faturamentoHoje.divide(BigDecimal.valueOf(pedidosFinalizados), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Pedidos por tipo (hoje)
        long delivery = pedidosHoje.stream().filter(p -> p.getTipo() == TipoPedido.ENTREGA).count();
        long balcao = pedidosHoje.stream().filter(p -> p.getTipo() == TipoPedido.BALCAO).count();
        long mesa = pedidosHoje.stream().filter(p -> p.getTipo() == TipoPedido.MESA).count();

        return new IndicadoresDiariosResponse(hoje, totalPedidos, pedidosFinalizados, pedidosCancelados,
                pedidosEmAndamento, faturamentoHoje, ticketMedio, delivery, balcao, mesa);
    }

    /**
     * Lista os pedidos pendentes (em preparação, prontos, etc.) para a cozinha.
     */
    @Transactional(readOnly = true)
    public List<PedidoResumoResponse> pedidosPendentesCozinha() {
        List<StatusPedido> statusPendentes = List.of(
                StatusPedido.PENDENTE,
                StatusPedido.EM_PREPARACAO,
                StatusPedido.PEDIDO_INICIADO,
                StatusPedido.PRONTO
        );

        List<Pedido> pendentes = pedidoRepository.findByStatusInOrderByDtHoraAberturaAsc(statusPendentes);

        return pendentes.stream()
                .map(p -> new PedidoResumoResponse(
                        p.getId(),
                        p.getTipo(),
                        p.getStatus(),
                        p.getDtHoraAbertura(),
                        p.getCliente() != null ? p.getCliente().getNome() : p.getNomeBalcao(),
                        p.calcularTotal()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Top 5 produtos mais vendidos hoje.
     */
    @Transactional(readOnly = true)
    public List<ProdutoVendidoResponse> topProdutosHoje() {
        LocalDate hoje = LocalDate.now();
        return relatorioService.produtosMaisVendidos(hoje, hoje, 5);
    }

    /**
     * Status dos caixas abertos (se houver) - resumo rápido.
     */
    @Transactional(readOnly = true)
    public CaixaStatusResponse statusCaixaAtual() {
    	try {
			Caixa caixaAberto = caixaService.buscarCaixaAbertoAtual();
			return new CaixaStatusResponse(true, caixaAberto.getOperador().getNome(), caixaAberto.getSaldo());
		} catch (NenhumCaixaAbertoException e) {
			return new CaixaStatusResponse(false, null, BigDecimal.ZERO);
		}
    }
    
}