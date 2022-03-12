package com.gabrielferreira.br.modelo.dto.criar;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

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
public class CriarLivroDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@NotBlank(message = "Título não pode ser vazio")
	@Size(min=5, max = 150, message="O campo título deve ter no mínimo 5 até 150 caracteres")
	private String titulo;
	
	@NotNull(message = "Usuário não pode ser vazio")
	private Long idUsuario;
	
	@NotBlank(message = "ISBN não pode ser vazio")
	private String isbn;
	
	@NotBlank(message = "Subtítulo não pode ser vazio")
	@Size(min=5, max = 250, message="O campo subtítulo deve ter no mínimo 5 até 250 caracteres")
	private String subtitulo;
	
	@NotBlank(message = "Sinopse não pode ser vazio")
	@Size(min=5, max = 250, message="O campo sinopse deve ter no mínimo 5 até 250 caracteres")
	private String sinopse;
	
	public CriarLivroDTO(Livro livro) {
		id = livro.getId();
		titulo = livro.getTitulo();
		idUsuario = livro.getUsuario().getId();
		isbn = livro.getIsbn();
		subtitulo = livro.getSubtitulo();
		sinopse = livro.getSinopse();
	}

}
