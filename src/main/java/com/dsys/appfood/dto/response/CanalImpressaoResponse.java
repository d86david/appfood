package com.dsys.appfood.dto.response;

import com.dsys.appfood.domain.model.CanalImpressao;

public record CanalImpressaoResponse(
    Integer id,
    String nome,
    String descricao,
    Boolean ativo
) {
    public static CanalImpressaoResponse from(CanalImpressao canal) {
        return new CanalImpressaoResponse(
            canal.getId(),
            canal.getNome(),
            canal.getDescricao(),
            canal.isAtivo()
        );
    }
}