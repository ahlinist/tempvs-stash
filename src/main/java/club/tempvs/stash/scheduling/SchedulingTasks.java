package club.tempvs.stash.scheduling;

import club.tempvs.stash.amqp.UserSynchronizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "amqp.enabled", havingValue = "true")
public class SchedulingTasks {

    private final UserSynchronizer userSynchronizer;

    //runs every 30 seconds
    @Scheduled(fixedRate = 30 * 1000)
    public void refreshUsers() {
        userSynchronizer.execute();
    }
}
