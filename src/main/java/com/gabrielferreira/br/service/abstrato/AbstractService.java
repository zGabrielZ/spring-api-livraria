package com.gabrielferreira.br.service.abstrato;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractService<T> {
	
	private final JpaRepository<T, Long> jpaRepository;
	
	@Transactional
	public void deletar(Long id, String mensagem) {
		if (id == null) {
			throw new IllegalArgumentException("Para deletar o " + mensagem + " é preciso informar o id.");
		}
		jpaRepository.deleteById(id);
	}
	
	public T getDetalhe(Long id, String mensagem) {
		Optional<T> optionalEntidade = jpaRepository.findById(id);
		if(!optionalEntidade.isPresent()) {
			throw new EntidadeNotFoundException(mensagem+" não encontrado.");
		}
		return optionalEntidade.get();
	}
	
	public List<T> getLista(){
		return jpaRepository.findAll();
	}
	

}
