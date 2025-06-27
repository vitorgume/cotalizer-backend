package com.gumeinteligenciacomercial.orcaja;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OrcajaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
//		System.setProperty("URL_BD", dotenv.get("URL_BD"));
//		System.setProperty("USER_BD", dotenv.get("USER_BD"));
//		System.setProperty("PASSWORD_BD", dotenv.get("PASSWORD_BD"));
		System.setProperty("OPENIA_API_KEY", dotenv.get("OPENIA_API_KEY"));
		System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));
		System.setProperty("SENHA_APP_GOOGLE", dotenv.get("SENHA_APP_GOOGLE"));
		System.setProperty("EMAIL_GOOGLE", dotenv.get("EMAIL_GOOGLE"));

		SpringApplication.run(OrcajaApplication.class, args);
	}

}
