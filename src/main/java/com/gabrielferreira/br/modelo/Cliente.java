package com.gabrielferreira.br.modelo;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gabrielferreira.br.modelo.enums.TipoDocumento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_CLIENTE")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome_completo",nullable = false)
	private String nomeCompleto;
	
	@Column(name = "documento",nullable = false)
	private String documento;
	
	@Column(name = "data_nascimento",nullable = false)
	private LocalDate dataNascimento;
	
	@Column(name = "possui_livro")
	private Boolean possuiLivro;
	
	@Enumerated(EnumType.STRING)
	private TipoDocumento tipoDocumento;

}
