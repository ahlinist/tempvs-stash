package club.tempvs.stash.service.impl;

import club.tempvs.stash.amqp.ImageEventProcessor;
import club.tempvs.stash.clients.ImageClient;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageEventProcessor imageEventProcessor;
    private final ImageClient imageClient;

    @Override
    public void delete(List<String> objectIds) {
        imageEventProcessor.deleteImage()
                .send(MessageBuilder.withPayload(objectIds).build());
    }

    @Override
    public ImageDto store(ImageDto payload) {
        return imageClient.store(payload);
    }
}
