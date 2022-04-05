package com.gabrielferreira.br.modelo.dto.mostrar;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "Código do autor",example = "1")
	private Long id;
	
	@ApiModelProperty(value = "Nome do autor",example = "Gabriel Ferreira")
	private String autor;
	
	@ApiModelProperty(value = "Data de nascimento do autor",example = "26/12/1997")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimento;
	
	public UsuarioDTO(Usuario usuario) {
		id = usuario.getId();
		autor = usuario.getAutor();
		dataNascimento = usuario.getDataNascimento();
	}

}
