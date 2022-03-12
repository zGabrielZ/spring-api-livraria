package com.gabrielferreira.br.modelo.dto.mostrar;

import java.io.Serializable;

import com.gabrielferreira.br.modelo.Livro;

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
public class LivroDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long id;
	private String titulo;
	private String subtitulo;
	private String sinopse;
	private String isbn;
	private UsuarioDTO usuarioDto;
	
	public LivroDTO(Livro livro) {
		id = livro.getId();
		titulo = livro.getTitulo();
		subtitulo = livro.getSubtitulo();
		sinopse = livro.getSinopse();
		isbn = livro.getIsbn();
		usuarioDto = new UsuarioDTO(livro.getUsuario());
	}

}