package com.gabrielferreira.br.repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gabrielferreira.br.modelo.Cliente;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long>{
	
}
