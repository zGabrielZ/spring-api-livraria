package com.gabrielferreira.br.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.repositorio.CategoriaRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;

@Service
public class CategoriaService extends AbstractService<Categoria>{

	private final CategoriaRepositorio categoriaRepositorio;
	
	public CategoriaService(JpaRepository<Categoria, Long> jpaRepository) {
		super(jpaRepository);
		this.categoriaRepositorio = (CategoriaRepositorio) jpaRepository;
	}

}
