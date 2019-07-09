package club.tempvs.stash.controller;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private Item item;
    @Mock
    private ItemDto itemDto;
    @Mock
    private ImageDto imageDto;
    @Mock
    private ItemService itemService;

    @Test
    public void testCreate() {
        Long itemGroupId = 1L;

        when(itemDto.toItem()).thenReturn(item);
        when(itemService.create(itemGroupId, item)).thenReturn(item);
        when(item.toItemDto()).thenReturn(itemDto);

        ItemDto result = itemController.create(itemGroupId, itemDto);

        verify(itemService).create(itemGroupId, item);
        verifyNoMoreInteractions(itemService);

        assertEquals("GroupDto is returned", itemDto, result);
    }

    @Test
    public void testGetItems() {
        Long itemGroupId = 1L;
        List<Item> items = Arrays.asList(item, item, item);
        List<ItemDto> itemDtos = Arrays.asList(itemDto, itemDto, itemDto);
        int page = 0;
        int size = 40;

        when(itemService.getItems(itemGroupId, page, size)).thenReturn(items);
        when(item.toItemDto()).thenReturn(itemDto);

        List<ItemDto> result = itemController.getItems(itemGroupId, page, size);

        verify(itemService).getItems(itemGroupId, page, size);
        verifyNoMoreInteractions(itemService);

        assertEquals("A list of itemDtos is returned", itemDtos, result);
    }

    @Test
    public void testGetItem() {
        Long itemId = 2L;

        when(itemService.getItem(itemId)).thenReturn(item);
        when(item.toItemDto()).thenReturn(itemDto);

        ItemDto result = itemController.getItem(itemId);

        verify(itemService).getItem(itemId);
        verifyNoMoreInteractions(itemService);

        assertEquals("An itemDto is returned", itemDto, result);
    }

    @Test
    public void testUpdateName() {
        Long id = 1L;
        String name = "name";

        when(itemService.updateName(id, name)).thenReturn(item);
        when(item.toItemDto()).thenReturn(itemDto);

        ItemDto result = itemController.updateName(id, ImmutableMap.of("name", name));

        verify(itemService).updateName(id, name);
        verifyNoMoreInteractions(itemService);

        assertEquals("ItemGroupDto is returned", itemDto, result);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        String description = "description";

        when(itemService.updateDescription(id, description)).thenReturn(item);
        when(item.toItemDto()).thenReturn(itemDto);

        ItemDto result = itemController.updateDescription(id, ImmutableMap.of("description", description));

        verify(itemService).updateDescription(id, description);
        verifyNoMoreInteractions(itemService);

        assertEquals("ItemGroupDto is returned", itemDto, result);
    }

    @Test
    public void testAddImage() {
        Long id = 1L;

        itemController.addImage(id, imageDto);

        verify(itemService).addImage(id, imageDto);
        verifyNoMoreInteractions(itemService);
    }


    @Test
    public void testDeleteImage() {
        Long itemId = 1L;
        String objectId = "objectId";

        itemController.deleteImage(itemId, objectId);

        verify(itemService).deleteImage(itemId, objectId);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void testDeleteItem() {
        Long itemId = 1L;

        itemController.delete(itemId);

        verify(itemService).delete(itemId);
        verifyNoMoreInteractions(itemService);
    }
}
