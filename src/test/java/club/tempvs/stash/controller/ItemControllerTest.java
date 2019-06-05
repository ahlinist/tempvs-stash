package club.tempvs.stash.controller;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
}
