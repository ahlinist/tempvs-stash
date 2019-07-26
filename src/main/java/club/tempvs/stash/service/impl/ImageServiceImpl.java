package club.tempvs.stash.service.impl;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

import club.tempvs.stash.amqp.ImageEventProcessor;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageEventProcessor imageEventProcessor;

    @Override
    public void delete(List<String> objectIds) {
        imageEventProcessor.deleteByIds()
                .send(withPayload(objectIds).build());
    }

    @Override
    public void delete(String belongsTo, Long entityId) {
        String query = String.format("%1$s::%2$d", belongsTo, entityId);
        imageEventProcessor.deleteForEntity()
                .send(withPayload(query).build());
    }

    @Override
    public void store(ImageDto payload) {
        imageEventProcessor.store()
                .send(withPayload(payload).build());
    }
}
