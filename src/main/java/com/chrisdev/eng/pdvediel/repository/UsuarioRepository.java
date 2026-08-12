package com.chrisdev.eng.pdvediel.repository;

import com.chrisdev.eng.pdvediel.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}