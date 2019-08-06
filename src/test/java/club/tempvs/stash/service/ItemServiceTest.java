package club.tempvs.stash.service;

import club.tempvs.stash.client.SourceClient;
import club.tempvs.stash.dao.ItemRepository;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.dto.SourceDto;
import club.tempvs.stash.exception.ForbiddenException;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.model.Classification;
import club.tempvs.stash.model.Period;
import club.tempvs.stash.service.impl.ItemServiceImpl;
import club.tempvs.stash.util.ValidationHelper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

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
    private ImageService imageService;
    @Mock
    private SourceClient sourceClient;
    @Mock
    private Item item;
    @Mock
    private ItemGroup itemGroup;
    @Mock
    private ErrorsDto errors;
    @Mock
    private User user, owner;
    @Mock
    private ImageDto imageDto;
    @Mock
    private SourceDto sourceDto;

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

    @Test
    public void testUpdateName() {
        Long id = 1L;
        Long userId = 2L;
        String name = "name";

        when(userHolder.getUser()).thenReturn(user);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(validationHelper.getErrors()).thenReturn(errors);
        when(itemRepository.save(item)).thenReturn(item);

        itemService.updateName(id, name);

        verify(itemRepository).findById(id);
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errors);
        verify(item).setName(name);
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository, validationHelper);
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
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemService.updateName(id, name);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateNameForMissingEntry() {
        Long id = 1L;
        String name = "name";

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        itemService.updateName(id, name);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        Long userId = 2L;
        String description = "description";

        when(userHolder.getUser()).thenReturn(user);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.save(item)).thenReturn(item);

        itemService.updateDescription(id, description);

        verify(itemRepository).findById(id);
        verify(item).setDescription(description);
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository);
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
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemService.updateDescription(id, description);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateDescriptionForMissingEntry() {
        Long id = 1L;
        String description = "description";

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        itemService.updateDescription(id, description);
    }

    @Test
    public void testAddImage() {
        Long itemId = 1L;
        Long userId = 2L;

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);

        itemService.addImage(itemId, imageDto);

        verify(itemRepository).findById(itemId);
        verify(imageService).store(imageDto);
        verifyNoMoreInteractions(itemRepository, imageService);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddImageForWrongUser() {
        Long itemId = 1L;
        Long userId = 2L;
        Long wrongUserId = 3L;
        User wrongUser = new User();
        wrongUser.setId(wrongUserId);

        when(userHolder.getUser()).thenReturn(wrongUser);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemService.addImage(itemId, imageDto);
    }

    @Test(expected = NoSuchElementException.class)
    public void testAddImageForMissingItem() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.addImage(itemId, imageDto);
    }

    @Test
    public void testDeleteImage() {
        Long itemId = 1L;
        Long userId = 2L;
        String objectId = "objectId";
        List<String> objectIds = ImmutableList.of(objectId);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);

        itemService.deleteImage(itemId, objectId);

        verify(itemRepository).findById(itemId);
        verify(imageService).delete(objectIds);
        verifyNoMoreInteractions(itemRepository, imageService);
    }

    @Test(expected = ForbiddenException.class)
    public void testDeleteImageForWrongUser() {
        Long itemId = 1L;
        Long userId = 2L;
        Long wrongUserId = 3L;
        User wrongUser = new User();
        wrongUser.setId(wrongUserId);
        String objectId = "objectId";

        when(userHolder.getUser()).thenReturn(wrongUser);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        itemService.deleteImage(itemId, objectId);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteImageForMissingItem() {
        Long itemId = 1L;
        String objectId = "objectId";

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.deleteImage(itemId, objectId);
    }

    @Test
    public void testDeleteItem() {
        Long itemId = 1L;
        Long userId = 2L;

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(user);

        itemService.delete(itemId);

        verify(itemRepository).findById(itemId);
        verify(imageService).delete("item", itemId);
        verify(itemRepository).delete(item);
        verifyNoMoreInteractions(itemRepository, imageService);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteItemForMissingItem() {
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.delete(itemId);
    }

    @Test(expected = ForbiddenException.class)
    public void testDeleteItemForWrongUser() {
        Long itemId = 1L;
        Long userId = 2L;
        Long wrongUserId = 3L;
        User wrongUser = new User();
        wrongUser.setId(wrongUserId);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(wrongUser);

        itemService.delete(itemId);
    }

    @Test
    public void testLinkSource() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(owner);
        when(sourceClient.get(sourceId)).thenReturn(sourceDto);
        when(sourceDto.getClassification()).thenReturn(Classification.ARMOR);
        when(sourceDto.getPeriod()).thenReturn(Period.ANCIENT);
        when(item.getClassification()).thenReturn(Classification.ARMOR);
        when(item.getPeriod()).thenReturn(Period.ANCIENT);
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.linkSource(itemId, sourceId);

        verify(itemRepository).findById(itemId);
        verify(userHolder).getUser();
        verify(sourceClient).get(sourceId);
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository, sourceClient, userHolder);

        assertEquals("Item object is returned", item, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testLinkSourceForMissingItem() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.linkSource(itemId, sourceId);
    }

    @Test(expected = NoSuchElementException.class)
    public void testLinkSourceForMissingSource() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(owner);
        when(sourceClient.get(sourceId)).thenReturn(null);

        itemService.linkSource(itemId, sourceId);
    }

    @Test(expected = IllegalStateException.class)
    public void testLinkSourceForSourceWithWrongPeriodReturned() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(owner);
        when(sourceClient.get(sourceId)).thenReturn(sourceDto);
        when(sourceDto.getClassification()).thenReturn(Classification.ARMOR);
        when(sourceDto.getPeriod()).thenReturn(Period.CONTEMPORARY);
        when(item.getClassification()).thenReturn(Classification.ARMOR);
        when(item.getPeriod()).thenReturn(Period.ANCIENT);

        itemService.linkSource(itemId, sourceId);
    }

    @Test(expected = IllegalStateException.class)
    public void testLinkSourceForSourceWithWrongClassificationReturned() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(owner);
        when(sourceClient.get(sourceId)).thenReturn(sourceDto);
        when(sourceDto.getClassification()).thenReturn(Classification.CLOTHING);
        when(item.getClassification()).thenReturn(Classification.ARMOR);

        itemService.linkSource(itemId, sourceId);
    }

    @Test(expected = ForbiddenException.class)
    public void testLinkSourceForWrongOwner() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(userHolder.getUser()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);

        itemService.linkSource(itemId, sourceId);
    }

    @Test
    public void testUnlinkSource() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(owner);
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.unlinkSource(itemId, sourceId);

        verify(itemRepository).findById(itemId);
        verify(userHolder).getUser();
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository, sourceClient, userHolder);

        assertEquals("Item object is returned", item, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUnlinkSourceForMissingItem() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        itemService.unlinkSource(itemId, sourceId);
    }

    @Test(expected = ForbiddenException.class)
    public void testUnlinkSourceForWrongUser() {
        Long itemId = 1L;
        Long sourceId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(item.getItemGroup()).thenReturn(itemGroup);
        when(itemGroup.getOwner()).thenReturn(owner);
        when(userHolder.getUser()).thenReturn(user);

        itemService.unlinkSource(itemId, sourceId);
    }
}
