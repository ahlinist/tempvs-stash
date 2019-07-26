package club.tempvs.stash;

import club.tempvs.stash.amqp.ImageEventProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
@EnableBinding(ImageEventProcessor.class)
public class ItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemApplication.class, args);
	}
}
