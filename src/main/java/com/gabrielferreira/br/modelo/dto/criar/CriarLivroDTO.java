package com.gabrielferreira.br.modelo.dto.criar;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.gabrielferreira.br.modelo.Livro;

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
public class CriarLivroDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "Código do livro",hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "Título do livro",example = "Aventuras da Turma X")
	@NotBlank(message = "Título não pode ser vazio")
	@Size(min=5, max = 150, message="O campo título deve ter no mínimo 5 até 150 caracteres")
	private String titulo;
	
	@ApiModelProperty(value = "ID do usuário",example = "1")
	@NotNull(message = "Usuário não pode ser vazio")
	private Long idUsuario;
	
	@ApiModelProperty(value = "ID da Categoria",example = "1")
	@NotNull(message = "Categoria não pode ser vazia")
	private Long idCategoria;
	
	@ApiModelProperty(value = "ISBN do Livro",example = "123456")
	@NotBlank(message = "ISBN não pode ser vazio")
	private String isbn;
	
	@ApiModelProperty(value = "Subtitulo do Livro",example = "Aventuras da turma da bagunça")
	@NotBlank(message = "Subtítulo não pode ser vazio")
	@Size(min=5, max = 250, message="O campo subtítulo deve ter no mínimo 5 até 250 caracteres")
	private String subtitulo;
	
	@ApiModelProperty(value = "Sinopse do Livro",example = "Era uma vez...")
	@NotBlank(message = "Sinopse não pode ser vazio")
	@Size(min=5, max = 250, message="O campo sinopse deve ter no mínimo 5 até 250 caracteres")
	private String sinopse;
	
	@ApiModelProperty(value = "Estoque do Livro",example = "100")
	@NotNull(message = "Estoque do livro não pode ser vazio")
	private Integer estoque;
	
	public CriarLivroDTO(Livro livro) {
		id = livro.getId();
		titulo = livro.getTitulo();
		idUsuario = livro.getUsuario().getId();
		idCategoria = livro.getCategoria().getId();
		isbn = livro.getIsbn();
		subtitulo = livro.getSubtitulo();
		sinopse = livro.getSinopse();
		estoque = livro.getEstoque();
	}

}
