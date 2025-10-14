package BlueMoon.bluemoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"BlueMoon", "BlueMoon.bluemoon"})
public class BluemoonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BluemoonApplication.class, args);
	}

}
