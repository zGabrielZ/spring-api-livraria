package com.gabrielferreira.br.service;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.modelo.Categoria;
import com.gabrielferreira.br.modelo.dto.criar.CriarCategoriaDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.CategoriaDTO;
import com.gabrielferreira.br.repositorio.CategoriaRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;
import com.gabrielferreira.br.utils.ValidacaoFormatacao;

@Service
public class CategoriaService extends AbstractService<Categoria>{

	private final CategoriaRepositorio categoriaRepositorio;
	
	public CategoriaService(JpaRepository<Categoria, Long> jpaRepository) {
		super(jpaRepository);
		this.categoriaRepositorio = (CategoriaRepositorio) jpaRepository;
	}
	
	@Transactional
	public Categoria inserirCategoria(CriarCategoriaDTO criarCategoriaDTO) {
		Categoria categoria = new Categoria(criarCategoriaDTO.getId(), ValidacaoFormatacao.getFormatacaoNome(criarCategoriaDTO.getDescricao()));
		return categoriaRepositorio.save(categoria);
	}
	
	public List<CategoriaDTO> mostrarCategorias(){
		List<Categoria> categorias = getLista();
		if(categorias.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhuma categoria encontrada.");
		}
		List<CategoriaDTO> categoriaDTOs = categorias.stream().map(c -> new CategoriaDTO(c)).collect(Collectors.toList());
		return categoriaDTOs;
	}

}
