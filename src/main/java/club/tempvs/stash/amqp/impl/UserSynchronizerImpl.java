package club.tempvs.stash.amqp.impl;

import club.tempvs.stash.amqp.UserSynchronizer;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserSynchronizerImpl extends AbstractAMQPConnector implements UserSynchronizer {

    private static final String STASH_USER_AMQP_QUEUE = "stash.user";

    private final UserService userService;

    @Autowired
    public UserSynchronizerImpl(ObjectMapper jacksonObjectMapper,
                                UserService userService,
                                ConnectionFactory amqpConnectionFactory) {
        super(amqpConnectionFactory, jacksonObjectMapper, STASH_USER_AMQP_QUEUE);
        this.userService = userService;
    }

    public void execute() {
        super.receive(this::refreshUser);
    }

    private void refreshUser(String json) {
        try {
            User user = jacksonObjectMapper.readValue(json, User.class);
            userService.save(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
