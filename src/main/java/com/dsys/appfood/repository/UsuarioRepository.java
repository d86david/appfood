package com.dsys.appfood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.enums.TipoUsuario;
import com.dsys.appfood.domain.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	// PESQUISA USUÁRIO PELO LOGIN APENAS OS QUE ESTÃO ATIVOS
	Optional<Usuario> findByLoginAndAtivoTrue(String login);

	// PESQUISA EXISTENCIA E SE ESTÁ ATIVO AO MESMO TEMPO
	boolean existsByIdAndAtivoTrue(Integer id);

	// PESQUISA USUARIOS ATIVOS
	List<Usuario> findByAtivoTrue();

	// PESQUISA USUARIO INATIVOS
	List<Usuario> findByAtivoFalse();

	// PESQUISA TODOS USUARIOS (PARA RELATÓRIOS ADMIN)
	List<Usuario> findAll();

	// LISTAR POR TIPO E ATIVO (PARA SELECIONAR GERENTES DISPONIVEIS)
	List<Usuario> findByTipoAndAtivoTrue(TipoUsuario tipo);

}
