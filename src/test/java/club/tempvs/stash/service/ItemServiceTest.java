package club.tempvs.stash.service;

import club.tempvs.stash.dao.ItemRepository;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.exception.ForbiddenException;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.impl.ItemServiceImpl;
import club.tempvs.stash.util.ValidationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemGroupService itemGroupService;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private UserHolder userHolder;
    @Mock
    private Item item;
    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ErrorsDto errors;
    @Mock
    private User user, owner;

    @Test
    public void testCreateItem() {
        Long groupId = 1L;
        Long userId = 2L;
        Long ownerId = 2L;

        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroupService.getById(groupId)).thenReturn(itemGroup);
        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(ownerId);
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.create(groupId, item);

        verify(itemGroupService).getById(groupId);
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemGroupService, itemRepository);

        assertEquals("Item is returned", item, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testCreateItemForIllegalUser() {
        Long groupId = 1L;
        Long userId = 2L;
        Long ownerId = 3L;

        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroupService.getById(groupId)).thenReturn(itemGroup);
        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(ownerId);

        itemService.create(groupId, item);
    }

    @Test
    public void testGetItems() {
        Long groupId = 1L;
        List<Item> items = Arrays.asList(item, item, item);
        int page = 0;
        int size = 40;
        Pageable pageable = PageRequest.of(page, size);

        when(itemRepository.findAllByItemGroupId(groupId, pageable)).thenReturn(items);

        List<Item> result = itemService.getItems(groupId, page, size);

        verify(itemRepository).findAllByItemGroupId(groupId, pageable);
        verifyNoMoreInteractions(itemRepository);

        assertEquals("A list of items is returned", items, result);
    }

    @Test
    public void testGetItem() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItem(itemId);

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository);

        assertEquals("Item object is returned", item, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetItemForMissingOne() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.getItem(itemId);
    }
}
