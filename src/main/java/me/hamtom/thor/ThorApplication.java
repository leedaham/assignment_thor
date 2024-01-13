package me.hamtom.thor;

import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Configuration
@EnableJpaAuditing
@SpringBootApplication
public class ThorApplication{

	public static void main(String[] args) {
		SpringApplication.run(ThorApplication.class, args);
	}

	/**
	 * tomcat %2F 설정
	 */
	@Bean
	TomcatConnectorCustomizer connectorCustomizer() {
		return (connector) -> connector.setEncodedSolidusHandling(EncodedSolidusHandling.DECODE.getValue());
	}
}
