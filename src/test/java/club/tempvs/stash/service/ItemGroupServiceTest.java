package club.tempvs.stash.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.impl.ItemGroupServiceImpl;
import club.tempvs.stash.util.ValidationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ItemGroupServiceTest {

    private ItemGroupService itemGroupService;

    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ErrorsDto errors;
    @Mock
    private User user;
    @Mock
    private UserHolder userHolder;
    @Mock
    private ItemGroupRepository itemGroupRepository;
    @Mock
    private ValidationHelper validationHelper;

    @Before
    public void setUp() {
        itemGroupService = new ItemGroupServiceImpl(userHolder, itemGroupRepository, validationHelper);
    }

    @Test
    public void testCreate() {
        String name = "name";

        when(userHolder.getUser()).thenReturn(user);
        when(itemGroupRepository.save(itemGroup)).thenReturn(itemGroup);
        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroup.getName()).thenReturn(name);

        ItemGroup result = itemGroupService.create(itemGroup);

        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errors);
        verify(itemGroup).setOwner(user);
        verify(itemGroupRepository).save(itemGroup);
        verifyNoMoreInteractions(validationHelper, itemGroupRepository);

        assertEquals("ItemGroup object is returned", itemGroup, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateForInvalidInput() {
        String name = "";

        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroup.getName()).thenReturn(name);
        doThrow(new IllegalArgumentException()).when(validationHelper).processErrors(errors);

        itemGroupService.create(itemGroup);
    }

    @Test
    public void testFindAllByUserId() {
        Long userId = 1L;
        List<ItemGroup> itemGroups = Arrays.asList(itemGroup, itemGroup);

        when(itemGroupRepository.findAllByUserId(userId)).thenReturn(itemGroups);

        List<ItemGroup> result = itemGroupService.findAllByUserId(userId);

        verify(itemGroupRepository).findAllByUserId(userId);
        verifyNoMoreInteractions(itemGroupRepository);

        assertEquals("ItemGroup list is returned", itemGroups, result);
    }

    @Test
    public void testFindAllByUserIdForMissingId() {
        Long userId = 1L;
        List<ItemGroup> itemGroups = Arrays.asList(itemGroup, itemGroup);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemGroupRepository.findAllByUserId(userId)).thenReturn(itemGroups);

        List<ItemGroup> result = itemGroupService.findAllByUserId(null);

        verify(itemGroupRepository).findAllByUserId(userId);
        verifyNoMoreInteractions(itemGroupRepository);

        assertEquals("ItemGroup list is returned", itemGroups, result);
    }

    @Test
    public void testGetById() {
        Long itemGroupId = 1L;

        when(itemGroupRepository.findById(itemGroupId)).thenReturn(Optional.of(itemGroup));

        ItemGroup result = itemGroupService.getById(itemGroupId);

        verify(itemGroupRepository).findById(itemGroupId);
        verifyNoMoreInteractions(itemGroupRepository);

        assertEquals("Item group is returned", itemGroup, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetByIdForMissingEntry() {
        Long itemGroupId = 1L;

        when(itemGroupRepository.findById(itemGroupId)).thenReturn(Optional.empty());

        itemGroupService.getById(itemGroupId);
    }
}
