package com.dsys.appfood.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dsys.appfood.domain.enums.FormaPagamento;
import com.dsys.appfood.domain.enums.TipoCustomizacao;
import com.dsys.appfood.domain.model.ItemCustomizacao;
import com.dsys.appfood.domain.model.ItemPedido;
import com.dsys.appfood.domain.model.Pedido;
import com.dsys.appfood.domain.model.SubItemSabor;
import com.dsys.appfood.dto.response.ImpressaoBalcaoResponse;
import com.dsys.appfood.dto.response.ImpressaoCozinhaResponse;
import com.dsys.appfood.dto.response.ImpressaoResponse;

/**
 * Serviço de impressão com roteamento por categoria.
 * 
 * CONCEITO: 
 * - Cada item do pedido é roteado para a impressora configurada na categoria do produto.
 * - A cozinha recebe impressões separadas por categoria (Pizzas, Lanches, etc.)
 * - O balcão recebe uma única impressão com todos os itens e dados do cliente.
 */
@Service
public class ImpressaoService {

    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String LINHA = "----------------------------------------\n";
    private static final String LINHA_DUPLA = "========================================\n";

    private final PedidoService pedidoService;

    public ImpressaoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =============================================================
    // MÉTODO PÚBLICO: IMPRIMIR PEDIDO (COZINHA + BALCÃO)
    // =============================================================

    /**
     * Gera e envia as impressões do pedido:
     * - Cozinha: roteada por categoria (cada categoria vai para sua impressora).
     * - Balcão: uma única via com todos os dados.
     * 
     * @param pedidoId ID do pedido a ser impresso.
     * @return DTO com os conteúdos gerados (para debug/retorno).
     */
    public ImpressaoResponse imprimirPedido(Integer pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        // 1. Imprime a cozinha (roteada por categoria)
        Map<String, String> conteudosCozinha = imprimirCupomCozinha(pedido);

        // 2. Imprime o balcão (via única)
        String conteudoBalcao = imprimirCupomBalcao(pedido);

        return new ImpressaoResponse(conteudosCozinha, conteudoBalcao);
    }
    
    // =============================================================
    // MÉTODO PÚBLICO: IMPRIMIR PEDIDO BALCÃO
    // =============================================================

    /**
     * Gera e envia a impressão do pedido para o balcão: uma única via com todos os dados.
     */
    public ImpressaoBalcaoResponse imprimirPedidoBalcao(Integer pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        // Imprime o balcão (via única)
        String conteudoBalcao = imprimirCupomBalcao(pedido);

        return new ImpressaoBalcaoResponse(conteudoBalcao);
    }
    
    // =============================================================
    // MÉTODO PÚBLICO: IMPRIMIR PEDIDO COZINHA
    // =============================================================

    /**
     * Gera e envia a impressão do pedido para a cozinha: roteada por categoria (cada categoria vai para sua impressora).
     * 
     */
    public ImpressaoCozinhaResponse imprimirPedidoCozinha(Integer pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        // Imprime a cozinha (roteada por categoria)
        Map<String, String> conteudosCozinha = imprimirCupomCozinha(pedido);

        return new ImpressaoCozinhaResponse(conteudosCozinha);
    }

    // =============================================================
    // IMPRESSÃO DA COZINHA (ROTEADA POR CATEGORIA)
    // =============================================================

    /**
     * Gera e envia a impressão do pedido para a cozinha, separando por categoria/impressora.
     * 
     * @param pedido Pedido a ser impresso.
     * @return Mapa com a impressora como chave e o conteúdo como valor (para debug).
     */
    private Map<String, String> imprimirCupomCozinha(Pedido pedido) {

        // 1. Agrupa os itens por impressora (baseado na categoria do produto)
        Map<String, List<SubItemSabor>> itensPorImpressora = agruparItensPorImpressora(pedido);

        // 2. Para cada impressora, gera o conteúdo do cupom e envia
        Map<String, String> conteudos = new LinkedHashMap<>();

        for (Map.Entry<String, List<SubItemSabor>> entry : itensPorImpressora.entrySet()) {
            String impressora = entry.getKey();
            List<SubItemSabor> itens = entry.getValue();

            // Gera o cupom específico para aquela impressora
            String conteudo = gerarCupomCozinha(pedido, impressora, itens);

            // Envia para a impressora
            enviarParaImpressora(conteudo, impressora);

            // Armazena para retorno (debug)
            conteudos.put(impressora, conteudo);
        }

        // 3. Se não houver itens, imprime um cupom vazio
        if (conteudos.isEmpty()) {
            String vazio = "=== PEDIDO #" + pedido.getId() + " - NENHUM ITEM PARA IMPRIMIR ===\n";
            conteudos.put("VAZIO", vazio);
            enviarParaImpressora(vazio, "VAZIO");
        }

        return conteudos;
    }

    // =============================================================
    // IMPRESSÃO DO BALCÃO (VIA ÚNICA)
    // =============================================================

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
    // AGRUPAMENTO POR IMPRESSORA
    // =============================================================

    /**
     * Agrupa os sabores (SubItemSabor) do pedido por impressora.
     * A impressora é definida na Categoria do produto.
     * Se a categoria não tiver impressora definida, usa "GERAL".
     */
    private Map<String, List<SubItemSabor>> agruparItensPorImpressora(Pedido pedido) {
        Map<String, List<SubItemSabor>> resultado = new LinkedHashMap<>();

        for (ItemPedido item : pedido.getItens()) {
            for (SubItemSabor sub : item.getSubItens()) {
                // Obtém o nome da impressora da categoria do produto
                String impressora = sub.getProduto().getCategoria().getImpressora();

                // Se não tiver impressora definida, usa "GERAL"
                if (impressora == null || impressora.isBlank()) {
                    impressora = "GERAL";
                }

                // Adiciona o sabor à lista da impressora
                resultado.computeIfAbsent(impressora, k -> new ArrayList<>()).add(sub);
            }
        }
        return resultado;
    }

    // =============================================================
    // GERADORES DE CONTEÚDO
    // =============================================================

    /**
     * Gera o conteúdo do cupom para a COZINHA (específico para uma impressora).
     */
    private String gerarCupomCozinha(Pedido pedido, String impressora, List<SubItemSabor> itens) {
        StringBuilder sb = new StringBuilder();

        // --- CABEÇALHO ---
        sb.append("   PEDIDO ").append(pedido.getTipo().toString().toUpperCase()).append("\n");
        sb.append(LINHA_DUPLA);
        sb.append("PEDIDO #").append(pedido.getId()).append("\n");
        sb.append("DATA: ").append(pedido.getDtHoraAbertura().format(FORMATTER_DATA)).append("\n");
        sb.append("CLIENTE: ").append(obterNomeCliente(pedido)).append("\n");
        sb.append(LINHA);

        // --- ITENS (agrupados por ItemPedido para manter contexto) ---
        Map<ItemPedido, List<SubItemSabor>> itensPorItem = itens.stream()
                .collect(Collectors.groupingBy(SubItemSabor::getItem));

        for (Map.Entry<ItemPedido, List<SubItemSabor>> entry : itensPorItem.entrySet()) {
            ItemPedido item = entry.getKey();
            List<SubItemSabor> sabores = entry.getValue();

            // Informações do item (tamanho)
            sb.append("TAMANHO: ").append(item.getTamanho().getNome()).append("\n");

            // Cada sabor
            for (SubItemSabor sub : sabores) {
                sb.append("  - ").append(sub.getProduto().getNome()).append("\n");

                // Customizações do sabor (adicionados/removidos)
                for (var c : sub.getCustomizacoes()) {
                    String acao = (c.getTipoCustomizacao() == TipoCustomizacao.ADICIONAL) ? "+ " : "SEM ";
                    sb.append("    ").append(acao)
                      .append(c.getIngrediente().getNome().toUpperCase())
                      .append("\n");
                }
            }

            // Bordas (customizações globais do item) - só uma vez por item
            for (var b : item.getCustomizacoesGlobais()) {
                sb.append("  + Borda: ").append(b.getBorda().getNome()).append("\n");
            }

            sb.append(LINHA);
        }

        // --- RODAPÉ ---
        sb.append("TOTAL: R$ ").append(pedido.calcularTotal()).append("\n");
        sb.append(LINHA_DUPLA);
        sb.append("    *** ENVIAR PARA ").append(impressora.toUpperCase()).append(" ***    \n");
        sb.append(LINHA_DUPLA);

        return sb.toString();
    }

    /**
     * Gera o conteúdo do cupom para o BALCÃO (via única).
     */
    private String gerarCupomBalcao(Pedido pedido) {
        StringBuilder sb = new StringBuilder();

        // --- CABEÇALHO ---
        sb.append("   PEDIDO " + pedido.getTipo().toString().toUpperCase() + "   \n");
        sb.append(LINHA_DUPLA);
        sb.append("PEDIDO #").append(pedido.getId()).append("\n");
        sb.append("DATA: ").append(pedido.getDtHoraAbertura().format(FORMATTER_DATA)).append("\n");
        sb.append(LINHA);

        // --- DADOS DO CLIENTE ---
        sb.append("CLIENTE: ").append(obterNomeCliente(pedido)).append("\n");
        if (pedido.getCliente() != null) {
            var cliente = pedido.getCliente();
            if (cliente.getTelefonePrincipal() != null) {
                sb.append("TELEFONE: ").append(cliente.getTelefonePrincipal()).append("\n");
            }
            if (cliente.getEndereco() != null) {
                var end = cliente.getEndereco();
                sb.append("ENDEREÇO: ").append(end.getLogradouro()).append(", ").append(end.getNumero());
                if (end.getComplemento() != null && !end.getComplemento().isBlank()) {
                    sb.append(" - ").append(end.getComplemento());
                }
                sb.append("\n");
                sb.append("BAIRRO: ").append(end.getBairro()).append("\n");
                sb.append("CIDADE: ").append(end.getCidade()).append("/").append(end.getUf()).append("\n");
            }
        } else {
            sb.append("(CLIENTE NÃO CADASTRADO - BALCÃO)\n");
        }
        sb.append(LINHA);

        // --- PRODUTOS ---
        sb.append("ITEM                QTD  VALOR  TOTAL\n");
        sb.append(LINHA);

        for (ItemPedido item : pedido.getItens()) {
            for (SubItemSabor sub : item.getSubItens()) {
                String nomeProduto = sub.getProduto().getNome();
                String tamanho = item.getTamanho().getNome();
                BigDecimal precoUnitario = sub.getPrecoSabor();
                BigDecimal totalSabor = sub.calculaPrecoSabor();

                String nomeFormato = String.format("%-20s", nomeProduto + " (" + tamanho + ") ");
                sb.append(nomeFormato)
                  .append(" 1  ")
                  .append(String.format("R$%6.2f", precoUnitario))
                  .append(" ")
                  .append(String.format("R$%6.2f", totalSabor))
                  .append("\n");
            }

            // Bordas como itens extras
            for (ItemCustomizacao b : item.getCustomizacoesGlobais()) {
                sb.append("  + Borda: ").append(b.getBorda().getNome())
                  .append(" 1   ")
                  .append(String.format("R$%6.2f", b.getValorCobrado()))
                  .append(" ")
                  .append(String.format("R$%6.2f", b.getValorCobrado()))
                  .append("\n");
            }
        }

        sb.append(LINHA);
        sb.append("SUBTOTAL: R$ ").append(pedido.getValorBruto()).append("\n");
        if (pedido.getDesconto() != null && pedido.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("DESCONTO: -R$ ").append(pedido.getDesconto()).append("\n");
        }
        if (pedido.getTaxaEntrega() != null && pedido.getTaxaEntrega().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("TAXA ENTREGA: R$ ").append(pedido.getTaxaEntrega()).append("\n");
        }
        sb.append(LINHA_DUPLA);
        sb.append("TOTAL: R$ ").append(pedido.calcularTotal()).append("\n");
        sb.append(LINHA_DUPLA);

        // --- FORMA DE PAGAMENTO ---
        sb.append("FORMA DE PAGAMENTO:\n");
        if (pedido.getPagamentos().isEmpty()) {
            sb.append("  (Aguardando pagamento)\n");
        } else {
            for (var pag : pedido.getPagamentos()) {
                sb.append("  ").append(pag.getFormaPagamento())
                  .append(": R$ ").append(pag.getValor())
                  .append("\n");
                if (pag.getFormaPagamento() == FormaPagamento.DINHEIRO) {
                    BigDecimal troco = pedido.calcularTroco();
                    if (troco.compareTo(BigDecimal.ZERO) > 0) {
                        sb.append("    TROCO: R$ ").append(troco).append("\n");
                    }
                }
            }
        }

        sb.append(LINHA_DUPLA);
        sb.append("    *** OBRIGADO PELA PREFERÊNCIA ***    \n");
        sb.append(LINHA_DUPLA);

        return sb.toString();
    }

    // =============================================================
    // MÉTODOS DE ENVIO PARA IMPRESSORA
    // =============================================================

    /**
     * Simula o envio do conteúdo para uma impressora.
     * Em produção, substituir pela integração real.
     */
    private void enviarParaImpressora(String conteudo, String impressora) {
        try {
            String nomeArquivo = "impressao_" + impressora + "_" + System.currentTimeMillis() + ".txt";
            java.nio.file.Path path = java.nio.file.Paths.get("impressoes", nomeArquivo);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.writeString(path, conteudo);
            System.out.println(">>> Impressão enviada para " + impressora + ": " + path.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Erro ao simular impressão (" + impressora + "): " + e.getMessage());
            // Fallback: imprime no console
            System.out.println("=== IMPRESSÃO " + impressora + " ===\n" + conteudo);
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
}