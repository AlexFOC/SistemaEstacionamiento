package edu.ec.ucacue.sistemaproyecto;

import edu.ec.ucacue.sistemaproyecto.model.Plaza;
import edu.ec.ucacue.sistemaproyecto.model.Configuracion;
import edu.ec.ucacue.sistemaproyecto.service.ConfiguracionService;
import edu.ec.ucacue.sistemaproyecto.repository.PlazaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SistemaproyectoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaproyectoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(PlazaRepository plazaRepository) {
		return args -> {
			if (plazaRepository.count() == 0) {
				for (char c = 'A'; c <= 'G'; c++) {
					for (int i = 1; i <= 3; i++) {
						Plaza plaza = new Plaza();
						plaza.setId(String.valueOf(c) + i);
						plaza.setEstado("libre");
						plazaRepository.save(plaza);
					}
				}
			}
		};
	}

	@Bean
	public CommandLineRunner initConfiguracion(ConfiguracionService configuracionService) {
		return args -> {
			if (configuracionService.obtenerConfiguracion() == null) {
				Configuracion config = new Configuracion();
				config.setTarifaPorHora(1.50);
				config.setToleranciaMinutos(15);
				config.setRedondeoMinutos(30);
				config.setValidacionPlacaActiva(true);
				config.setPatronPlaca("^[A-Z]{3}-\\d{3,4}$");
				config.setPaisSeleccionado("ECUADOR");
				configuracionService.guardarConfiguracion(config);
			}
		};
	}
}


