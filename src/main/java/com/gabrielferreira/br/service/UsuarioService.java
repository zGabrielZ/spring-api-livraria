package com.gabrielferreira.br.service;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.repositorio.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepositorio usuarioRepositorio;
	
	@Transactional
	public Usuario inserir(CriarUsuarioDTO criarUsuarioDTO) {
		Usuario usuario = new Usuario(criarUsuarioDTO.getId(), criarUsuarioDTO.getAutor(), criarUsuarioDTO.getDataNascimento(), null);
		verificarAutorExistente(usuario.getAutor());
		return usuarioRepositorio.save(usuario);
	}
	
	public Usuario getUsuario(Long id) {
		Optional<Usuario> optionalUsuario = usuarioRepositorio.findById(id);
		if(!optionalUsuario.isPresent()) {
			throw new EntidadeNotFoundException("Usuário não encontrado.");
		}
		return optionalUsuario.get();
 	}
	
	public void verificarAutorExistente(String autor) {
		Boolean existeAutor = usuarioRepositorio.existsByAutor(autor);
		if(existeAutor) {
			throw new RegraDeNegocioException("Este autor já foi cadastrado.");
		}
	}
}
