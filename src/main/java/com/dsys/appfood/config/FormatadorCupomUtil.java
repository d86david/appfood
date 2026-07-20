package com.dsys.appfood.config;

import java.math.BigDecimal;

/**
 * ======================================================================
 *  CLASSE UTILITÁRIA DE FORMATAÇÃO DE CUPONS
 * ======================================================================
 * 
 * CONCEITO: Utility Class (Classe Utilitária)
 * - Contém apenas métodos estáticos (não precisa instanciar)
 * - Não tem estado (não guarda dados)
 * - Pode ser usada por qualquer Service que precise formatar texto
 * 
 * VANTAGENS:
 * - Reutilização: Cozinha e Balcão usam os mesmos métodos
 * - Testabilidade: Fácil criar testes unitários para esses métodos
 * - Manutenção: Se mudar a lógica de formatação, muda em um lugar só
 * 
 */
public final class FormatadorCupomUtil {
	
	// Construtor privado impede instanciação 
	private FormatadorCupomUtil() {
		throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
	}
	
	/**
     * Quebra um texto longo em múltiplas linhas, respeitando a largura máxima.
     * 
     * CONCEITO: Word Wrap (Quebra de Palavra)
     * - Divide o texto em palavras (separadas por espaço)
     * - Monta linhas que nunca ultrapassem a largura máxima
     * - Preserva palavras inteiras (não corta no meio)
     * 
     * @param texto        O texto completo a ser formatado
     * @param larguraMaxima A largura máxima da linha (ex: 48 ou 56)
     * @return O texto formatado com quebras de linha
     */
	public static String quebrarLinha(String texto, int larguraMaxima) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        
        String[] palavras = texto.split(" ");
        StringBuilder resultado = new StringBuilder();
        StringBuilder linhaAtual = new StringBuilder();
        
        for (String palavra : palavras) {
            int espacoNecessario = linhaAtual.length() == 0 
                ? palavra.length() 
                : linhaAtual.length() + 1 + palavra.length();
            
            if (espacoNecessario <= larguraMaxima) {
                if (linhaAtual.length() > 0) {
                    linhaAtual.append(" ");
                }
                linhaAtual.append(palavra);
            } else {
                if (linhaAtual.length() > 0) {
                    resultado.append(linhaAtual).append("\n");
                    linhaAtual.setLength(0);
                }
                
                if (palavra.length() > larguraMaxima) {
                    for (int i = 0; i < palavra.length(); i += larguraMaxima) {
                        int fim = Math.min(i + larguraMaxima, palavra.length());
                        resultado.append(palavra, i, fim).append("\n");
                    }
                } else {
                    linhaAtual.append(palavra);
                }
            }
        }
        
        if (linhaAtual.length() > 0) {
            resultado.append(linhaAtual);
        }
        
        return resultado.toString();
    }
	
	
	/**
     * Formata uma linha com texto à esquerda e valor à direita.
     * 
     * CONCEITO: Alinhamento Dinâmico
     * - Calcula quantos espaços colocar entre o texto e o valor
     * - Garante que o valor SEMPRE termine na coluna especificada
     * 
     * @param texto       O texto à esquerda (ex: "+ BACON")
     * @param valor       O valor monetário à direita
     * @param larguraTotal A largura total da linha (ex: 48)
     * @return A linha formatada
     */
    public static String formatarLinhaComValor(String texto, BigDecimal valor, int larguraTotal) {
        String valorFormatado = String.format("R$%6.2f", valor);
        int espacosNecessarios = larguraTotal - texto.length() - valorFormatado.length();
        
        if (espacosNecessarios < 1) {
            espacosNecessarios = 1;
        }
        
        return texto + " ".repeat(espacosNecessarios) + valorFormatado + "\n";
    }
    
    
    /**
     * Formata uma linha com texto à esquerda sem o valor do produto.
     * 
     * CONCEITO: Alinhamento Dinâmico
     * - Calcula quantos espaços colocar entre o texto e o valor
     * - Garante que o valor SEMPRE termine na coluna especificada
     * 
     * @param texto       O texto à esquerda (ex: "+ BACON")
     * @param larguraTotal A largura total da linha (ex: 48)
     * @return A linha formatada
     */
    public static String formatarLinhaSemValor(String texto, int larguraTotal) {
        
        int espacosNecessarios = larguraTotal - texto.length();
        
        if (espacosNecessarios < 1) {
            espacosNecessarios = 1;
        }
        
        return texto + " ".repeat(espacosNecessarios) + "\n";
    }

    /**
     * Centraliza um texto dentro de uma largura específica.
     * 
     * @param texto   O texto a ser centralizado
     * @param largura A largura total
     * @return O texto centralizado
     */
    public static String centralizar(String texto, int largura) {
        if (texto.length() >= largura) {
            return texto;
        }
        
        int espacosTotais = largura - texto.length();
        int espacosEsquerda = espacosTotais / 2;
        int espacosDireita = espacosTotais - espacosEsquerda;
        
        return " ".repeat(espacosEsquerda) + texto + " ".repeat(espacosDireita);
    }
    
    /**
     * Repete um caractere N vezes (útil para criar linhas separadoras).
     * 
     * @param caractere O caractere a ser repetido (ex: '-', '=')
     * @param vezes     Quantas vezes repetir
     * @return A string repetida
     */
    public static String repetir(char caractere, int vezes) {
        return String.valueOf(caractere).repeat(vezes);
    }
}
