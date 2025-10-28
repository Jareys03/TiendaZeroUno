package com.example.mdai;

import com.example.mdai.model.Usuario;
import com.example.mdai.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TiendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository repo) {
		return args -> {
			repo.save(new Usuario("Javier", "javier@zerouno.com"));
			repo.save(new Usuario("Laura", "laura@zerouno.com"));
			repo.save(new Usuario("Carlos", "carlos@zerouno.com"));
			repo.save(new Usuario("Luis", "luis@zerouno.com"));
			repo.save(new Usuario("Pedro", "pedro@zerouno.com"));
			repo.save(new Usuario("Antonio", "antonio@zerouno.com"));
			repo.save(new Usuario("Maria", "maria@zerouno.com"));
			repo.findAll().forEach(u ->
					System.out.println("Usuario: " + u.getId() + " - " + u.getNombre())
			);
		};
	}

}
