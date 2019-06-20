package club.tempvs.stash.controller;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
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
        Long itemGroupId = 1L;
        Long itemId = 2L;

        when(itemService.getItem(itemId)).thenReturn(item);
        when(item.toItemDto()).thenReturn(itemDto);

        ItemDto result = itemController.getItem(itemGroupId, itemId);

        verify(itemService).getItem(itemId);
        verifyNoMoreInteractions(itemService);

        assertEquals("An itemDto is returned", itemDto, result);
    }
}
