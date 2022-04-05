package com.gabrielferreira.br.modelo.dto.criar;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabrielferreira.br.modelo.Usuario;

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
public class CriarUsuarioDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Código do autor",hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "Nome do autor",example = "Gabriel Ferreira")
	private String autor;
	
	@ApiModelProperty(value = "Data de nascimento do autor",example = "DD/MM/AAAA")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimento;
	
	public CriarUsuarioDTO(Usuario usuario) {
		id = usuario.getId();
		autor = usuario.getAutor();
		dataNascimento = usuario.getDataNascimento();
	}
}
