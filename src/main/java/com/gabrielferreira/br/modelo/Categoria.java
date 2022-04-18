package com.gabrielferreira.br.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_CATEGORIA")
@Getter
@Setter
@Builder
@ToString(exclude = {"livros"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Categoria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private String descricao;
	
	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "categoria")
	private List<Livro> livros = new ArrayList<Livro>();
	
	public Categoria(Long id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

}
