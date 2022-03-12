package com.gabrielferreira.br.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gabrielferreira.br.modelo.Livro;

@Repository
public interface LivroRepositorio extends JpaRepository<Livro, Long>{

	@Query("SELECT l FROM Livro l where l.isbn = :isbn")
	public Livro buscarIsbnLivro(@Param("isbn") String isbn);

	public Boolean existsByTitulo(String titulo);
}
