package com.gabrielferreira.br.modelo.dto.criar;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CriarCategoriaDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Código da categoria",hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "Descrição da categoria",example = "Ação")
	@NotBlank(message = "Descrição não pode ser vazio")
	@Size(min=5, max = 150, message="O campo descrição deve ter no mínimo 5 até 150 caracteres")
	private String descricao;
	
	public CriarCategoriaDTO(Categoria categoria) {
		id = categoria.getId();
		descricao = categoria.getDescricao();
	}
}
