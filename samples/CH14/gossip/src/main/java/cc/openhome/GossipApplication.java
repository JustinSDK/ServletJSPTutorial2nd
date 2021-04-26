package cc.openhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(
    scanBasePackages={
        "cc.openhome",
        "cc.openhome.controller",
        "cc.openhome.model",
        "cc.openhome.aspect"
    }
)
@PropertySources({
    @PropertySource("classpath:web.properties"),
    @PropertySource("classpath:mail.properties")
})
public class GossipApplication {
	public static void main(String[] args) {
		SpringApplication.run(GossipApplication.class, args);
	}
}
