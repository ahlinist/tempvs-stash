package club.tempvs.stash.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ItemGroupDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.service.ItemGroupService;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ItemGroupControllerTest {

    @InjectMocks
    private ItemGroupController itemGroupController;

    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ItemGroupDto itemGroupDto;
    @Mock
    private StashDto stashDto;
    @Mock
    private ItemGroupService itemGroupService;

    @Test
    public void testCreate() {

        when(itemGroupDto.toGroup()).thenReturn(itemGroup);
        when(itemGroupService.create(itemGroup)).thenReturn(itemGroup);
        when(itemGroup.toItemGroupDto()).thenReturn(itemGroupDto);

        ItemGroupDto result = itemGroupController.create(itemGroupDto);

        verify(itemGroupService).create(itemGroup);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("GroupDto is returned", itemGroupDto, result);
    }

    @Test
    public void testFindGroupsByUserId() {
        Long userId = 1L;
        int page = 0;
        int size = 40;

        when(itemGroupService.getStash(userId, page, size)).thenReturn(stashDto);

        StashDto result = itemGroupController.findGroupsByUserId(userId, page, size);

        verify(itemGroupService).getStash(userId, page, size);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("List of groups is returned", stashDto, result);
    }

    @Test
    public void testFindGroupById() {
        Long id = 1L;

        when(itemGroupService.getById(id)).thenReturn(itemGroup);
        when(itemGroup.toItemGroupDto()).thenReturn(itemGroupDto);

        ItemGroupDto result = itemGroupController.findGroupById(id);

        verify(itemGroupService).getById(id);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("ItemGroupDto is returned", itemGroupDto, result);
    }

    @Test
    public void testUpdateName() {
        Long id = 1L;
        String name = "name";

        when(itemGroupService.updateName(id, name)).thenReturn(itemGroup);
        when(itemGroup.toItemGroupDto()).thenReturn(itemGroupDto);

        ItemGroupDto result = itemGroupController.updateName(id, ImmutableMap.of("name", name));

        verify(itemGroupService).updateName(id, name);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("ItemGroupDto is returned", itemGroupDto, result);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        String description = "description";

        when(itemGroupService.updateDescription(id, description)).thenReturn(itemGroup);
        when(itemGroup.toItemGroupDto()).thenReturn(itemGroupDto);

        ItemGroupDto result = itemGroupController.updateDescription(id, ImmutableMap.of("description", description));

        verify(itemGroupService).updateDescription(id, description);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("ItemGroupDto is returned", itemGroupDto, result);
    }
}
