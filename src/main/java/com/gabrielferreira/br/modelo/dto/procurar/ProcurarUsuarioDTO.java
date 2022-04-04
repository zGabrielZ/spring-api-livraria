package com.gabrielferreira.br.modelo.dto.procurar;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ProcurarUsuarioDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String autor;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimentoInicio;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimentoFinal;
	
	public LocalDate setParaLocalDate(Date dataNascimento) {
		LocalDate localDate = dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate;
	}
	
	public Date setParaDate(LocalDate dataNascimento) {
		Date date = Date.from(dataNascimento.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return date;
	}

}
