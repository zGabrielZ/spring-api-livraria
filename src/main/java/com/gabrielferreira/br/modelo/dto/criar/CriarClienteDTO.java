package com.gabrielferreira.br.modelo.dto.criar;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

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
public class CriarClienteDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Código do cliente",hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "Nome completo do cliente",example = "Gabriel Ferreira")
	@NotBlank(message = "Nome completo não pode ser vazio")
	@Size(min=5, max = 250, message="O campo nome deve ter no mínimo 5 até 250 caracteres")
	private String nomeCompleto;
	
	@ApiModelProperty(value = "Documento do cliente (CPF ou CNPJ)",example = "17778590000")
	@NotBlank(message = "Documento não pode ser vazio")
	private String documento;
	
	@ApiModelProperty(value = "Data nascimento do cliente",example = "dd/mm/aaaa")
	@NotNull(message = "Data de nascimento não pode ser vazio")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataNascimento;
	
	@ApiModelProperty(value = "Possui livro o cliente",example = "true")
	private Boolean possuiLivro;
	
	@ApiModelProperty(value = "Tipo documento do cliente",example = "1")
	@NotNull(message = "Tipo documento não pode ser vazio")
	private Integer tipoDocumentoCodigo;
	
	public CriarClienteDTO(Cliente cliente) {
		id = cliente.getId();
		nomeCompleto = cliente.getNomeCompleto();
		documento = cliente.getDocumento();
		dataNascimento = cliente.getDataNascimento();
		possuiLivro = cliente.getPossuiLivro();
		tipoDocumentoCodigo = cliente.getTipoDocumento().getCodigo();
	}
}
