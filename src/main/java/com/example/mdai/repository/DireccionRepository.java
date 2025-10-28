package com.example.mdai.repository;

import com.example.mdai.model.Direccion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends CrudRepository<Direccion, Long> {

}
