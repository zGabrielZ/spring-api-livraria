package com.gabrielferreira.br.modelo.dto.procurar;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabrielferreira.br.modelo.enums.TipoDocumento;

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
public class ProcurarClienteDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nomeCompleto;
	private String documento;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimentoInicio;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimentoFinal;
	
	private Boolean possuiLivro;
	private TipoDocumento tipoDocumento;

}
