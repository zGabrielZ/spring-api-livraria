package com.gabrielferreira.br.repositorio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gabrielferreira.br.modelo.Livro;

@Repository
public interface LivroRepositorio extends JpaRepository<Livro, Long>{

	@Query("SELECT l FROM Livro l where l.isbn = :isbn")
	public Livro buscarIsbnLivro(@Param("isbn") String isbn);
	
	@Query("SELECT l FROM Livro l where l.isbn = :isbn and l.id <> :idLivro")
	public Livro buscarIsbnLivroQuandoForAtualizar(@Param("isbn") String isbn,@Param("idLivro") Long idLivro);

	public Boolean existsByTitulo(String titulo);
	
	@Query("SELECT l FROM Livro l where l.titulo = :titulo and l.id <> :idLivro")
	public Optional<Livro> existsByTituloQuandoForAtualizar(@Param("titulo") String titulo,@Param("idLivro") Long idLivro);
	
	@Query("SELECT l FROM Livro l join l.categoria c where c.id = :idCategoria")
	public List<Livro> findLivrosByCategoriaId(@Param("idCategoria") Long idCategoria);
}
