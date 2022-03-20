package com.gabrielferreira.br.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielferreira.br.exception.ErroValidacaoException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.service.UsuarioService;
import com.gabrielferreira.br.validacao.CriarUsuarioValidacao;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private static String USUARIO_MSG = "Usuário";
	
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
	
	@GetMapping("/{idUsuario}")
	public ResponseEntity<UsuarioDTO> obterInformacaoUsuario(@PathVariable Long idUsuario){
		Usuario usuario = usuarioService.getDetalhe(idUsuario,USUARIO_MSG);
		UsuarioDTO usuarioDto = new UsuarioDTO(usuario);
		return new ResponseEntity<>(usuarioDto,HttpStatus.OK);
	}
	
	@PutMapping("/{idUsuario}")
	public ResponseEntity<CriarUsuarioDTO> atualizarUsuario(@PathVariable Long idUsuario, @RequestBody CriarUsuarioDTO usuarioDto){
		List<String> verificaCampos = CriarUsuarioValidacao.getVerificacaoErrosCriarUsuario(usuarioDto);
		verificarCamposUsuario(verificaCampos);
		Usuario usuario = usuarioService.getDetalhe(idUsuario,USUARIO_MSG);
		usuario = usuarioService.inserir(usuarioDto);
		CriarUsuarioDTO criarUsuarioDTO = new CriarUsuarioDTO(usuario);
		return new ResponseEntity<>(criarUsuarioDTO,HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("{idUsuario}")
	public ResponseEntity<Void> deletarUsuario(@PathVariable Long idUsuario){
		Usuario usuario = usuarioService.getDetalhe(idUsuario,USUARIO_MSG);
		usuarioService.deletar(usuario.getId(),USUARIO_MSG);
		return ResponseEntity.noContent().build();
	}
	
	private void verificarCamposUsuario(List<String> verificarCampos) {
		if(!verificarCampos.isEmpty()) {
			throw new ErroValidacaoException(verificarCampos.toString());
		}
	}
}
