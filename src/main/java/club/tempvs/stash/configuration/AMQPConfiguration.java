package club.tempvs.stash.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfiguration {

    @Value("${amqp.url}")
    private String amqpUrl;
    @Value("${amqp.timeout}")
    private int amqpTimeout;

    @Bean
    public ConnectionFactory amqpConnectionFactory() throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        if (amqpUrl != null) {
            connectionFactory.setUri(amqpUrl);
            connectionFactory.setConnectionTimeout(amqpTimeout);
        }

        return connectionFactory;
    }
}
