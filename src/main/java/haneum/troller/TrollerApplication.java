package haneum.troller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TrollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrollerApplication.class, args);
	}

}
