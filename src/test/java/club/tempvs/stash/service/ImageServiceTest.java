package club.tempvs.stash.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.amqp.ImageEventProcessor;
import club.tempvs.stash.clients.ImageClient;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.service.impl.ImageServiceImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ImageEventProcessor imageEventProcessor;
    @Mock
    private ImageClient imageClient;
    @Mock
    private MessageChannel messageChannel;
    @Mock
    private ImageDto imageDto;

    @Test
    public void testDeleteImage() {
        String objectId1 = "objectId1";
        String objectId2 = "objectId2";
        List<String> objectIds = ImmutableList.of(objectId1, objectId2);

        when(imageEventProcessor.deleteImage()).thenReturn(messageChannel);

        imageService.delete(objectIds);

        verify(imageEventProcessor).deleteImage();
        verify(messageChannel).send(any(Message.class));
        verifyNoMoreInteractions(imageEventProcessor);
    }

    @Test
    public void testStore() {
        when(imageClient.store(imageDto)).thenReturn(imageDto);

        ImageDto result = imageService.store(imageDto);

        verify(imageClient).store(imageDto);
        verifyNoMoreInteractions(imageClient);

        assertEquals("ImageDto is returned", imageDto, result);
    }
}
