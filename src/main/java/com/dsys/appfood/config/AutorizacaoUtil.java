package com.dsys.appfood.config;

import com.dsys.appfood.domain.model.Usuario;
import com.dsys.appfood.exception.AcessoNegadoException;


/**
 * ====================================================================== 
 * CLASSE UTILITÁRIA DE AUTORIZAÇÃO
 * ======================================================================
 * 
 * CONCEITO: Utility Class (Classe Utilitária) - Contém apenas métodos estáticos
 * (não precisa instanciar) - Não tem estado (não guarda dados) - Pode ser usada
 * por qualquer outra classe que precise verificar se a movimentação requer
 * autorização
 * 
 */
public final class AutorizacaoUtil {

	// Construtor privado impede instanciação
	private AutorizacaoUtil() {
		throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
	}

	/**
	 * Valida se uma operação exige autorização do Gerente.
	 */
	public static void exigirAutorizacaoGerente(Usuario gerente) {
		if (gerente == null) {
			throw new IllegalArgumentException("Essa operação exige autorização de um gerente!");
		}
	}

	public static void exigirPapelGerente(Usuario gerente) {

		exigirAutorizacaoGerente(gerente);

		if (!gerente.isGerente()) {
			throw new AcessoNegadoException("Esta operação exige autorização de um gerente.");
		}
	}
}
