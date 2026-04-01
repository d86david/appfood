package com.dsys.appfood.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Captura exceções lançadas pelos Service e Controller
 * e traduz para respostas HTTP com formato padronizado
 * 
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * Intercepta exceções de TODOS os Controllers automaticamente.
 * 
 */
@RestControllerAdvice
public class GlobalExcepetionHandler {

	// --- Entidade não encontrada -> HTTP 404 ---
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErroResponse handlerNaoEncontrado(EntidadeNaoEncontradaException ex) {
		return new ErroResponse("NÃO_ENCONTRADO", ex.getMessage());
	
	// ↑ Um único handler captura: EntregadorNaoEncontrado, ClienteNaoEncontrado, ProdutoNaoEncontrado — todos!
	}
	
	
	// --- Regra de negócio violada -> HTTP 422 --- 
	@ExceptionHandler(NegocioException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErroResponse handlerNegocio(NegocioException ex) {
		return new ErroResponse("REGRA_NEGOCIO", ex.getMessage());
	}
	
	// --- Argumento Inválido -> HTTP 400 --- 
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErroResponse handleArgumento(IllegalArgumentException ex) {
		return new ErroResponse("ARGUMENTO_INVALIDO", ex.getMessage());
	}
	
	// --- Argumento Inválido -> HTTP 400 --- 
		@ExceptionHandler(MethodArgumentNotValidException.class)
		@ResponseStatus(HttpStatus.BAD_REQUEST)
		public ErroResponse handleValidacao(MethodArgumentNotValidException ex) {
			
			//Pega todos os erros de todos os campos
			String mensagem = ex.getBindingResult()
					.getFieldErrors()
					.stream()
					.map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
					.collect(Collectors.joining(", "));
			
			return new ErroResponse("VALIDAÇÃO", mensagem);
		}
	
	// --- Estado inválido → HTTP 409 Conflict ---
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse handleEstado(IllegalStateException ex) {
        return new ErroResponse("CONFLITO", ex.getMessage());
    }

    // --- Qualquer outra exceção não tratada → HTTP 500 ---
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResponse handleGenerico(Exception ex) {
        return new ErroResponse("ERRO_INTERNO", "Ocorreu um erro inesperado.");
        // Não exponha ex.getMessage() aqui — pode vazar detalhes internos
    }
	
	
}
