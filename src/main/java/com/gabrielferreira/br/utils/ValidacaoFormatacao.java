package com.gabrielferreira.br.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidacaoFormatacao implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Definindo um padrão para o nome, sempre colocando a primeira letra do nome como maiúsculo e o restante como minusculo
	public static String getFormatacaoNome(String valor) {
		
		// Removendo os espaços
		String valorSemEspaco = valor.trim();

		// Separando cada nome informado em uma lista
		List<String> nomes = new ArrayList<>(Arrays.asList(valorSemEspaco.split(" ")));

		// Criar uma lista que vai inserir o nome corretamente
		List<String> nomesComFormato = new ArrayList<String>();

		// Realizando a formatação
		for (String nomeFormato : nomes) {
			if (!nomeFormato.isEmpty()) {
				String primeiraLetra = nomeFormato.substring(0, 1).toUpperCase();
				String restante = nomeFormato.substring(1).toLowerCase();
				nomesComFormato.add(primeiraLetra + restante);
			}
		}

		// Ajustando a formatação
		String nomeFormatado = nomesComFormato.toString().replace(",", "").replace("[", "").replace("]", "");
		return nomeFormatado;
	
	}

}
