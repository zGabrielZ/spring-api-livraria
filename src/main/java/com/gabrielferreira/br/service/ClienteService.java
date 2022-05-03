package com.gabrielferreira.br.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;
import com.gabrielferreira.br.modelo.Cliente;
import com.gabrielferreira.br.modelo.dto.criar.CriarClienteDTO;
import com.gabrielferreira.br.modelo.dto.mostrar.ClienteDTO;
import com.gabrielferreira.br.modelo.dto.procurar.ProcurarClienteDTO;
import com.gabrielferreira.br.modelo.enums.TipoDocumento;
import com.gabrielferreira.br.repositorio.ClienteRepositorio;
import com.gabrielferreira.br.service.abstrato.AbstractService;

@Service
public class ClienteService extends AbstractService<Cliente>{

	private final ClienteRepositorio clienteRepositorio;
	
	private final EntityManager entityManager;
	
	public ClienteService(JpaRepository<Cliente, Long> jpaRepository, EntityManager entityManager) {
		super(jpaRepository);
		this.clienteRepositorio = (ClienteRepositorio) jpaRepository;
		this.entityManager = entityManager;
	}
	
	@Transactional
	public Cliente inserirCliente(CriarClienteDTO criarClienteDTO) {
		Cliente cliente = new Cliente(criarClienteDTO.getId(), criarClienteDTO.getNomeCompleto()
				, criarClienteDTO.getDocumento(), criarClienteDTO.getDataNascimento(), criarClienteDTO.getPossuiLivro(), 
				getTipoDocumentoEscolhido(criarClienteDTO.getTipoDocumentoCodigo()));
		verificarTipoDocumento(cliente.getTipoDocumento());
		return clienteRepositorio.save(cliente);
	}
	
	public List<ClienteDTO> clientesFiltros(ProcurarClienteDTO procurarClienteDTO){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
		Root<Cliente> root = cq.from(Cliente.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if(procurarClienteDTO.getNomeCompleto() != null || StringUtils.isNotEmpty(procurarClienteDTO.getNomeCompleto())) {
			Predicate predicateNome = cb.like(root.get("nomeCompleto"), "%" + procurarClienteDTO.getNomeCompleto() + "%");
			predicates.add(predicateNome);
		}
		
		if(procurarClienteDTO.getDocumento() != null || StringUtils.isNotEmpty(procurarClienteDTO.getDocumento())) {
			Predicate predicateDocumento = cb.equal(root.get("documento"), procurarClienteDTO.getDocumento());
			predicates.add(predicateDocumento);
		}
		
		if(procurarClienteDTO.getPossuiLivro() != null) {
			Predicate predicatePossuiLivro = cb.equal(root.get("possuiLivro"), procurarClienteDTO.getPossuiLivro());
			predicates.add(predicatePossuiLivro);
		}
		
		if(procurarClienteDTO.getTipoDocumentoCodigo() != null) {
			Predicate predicatePTipoDocumentoCodigo = cb.equal(root.get("tipoDocumento"), procurarClienteDTO.getTipoDocumentoCodigo());
			predicates.add(predicatePTipoDocumentoCodigo);
		}
		
		cq.orderBy(cb.desc(root.get("id")));
		cq.where((Predicate[])predicates.toArray(new Predicate[0]));
		
		TypedQuery<Cliente> typedQuery = entityManager.createQuery(cq);
		List<Cliente> clientes = typedQuery.getResultList();
		
		if(clientes.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum cliente encontrado.");
		}
		
		List<ClienteDTO> clientesDtos = clientes.stream().map(c -> new ClienteDTO(c)).collect(Collectors.toList());
		return clientesDtos;
		
	}
	
	public List<ClienteDTO> mostrarClientes(){
		List<Cliente> clientes = getLista();
		if(clientes.isEmpty()) {
			throw new EntidadeNotFoundException("Nenhum cliente encontrado.");
		}
		List<ClienteDTO> clienteDTOs = clientes.stream().map(c -> new ClienteDTO(c)).collect(Collectors.toList());
		return clienteDTOs;
	}
	
	public TipoDocumento getTipoDocumentoEscolhido(Integer codigo) {
		for(TipoDocumento tipoDocumento : TipoDocumento.values()) {
			if(tipoDocumento.getCodigo().equals(codigo)) {
				return tipoDocumento;
			}
		}
		return null;
	}
	
	private void verificarTipoDocumento(TipoDocumento tipoDocumento) {
		if(tipoDocumento == null) {
			throw new RegraDeNegocioException("Insira o tipo de documento corretamente.");
		}
	}

}
