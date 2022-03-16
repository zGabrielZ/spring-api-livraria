package com.gabrielferreira.br.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gabrielferreira.br.modelo.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{

	public Boolean existsByAutor(String autor);
	
	@Query("SELECT u FROM Usuario u where u.autor = :autor and u.id <> :idUsuario")
	public Usuario buscarAutorUsuarioQuandoForAtualizar(@Param("autor") String autor,@Param("idUsuario") Long idUsuario);
}
