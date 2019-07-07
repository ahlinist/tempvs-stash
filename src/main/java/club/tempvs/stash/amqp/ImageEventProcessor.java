package club.tempvs.stash.amqp;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ImageEventProcessor {

    @Output("image.delete")
    MessageChannel deleteImage();
}
