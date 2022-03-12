package com.gabrielferreira.br.validacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gabrielferreira.br.modelo.dto.criar.CriarUsuarioDTO;

public class CriarUsuarioValidacao implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static List<String> getVerificacaoErrosCriarUsuario(CriarUsuarioDTO criarUsuarioDTO){
		List<String> erros = new ArrayList<String>();
		
		String msgNomeErro = getVerificarErroCriarUsuarioNome(criarUsuarioDTO.getAutor());
		String msgDataNascimentoErro = getVerificarErroCriarUsuarioDataNascimento(criarUsuarioDTO.getDataNascimento());
		
		if(msgNomeErro != null) {
			erros.add(msgNomeErro);
		}
		
		if(msgDataNascimentoErro != null) {
			erros.add(msgDataNascimentoErro);
		}
		
		return erros;
	}
	
	private static String getVerificarErroCriarUsuarioNome(String nome) {
		// Se não estiver na faixa do 5 a 150 e for diferente de nulo
		if(nome != null && !(nome.length() >= 5 && nome.length() <= 150)) {
			return "O campo nome deve ter no mínimo 5 até 150 caracteres";
		} else if(nome == null) {
			return "Nome não pode ser vazio";
		}
		return null;
	}
	
	private static String getVerificarErroCriarUsuarioDataNascimento(Date dataNascimento) {
		// Se data de nascimento for depois que a data atual e for difente de nulo
		if(dataNascimento != null && dataNascimento.after(new Date())) {
			return "O campo data de nascimento não pode ser depois da data atual";
		} else if(dataNascimento == null){
			return "Data de nascimento não pode ser vazio";
		}
		return null;
	}

}
