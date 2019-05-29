package club.tempvs.stash.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.exception.ForbiddenException;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.impl.ItemGroupServiceImpl;
import club.tempvs.stash.util.ValidationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ItemGroupServiceTest {

    @InjectMocks
    private ItemGroupServiceImpl itemGroupService;

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
    @Mock
    private UserService userService;

    @Test
    public void testCreate() {
        String name = "name";
        Long userId = 1L;

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);
        when(itemGroupRepository.save(itemGroup)).thenReturn(itemGroup);
        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroup.getName()).thenReturn(name);

        ItemGroup result = itemGroupService.create(itemGroup);

        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errors);
        verify(userService).getById(userId);
        verify(itemGroup).setOwner(user);
        verify(itemGroupRepository).save(itemGroup);
        verifyNoMoreInteractions(validationHelper, itemGroupRepository, userService);

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
    public void testGetStash() {
        Long userId = 1L;
        List<ItemGroup> itemGroups = Arrays.asList(itemGroup, itemGroup);
        StashDto stashDto = new StashDto(user, itemGroups);

        when(userService.getById(userId)).thenReturn(user);
        when(itemGroupRepository.findAllByOwner(user)).thenReturn(itemGroups);

        StashDto result = itemGroupService.getStash(userId);

        verify(userService).getById(userId);
        verify(itemGroupRepository).findAllByOwner(user);
        verifyNoMoreInteractions(itemGroupRepository, userService);

        assertEquals("StashDto is returned", stashDto, result);
    }

    @Test
    public void testGetStashForMissingId() {
        Long userId = 1L;
        List<ItemGroup> itemGroups = Arrays.asList(itemGroup, itemGroup);
        StashDto stashDto = new StashDto(user, itemGroups);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);
        when(itemGroupRepository.findAllByOwner(user)).thenReturn(itemGroups);

        StashDto result = itemGroupService.getStash(null);

        verify(userService).getById(userId);
        verify(itemGroupRepository).findAllByOwner(user);
        verifyNoMoreInteractions(itemGroupRepository, userService);

        assertEquals("StashDto is returned", stashDto, result);
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

    @Test
    public void testUpdateName() {
        Long id = 1L;
        Long userId = 2L;
        String name = "name";

        when(userHolder.getUser()).thenReturn(user);
        when(itemGroupRepository.findById(id)).thenReturn(Optional.of(itemGroup));
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemGroupRepository.save(itemGroup)).thenReturn(itemGroup);

        itemGroupService.updateName(id, name);

        verify(itemGroupRepository).findById(id);
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errors);
        verify(itemGroup).setName(name);
        verify(itemGroupRepository).save(itemGroup);
        verifyNoMoreInteractions(itemGroupRepository, validationHelper);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateNameForIllegalUser() {
        Long id = 1L;
        Long userId = 2L;
        Long wrongUserId = 3L;
        String name = "name";
        User wrongUser = new User();
        wrongUser.setId(wrongUserId);

        when(userHolder.getUser()).thenReturn(wrongUser);
        when(itemGroupRepository.findById(id)).thenReturn(Optional.of(itemGroup));
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemGroupService.updateName(id, name);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateNameForMissingEntry() {
        Long id = 1L;
        String name = "name";

        when(itemGroupRepository.findById(id)).thenReturn(Optional.empty());

        itemGroupService.updateName(id, name);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        Long userId = 2L;
        String description = "description";

        when(userHolder.getUser()).thenReturn(user);
        when(itemGroupRepository.findById(id)).thenReturn(Optional.of(itemGroup));
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemGroupRepository.save(itemGroup)).thenReturn(itemGroup);

        itemGroupService.updateDescription(id, description);

        verify(itemGroupRepository).findById(id);
        verify(itemGroup).setDescription(description);
        verify(itemGroupRepository).save(itemGroup);
        verifyNoMoreInteractions(itemGroupRepository);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateDescriptionForIllegalUser() {
        Long id = 1L;
        Long userId = 2L;
        Long wrongUserId = 3L;
        String description = "description";
        User wrongUser = new User();
        wrongUser.setId(wrongUserId);

        when(userHolder.getUser()).thenReturn(wrongUser);
        when(itemGroupRepository.findById(id)).thenReturn(Optional.of(itemGroup));
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemGroupService.updateDescription(id, description);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateDescriptionForMissingEntry() {
        Long id = 1L;
        String description = "description";

        when(itemGroupRepository.findById(id)).thenReturn(Optional.empty());

        itemGroupService.updateDescription(id, description);
    }
}
