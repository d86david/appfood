package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.*;
import com.dsys.appfood.dto.DesempenhoEntregadorResponse;
import com.dsys.appfood.dto.ProdutoVendidoResponse;
import com.dsys.appfood.dto.vendasPeriodoResponse;
import com.dsys.appfood.repository.*;

/**
 * Serviço dedicado à geração de relatórios gerenciais.
 * 
 * CONCEITOS IMPORTANTES:
 * 
 * 1. Relatórios são operações de LEITURA intensiva:
 *    - Todos os métodos usam @Transactional(readOnly = true).
 *    - Não há modificação de dados, apenas agregação e sumarização.
 * 
 * 2. Uso de Stream API e Collectors:
 *    - Agrupamentos, somas, médias, contagens.
 *    - Transformação de listas de entidades em DTOs de relatório.
 * 
 * 3. Separação de responsabilidades:
 *    - Este serviço NÃO acessa repositórios de outras entidades diretamente?
 *    - Pode acessar, pois é um serviço de "integração" para relatórios.
 *    - Injeta os repositórios necessários para evitar dependência circular.
 */
@Service
public class RelatorioService {

    private final PedidoRepository pedidoRepository;

    private final ItemPedidoRepository itemPedidoRepository;


    public RelatorioService(PedidoRepository pedidoRepository,
                            ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * Relatório de vendas por período: total de pedidos, faturamento, ticket médio.
     */
    @Transactional(readOnly = true)
    public vendasPeriodoResponse vendasNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<Pedido> pedidos = pedidoRepository.findByDtHoraAberturaBetween(inicio, fim);

        long totalPedidos = pedidos.size();
        BigDecimal faturamentoBruto = pedidos.stream()
                .map(Pedido::getValorBruto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontos = pedidos.stream()
                .map(p -> p.getDesconto() != null ? p.getDesconto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal faturamentoLiquido = pedidos.stream()
                .map(Pedido::calcularTotal) // valor total (bruto - desconto + taxa)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ticketMedio = totalPedidos > 0
                ? faturamentoLiquido.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Agrupamento por tipo de pedido
        Map<TipoPedido, Long> pedidosPorTipo = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getTipo, Collectors.counting()));

        return new vendasPeriodoResponse(dataInicio, dataFim, totalPedidos,
                faturamentoBruto, totalDescontos, faturamentoLiquido, ticketMedio, pedidosPorTipo);
    }

    /**
     * Produtos mais vendidos em um período.
     */
    @Transactional(readOnly = true)
    public List<ProdutoVendidoResponse> produtosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, int limite) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoDtHoraAberturaBetween(inicio, fim);

        // Mapa de Produto -> Quantidade total vendida
        Map<Produto, Integer> quantidadePorProduto = new HashMap<>();
        Map<Produto, BigDecimal> faturamentoPorProduto = new HashMap<>();

        for (ItemPedido item : itens) {
            for (SubItemSabor sub : item.getSubItens()) {
                Produto produto = sub.getProduto();
                quantidadePorProduto.merge(produto, 1, Integer::sum);
                faturamentoPorProduto.merge(produto, sub.getPrecoSabor(), BigDecimal::add);
            }
        }

        return quantidadePorProduto.entrySet().stream()
                .sorted(Map.Entry.<Produto, Integer>comparingByValue().reversed())
                .limit(limite)
                .map(entry -> {
                    Produto p = entry.getKey();
                    return new ProdutoVendidoResponse(p.getId(), p.getNome(),
                            entry.getValue(), faturamentoPorProduto.get(p));
                })
                .collect(Collectors.toList());
    }

    /**
     * Relatório de desempenho por entregador (quantidade de entregas e valor).
     */
    @Transactional(readOnly = true)
    public List<DesempenhoEntregadorResponse> desempenhoEntregadores(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<Pedido> pedidosEntrega = pedidoRepository.findByTipoAndDtHoraAberturaBetween(
                TipoPedido.ENTREGA, inicio, fim);

        Map<Entregador, List<Pedido>> porEntregador = pedidosEntrega.stream()
                .filter(p -> p.getEntregador() != null)
                .collect(Collectors.groupingBy(Pedido::getEntregador));

        return porEntregador.entrySet().stream()
                .map(entry -> {
                    Entregador e = entry.getKey();
                    List<Pedido> pedidos = entry.getValue();
                    long totalEntregas = pedidos.size();
                    BigDecimal totalTaxas = pedidos.stream()
                            .map(p -> p.getTaxaEntrega() != null ? p.getTaxaEntrega() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new DesempenhoEntregadorResponse(e.getId(), e.getNome(), totalEntregas, totalTaxas);
                })
                .sorted(Comparator.comparingLong(DesempenhoEntregadorResponse::getTotalEntregas).reversed())
                .collect(Collectors.toList());
    }


}