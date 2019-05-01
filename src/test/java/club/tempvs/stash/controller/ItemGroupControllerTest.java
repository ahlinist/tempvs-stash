package club.tempvs.stash.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ItemGroupDto;
import club.tempvs.stash.service.ItemGroupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ItemGroupControllerTest {

    private ItemGroupController itemGroupController;

    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ItemGroupDto itemGroupDto;
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
        List<ItemGroup> itemGroups = Arrays.asList(itemGroup, itemGroup);
        List<ItemGroupDto> itemGroupDtos = Arrays.asList(itemGroupDto, itemGroupDto);

        when(itemGroupService.findAllByUserId(userId)).thenReturn(itemGroups);
        when(itemGroup.toItemGroupDto()).thenReturn(itemGroupDto);

        List<ItemGroupDto> result = itemGroupController.findGroupsByUserId(userId);

        verify(itemGroupService).findAllByUserId(userId);
        verifyNoMoreInteractions(itemGroupService);

        assertEquals("List of groups is returned", itemGroupDtos, result);
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
