package com.gabrielferreira.br.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielferreira.br.exception.ErroValidacaoException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.service.UsuarioService;
import com.gabrielferreira.br.validacao.CriarUsuarioValidacao;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<CriarUsuarioDTO> criarUsuario(@RequestBody CriarUsuarioDTO usuarioDto){
		List<String> verificaCampos = CriarUsuarioValidacao.getVerificacaoErrosCriarUsuario(usuarioDto);
		verificarCamposUsuario(verificaCampos);
		Usuario usuario = usuarioService.inserir(usuarioDto);
		CriarUsuarioDTO criarUsuarioDTO = new CriarUsuarioDTO(usuario);
		return new ResponseEntity<>(criarUsuarioDTO,HttpStatus.CREATED);
	}
	
	private void verificarCamposUsuario(List<String> verificarCampos) {
		if(!verificarCampos.isEmpty()) {
			throw new ErroValidacaoException(verificarCampos.toString());
		}
	}
}
