package com.gabrielferreira.br.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
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
import com.gabrielferreira.br.modelo.Cliente;
import com.gabrielferreira.br.modelo.dto.criar.CriarClienteDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.ClienteDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarClienteDTO;
import com.gabrielferreira.br.service.ClienteService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/clientes")
@Api("Cliente API")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;
	
	@PostMapping
	@ApiOperation("Inserir um cliente")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Inseriu o cliente com sucesso"),
			@ApiResponse(code = 400,message = "Ocorreu um erro personalizado"),
			@ApiResponse(code = 401,message = "Não autorizado para inserir o cliente"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o cliente"),
	})
	public ResponseEntity<CriarClienteDTO> criarCliente(@Valid @RequestBody CriarClienteDTO criarClienteDTO){
		Cliente cliente = clienteService.inserirCliente(criarClienteDTO);
		CriarClienteDTO clienteDTO = new CriarClienteDTO(cliente);
		return new ResponseEntity<>(clienteDTO,HttpStatus.CREATED);
	}
	
	@PutMapping("/{idCliente}")
	@ApiOperation("Atualizar um cliente informando o ID")
	@ApiResponses(value = {
			@ApiResponse(code = 201,message = "Atualizou o cliente com sucesso"),
			@ApiResponse(code = 401,message = "Não autorizado para atualizar o cliente"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o cliente"),
	})
	public ResponseEntity<CriarClienteDTO> atualizarCliente(@PathVariable Long idCliente, @RequestBody @Valid CriarClienteDTO clienteDto){
		Cliente cliente = clienteService.getDetalhe(idCliente);
		clienteDto.setId(cliente.getId());
		cliente = clienteService.inserirCliente(clienteDto);
		CriarClienteDTO criarClientrDto = new CriarClienteDTO(cliente);
		return new ResponseEntity<>(criarClientrDto,HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/{idCliente}")
	@ApiOperation("Deletar um cliente por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200,message = "Retornou os valores com sucesso"),
			@ApiResponse(code = 204,message = "Cliente deletado com sucesso"),
			@ApiResponse(code = 401,message = "Não autorizado para deletar o cliente"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o cliente"),
	})
	public ResponseEntity<Void> deletarCliente(@PathVariable Long idCliente){
		Cliente cliente = clienteService.getDetalhe(idCliente);
		clienteService.deletar(cliente.getId());
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{idCliente}")
	@ApiOperation("Obtém informação de um cliente por ID")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar o cliente"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o cliente"),
	})
	public ResponseEntity<ClienteDTO> obterInformacaoCliente(@PathVariable Long idCliente){
		Cliente cliente = clienteService.getDetalhe(idCliente);
		ClienteDTO clienteDto = new ClienteDTO(cliente);
		return new ResponseEntity<>(clienteDto,HttpStatus.OK);
	}
	
	@GetMapping
	@ApiOperation("Paginação da listagem de clientes")
	@ApiResponses(value = {
			@ApiResponse(code = 401,message = "Não autorizado para consultar os clientes"),
			@ApiResponse(code = 403,message = "Não tem acesso a esse end-point"),
			@ApiResponse(code = 404,message = "Não foi encontrado o cliente"),
	})
	public ResponseEntity<PagedListHolder<ClienteDTO>> mostrarTodosClientesPaginado(
			@RequestParam(defaultValue = "0", value = "pagina") int pagina,
			@RequestParam(defaultValue = "5", value = "totalRegistro") int totalRegistro,
			@RequestParam(required = false) String nomeCompleto,
			@RequestParam(required = false) String documento,
			@RequestParam(required = false) Boolean possuiLivro,
			@RequestParam(required = false) Integer tipoDocumentoCodigo
			){
		
		ProcurarClienteDTO procurarClienteDTO = new ProcurarClienteDTO(nomeCompleto, documento, possuiLivro, tipoDocumentoCodigo);
		List<ClienteDTO> clienteDTOs = clienteService.clientesFiltros(procurarClienteDTO);
		
		PagedListHolder<ClienteDTO> pagedListHolder = new PagedListHolder<ClienteDTO>(clienteDTOs);
		pagedListHolder.setPage(pagina);
		pagedListHolder.setPageSize(totalRegistro);
		
		return new ResponseEntity<>(pagedListHolder,HttpStatus.OK);
	}
}
