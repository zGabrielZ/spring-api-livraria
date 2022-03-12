package com.gabrielferreira.br.exception;

public class ErroValidacaoException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ErroValidacaoException(String msg) {
		super(msg);
	}
}
