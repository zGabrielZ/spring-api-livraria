package com.gabrielferreira.br.modelo.dto.mostrar;

import java.io.Serializable;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabrielferreira.br.modelo.Cliente;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Código do cliente",hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "Nome completo do cliente",example = "Gabriel Ferreira")
	private String nomeCompleto;
	
	@ApiModelProperty(value = "Documento do cliente (CPF ou CNPJ)",example = "17778590000")
	private String documento;
	
	@ApiModelProperty(value = "Data nascimento do cliente",example = "26/12/1997")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataNascimento;
	
	@ApiModelProperty(value = "Possui livro o cliente",example = "true")
	private Boolean possuiLivro;
	
	@ApiModelProperty(value = "Tipo documento do cliente",example = "1")
	private Integer tipoDocumentoCodigo;
	
	public ClienteDTO(Cliente cliente) {
		id = cliente.getId();
		nomeCompleto = cliente.getNomeCompleto();
		documento = cliente.getDocumento();
		dataNascimento = cliente.getDataNascimento();
		possuiLivro = cliente.getPossuiLivro();
		tipoDocumentoCodigo = cliente.getTipoDocumento().getCodigo();
	}
}
