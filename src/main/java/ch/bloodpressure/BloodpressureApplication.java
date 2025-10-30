package ch.bloodpressure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BloodpressureApplication {

	public static void main(String[] args) {
        log.info("Starting Bloodpressure Application...");
		SpringApplication.run(BloodpressureApplication.class, args);
        log.info("Bloodpressure Application started successfully.");
	}

}
