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
    Optional<Usuario> findFirstBycorreo(String correo);
    long countByNombre(String nombre);
    boolean existsBycorreo(String correo);
    List<Usuario> findByNombreOrderByCorreoAsc(String nombre);

    // SQL nativas
    @Query(value = "SELECT * FROM usuarios WHERE correo = :correo", nativeQuery = true)
    Optional<Usuario> findBycorreoNative(@Param("correo") String correo);

    @Query(value = "SELECT * FROM usuarios WHERE nombre LIKE CONCAT('%', :nombre, '%')", nativeQuery = true)
    List<Usuario> searchByNombreLikeNative(@Param("nombre") String nombre);

    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE nombre = :nombre", nativeQuery = true)
    long countByNombreNative(@Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usuarios SET correo = :correo WHERE id = :id", nativeQuery = true)
    int updatecorreoByIdNative(@Param("id") Long id, @Param("correo") String correo);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuarios WHERE correo = :correo", nativeQuery = true)
    int deleteBycorreoNative(@Param("correo") String correo);
}
