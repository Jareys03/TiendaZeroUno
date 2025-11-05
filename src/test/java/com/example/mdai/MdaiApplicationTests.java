package com.example.mdai;

import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MdaiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UsuarioRepository usuarioRepository;
	@Test
	void usuarioRepositoryBeanExiste() {
		assertNotNull(usuarioRepository);
	}

}
