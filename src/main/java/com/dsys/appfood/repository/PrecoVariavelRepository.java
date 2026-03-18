package com.dsys.appfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsys.appfood.domain.model.PrecoVariavel;

@Repository
public interface PrecoVariavelRepository extends JpaRepository<PrecoVariavel, Integer>{
	
	

}
