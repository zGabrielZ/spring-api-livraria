package com.gabrielferreira.br.modelo.dto.mostrar;

import java.io.Serializable;
import com.gabrielferreira.br.modelo.Categoria;

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
public class CategoriaDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "Código da categoria",example = "1")
	private Long id;
	
	@ApiModelProperty(value = "Descrição da categoria",example = "Aventura")
	private String descricao;
	
	public CategoriaDTO(Categoria categoria) {
		id = categoria.getId();
		descricao = categoria.getDescricao();
	}

}
