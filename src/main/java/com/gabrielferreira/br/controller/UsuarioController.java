package com.gabrielferreira.br.controller;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielferreira.br.exception.ErroValidacaoException;
import com.gabrielferreira.br.modelo.Usuario;
import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.UsuarioDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarUsuarioDTO;
import com.gabrielferreira.br.service.UsuarioService;
import com.gabrielferreira.br.validacao.CriarUsuarioValidacao;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/usuarios")
@Api("Usuário API")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping
	@ApiOperation("Inserir um usuário")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Inseriu o usuário com sucesso"),
			@ApiResponse(code = 400,message = "Ocorreu um erro personalizado"),
			@ApiResponse(code = 401,message = "Não autorizado para inserir o usuário"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<CriarUsuarioDTO> criarUsuario(@RequestBody CriarUsuarioDTO usuarioDto){
		List<String> verificaCampos = CriarUsuarioValidacao.getVerificacaoErrosCriarUsuario(usuarioDto);
		verificarCamposUsuario(verificaCampos);
		Usuario usuario = usuarioService.inserir(usuarioDto);
		CriarUsuarioDTO criarUsuarioDTO = new CriarUsuarioDTO(usuario);
		return new ResponseEntity<>(criarUsuarioDTO,HttpStatus.CREATED);
	}
	
	@GetMapping("/{idUsuario}")
	@ApiOperation("Obtém informação de um usuário por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar o usuário"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<UsuarioDTO> obterInformacaoUsuario(@PathVariable Long idUsuario){
		Usuario usuario = usuarioService.getDetalhe(idUsuario);
		UsuarioDTO usuarioDto = new UsuarioDTO(usuario);
		return new ResponseEntity<>(usuarioDto,HttpStatus.OK);
	}
	
	@GetMapping
	@ApiOperation("Lista de usuários")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar os usuários"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<List<UsuarioDTO>> listaDeUsuarios(){
		List<UsuarioDTO> usuarios = usuarioService.mostrarUsuarios();
		return new ResponseEntity<>(usuarios,HttpStatus.OK);
	}
	
	@GetMapping("/filtro")
	@ApiOperation("Paginação da listagem de usuários")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar os usuários"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<PagedListHolder<UsuarioDTO>> listaDeUsuariosFiltro(
			@RequestParam(required = false) String autor,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dataNascimentoInicio,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dataNascimentoFinal,
			@RequestParam(defaultValue = "0", value = "pagina") int pagina,
			@RequestParam(defaultValue = "5", value = "totalRegistro") int totalRegistro){
		ProcurarUsuarioDTO procurarUsuarioDTO = new ProcurarUsuarioDTO(autor, dataNascimentoInicio,dataNascimentoFinal);
		List<UsuarioDTO> usuarioDTOs = usuarioService.filtroUsuarios(procurarUsuarioDTO);
		
		PagedListHolder<UsuarioDTO> paginacao = new PagedListHolder<>(usuarioDTOs);
		paginacao.setPage(pagina);
		paginacao.setPageSize(totalRegistro);
		
		return new ResponseEntity<>(paginacao,HttpStatus.OK);
	}
	
	@PutMapping("/{idUsuario}")
	@ApiOperation("Atualizar um usuário informando o ID")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Atualizou o usuário com sucesso"),
			@ApiResponse(code = 401,message = "Não autorizado para atualizar o usuário"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<CriarUsuarioDTO> atualizarUsuario(@PathVariable Long idUsuario, @RequestBody CriarUsuarioDTO usuarioDto){
		Usuario usuario = usuarioService.getDetalhe(idUsuario);
		
		List<String> verificaCampos = CriarUsuarioValidacao.getVerificacaoErrosCriarUsuario(usuarioDto);
		verificarCamposUsuario(verificaCampos);
		
		usuarioDto.setId(usuario.getId());
		usuario = usuarioService.inserir(usuarioDto);
		
		CriarUsuarioDTO criarUsuarioDTO = new CriarUsuarioDTO(usuario);
		return new ResponseEntity<>(criarUsuarioDTO,HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("{idUsuario}")
	@ApiOperation("Deletar um usuário por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200,message = "Retornou os valores com sucesso"),
			@ApiResponse(code = 204,message = "Usuário deletado com sucesso"),
			@ApiResponse(code = 400,message = "Ocorreu um erro personalizado"),
			@ApiResponse(code = 401,message = "Não autorizado para deletar o usuário"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o usuário"),
	})
	public ResponseEntity<Void> deletarUsuario(@PathVariable Long idUsuario){
		Usuario usuario = usuarioService.getDetalhe(idUsuario);
		usuarioService.deletar(usuario.getId());
		return ResponseEntity.noContent().build();
	}
	
	private void verificarCamposUsuario(List<String> verificarCampos) {
		if(!verificarCampos.isEmpty()) {
			throw new ErroValidacaoException(verificarCampos.toString());
		}
	}
}
