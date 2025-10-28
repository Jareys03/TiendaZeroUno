package com.example.mdai.repository;
import com.example.mdai.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    List<Usuario> findByNombre(String nombre);
    Optional<Usuario> findFirstByEmail(String email);
    long countByNombre(String nombre);
    boolean existsByEmail(String email);
    List<Usuario> findByNombreOrderByEmailAsc(String nombre);

    // SQL nativas
    @Query(value = "SELECT * FROM usuarios WHERE email = :email", nativeQuery = true)
    Optional<Usuario> findByEmailNative(@Param("email") String email);

    @Query(value = "SELECT * FROM usuarios WHERE nombre LIKE CONCAT('%', :nombre, '%')", nativeQuery = true)
    List<Usuario> searchByNombreLikeNative(@Param("nombre") String nombre);

    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE nombre = :nombre", nativeQuery = true)
    long countByNombreNative(@Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usuarios SET email = :email WHERE id = :id", nativeQuery = true)
    int updateEmailByIdNative(@Param("id") Long id, @Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuarios WHERE email = :email", nativeQuery = true)
    int deleteByEmailNative(@Param("email") String email);
}
