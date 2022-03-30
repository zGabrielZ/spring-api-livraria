package com.gabrielferreira.br.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;

@Service
public class UsuarioService extends AbstractService<Usuario>{
	
	private final UsuarioRepositorio usuarioRepositorio;
	
	public UsuarioService(JpaRepository<Usuario, Long> jpaRepository) {
		super(jpaRepository);
		this.usuarioRepositorio = (UsuarioRepositorio) jpaRepository;
	}
	
	@Transactional
	public Usuario inserir(CriarUsuarioDTO criarUsuarioDTO) {
		Usuario usuario = new Usuario(criarUsuarioDTO.getId(), getFormatoNome(criarUsuarioDTO.getAutor()), criarUsuarioDTO.getDataNascimento(), null);
		verificarAutorExistente(usuario);
		return usuarioRepositorio.save(usuario);
	}
	
	public List<UsuarioDTO> mostrarUsuarios(){
		List<Usuario> usuarios = getLista();
		if(usuarios.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum usuário encontrado.");
		}
		List<UsuarioDTO> usuarioDTOs = usuarios.stream().map(u -> new UsuarioDTO(u)).collect(Collectors.toList());
		return usuarioDTOs;
	}
	
	public void verificarAutorExistente(Usuario usuario) {
		if(usuario.getId() == null) {
			
			Boolean existeAutor = usuarioRepositorio.existsByAutor(usuario.getAutor());
			if(existeAutor) {
				throw new RegraDeNegocioException("Este autor já foi cadastrado.");
			}
		
		} else if(usuario.getId() != null) {
			
			Usuario usuarioPesquisado = usuarioRepositorio.buscarAutorUsuarioQuandoForAtualizar(usuario.getAutor(), usuario.getId());
			if(usuarioPesquisado != null) {
				throw new RegraDeNegocioException("Autor já existente ao atualizar.");
			}
			
		}
	}
	
	// Definindo um padrão para o nome, sempre colocando a primeira letra do nome como maiúsculo e o restante como minusculo
	public String getFormatoNome(String nome) {
		
		// Removendo os espaços
		String valorSemEspaco = nome.trim();
		
		// Separando cada nome informado em uma lista
		List<String> nomes = new ArrayList<>(Arrays.asList(valorSemEspaco.split(" ")));
		
		// Criar uma lista que vai inserir o nome corretamente
		List<String> nomesComFormato = new ArrayList<String>();
		
		// Realizando a formatação
		for(String nomeFormato : nomes) {
			if(!nomeFormato.isEmpty()) {
				String primeiraLetra = nomeFormato.substring(0,1).toUpperCase();
				String restante = nomeFormato.substring(1).toLowerCase();
				nomesComFormato.add(primeiraLetra + restante);
			}
		}
		
		// Ajustando a formatação
		String nomeFormatado = nomesComFormato.toString().replace(",","").replace("[", "").replace("]", "");
		return nomeFormatado;
	}
}
