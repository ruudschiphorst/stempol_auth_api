package nl.politie.predev.stempol.auth.api;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = {EmbeddedLDAPAuthApi.class})
public class EmbeddedLDAPAuthApi {

	@PostConstruct
	private void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	public static void main(String... args) {
		SpringApplication.run(EmbeddedLDAPAuthApi.class, args);
	}
	
}
