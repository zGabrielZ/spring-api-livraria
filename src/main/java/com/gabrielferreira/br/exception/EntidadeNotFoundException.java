package com.gabrielferreira.br.exception;

public class EntidadeNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EntidadeNotFoundException(String msg) {
		super(msg);
	}

}
