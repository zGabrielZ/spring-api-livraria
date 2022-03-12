package com.gabrielferreira.br.modelo.dto.mostrar;

import java.io.Serializable;
import java.util.Date;

import com.gabrielferreira.br.modelo.Usuario;

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
	
	private Long id;
	private String autor;
	private Date dataNascimento;
	
	public UsuarioDTO(Usuario usuario) {
		id = usuario.getId();
		autor = usuario.getAutor();
		dataNascimento = usuario.getDataNascimento();
	}

}
