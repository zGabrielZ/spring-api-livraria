package com.gabrielferreira.br.modelo.enums;

import lombok.Getter;
import lombok.Setter;

public enum TipoDocumento {

	CPF(1,"CPF"),
	CNPJ(2,"CNPJ");
	
	@Getter
	@Setter
	private Integer codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	private TipoDocumento(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	
}
