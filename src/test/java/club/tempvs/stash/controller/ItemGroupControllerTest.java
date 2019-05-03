package club.tempvs.stash.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ItemGroupDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.service.ItemGroupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ItemGroupControllerTest {

    private ItemGroupController itemGroupController;

    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ItemGroupDto itemGroupDto;
    @Mock
    private StashDto stashDto;
    @Mock
    private ItemGroupService itemGroupService;

    @Before
    public void setUp() {
        itemGroupController = new ItemGroupController(itemGroupService);
    }

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

        when(itemGroupService.getStash(userId)).thenReturn(stashDto);

        StashDto result = itemGroupController.findGroupsByUserId(userId);

        verify(itemGroupService).getStash(userId);
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
}
