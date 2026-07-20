package com.dsys.appfood.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dsys.appfood.domain.enums.StatusPedido;
import com.dsys.appfood.domain.enums.TipoPedido;
import com.dsys.appfood.domain.model.Pedido;

public record PedidoResponse(

        Integer id,
        Integer clienteId,
        String clienteNome,
        TipoPedido tipo,
        String nomeBalcao,
        LocalDateTime dtHoraAbertura,
        LocalDateTime dtHoraFinalizacao,
        BigDecimal valorBruto,
        BigDecimal desconto,
        BigDecimal taxaEntrega,
        BigDecimal valorTotal,
        StatusPedido status,
        boolean pedidoPago,
        Integer operadorId,
        String operadorNome,
        Integer entregadorId,
        String entregadorNome,
        Integer numeroMesa,
        String obsPedido,
        List<ItemPedidoResponse> itens,
        List<PagamentoResponse> pagamentos

) {
    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente() != null ? pedido.getCliente().getId() : null,
                pedido.getCliente() != null ? pedido.getCliente().getNome() : pedido.getNomeBalcao(),
                pedido.getTipo(),
                pedido.getNomeBalcao(),
                pedido.getDtHoraAbertura(),
                pedido.getDtHoraFinalizacao(),
                pedido.getValorBruto(),
                pedido.getDesconto(),
                pedido.getTaxaEntrega(),
                pedido.calcularTotal(),
                pedido.getStatus(),
                pedido.isPedidoPago(),
                pedido.getOperador() != null ? pedido.getOperador().getId() : null,
                pedido.getOperador() != null ? pedido.getOperador().getNome() : null,
                pedido.getEntregador() != null ? pedido.getEntregador().getId() : null,
                pedido.getEntregador() != null ? pedido.getEntregador().getNome() : null,
                pedido.getNumeroMesa(),
                pedido.getObsPedido(),
                pedido.getItens().stream().map(ItemPedidoResponse::from).toList(),
                pedido.getPagamentos().stream().map(PagamentoResponse::from).toList()
        );
    }
}