package com.gabrielferreira.br.controller.exception;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gabrielferreira.br.exception.EntidadeNotFoundException;
import com.gabrielferreira.br.exception.Erro;
import com.gabrielferreira.br.exception.ErroValidacaoException;
import com.gabrielferreira.br.exception.RegraDeNegocioException;

@ControllerAdvice
public class ErrosController {

	// A classe MethodArgumentNotValidException é chamada pois colocou a anotação @Valid
	// Método para fazer a validação de anotação via hibernate e encaminhar a resposta em bad request para o usuário
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Erro> handleValidationExceptions(MethodArgumentNotValidException ex) {
		
		// Obtem os erros de validação da anotação implementada na classe
		BindingResult bindingResult = ex.getBindingResult();
		
		// Instanciando uma lista para os campos e obtendo cada erro de validação
		List<String> campos = new ArrayList<String>();
		for(ObjectError objectError : bindingResult.getAllErrors()) {
			campos.add(objectError.getDefaultMessage());
		}
		
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		
		Erro erro = new Erro("Campos inválidos.", httpStatus.value(),new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), campos);
		return new ResponseEntity<>(erro,httpStatus);
	}
	
	// Método para fazer a validação via mão e encaminhar a resposta em bad request para o usuário
	@ExceptionHandler(ErroValidacaoException.class)
	public ResponseEntity<Erro> erroValidacaoException(ErroValidacaoException e, HttpServletRequest httpServletRequest){
		
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		
		String msgsErros = e.getMessage().replace("[", "").replace("]", "");
		List<String> msgsErrosArray = new ArrayList<String>(Arrays.asList(msgsErros.split(",")));
		
		List<String> campos = new ArrayList<String>();
		for(String campo : msgsErrosArray) {
			campos.add(campo.trim());
		}
	
		Erro erro = new Erro("Campos inválidos.", httpStatus.value(),new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), campos);
		return new ResponseEntity<>(erro,httpStatus);
	}
	
	// Método que retornar os erros de regra de negocio e encaminhar a respostas em bad request para o usuário
	@ExceptionHandler(RegraDeNegocioException.class)
	public ResponseEntity<Erro> regraDeNegocioException(RegraDeNegocioException e, HttpServletRequest httpServletRequest){
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		Erro erro = new Erro(e.getMessage(), httpStatus.value(),new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), null);
		return new ResponseEntity<>(erro,httpStatus);
	}
	
	// Método que retornar os erros da entidade not found e encaminhar a respostas em not found para o usuário
	@ExceptionHandler(EntidadeNotFoundException.class)
	public ResponseEntity<Erro> entidadeNotFoundException(EntidadeNotFoundException e, HttpServletRequest httpServletRequest){
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		Erro erro = new Erro(e.getMessage(), httpStatus.value(),new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), null);
		return new ResponseEntity<>(erro,httpStatus);
	}
}
