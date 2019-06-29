package club.tempvs.stash.service.impl;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static java.util.Objects.isNull;

import club.tempvs.stash.clients.ImageClient;
import club.tempvs.stash.dao.ItemRepository;
import club.tempvs.stash.domain.Image;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.exception.ForbiddenException;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.ItemGroupService;
import club.tempvs.stash.service.ItemService;
import club.tempvs.stash.util.ValidationHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String NAME_FIELD = "name";
    private static final String CLASSIFICATION_FIELD = "classification";
    private static final String PERIOD_FIELD = "period";
    private static final String NAME_MISSING_ERROR = "item.name.blank.error";
    private static final String CLASSIFICATION_MISSING_ERROR = "item.classification.blank.error";
    private static final String PERIOD_MISSING_ERROR = "item.period.blank.error";

    private final ItemRepository itemRepository;
    private final ItemGroupService itemGroupService;
    private final ValidationHelper validationHelper;
    private final UserHolder userHolder;
    private final ImageClient imageClient;

    @Override
    public Item create(Long itmGroupId, Item item) {
        ErrorsDto errors = validationHelper.getErrors();

        if (isBlank(item.getName())) {
            validationHelper.addError(errors, NAME_FIELD, NAME_MISSING_ERROR);
        }

        if (isNull(item.getClassification())) {
            validationHelper.addError(errors, CLASSIFICATION_FIELD, CLASSIFICATION_MISSING_ERROR);
        }

        if (isNull(item.getPeriod())) {
            validationHelper.addError(errors, PERIOD_FIELD, PERIOD_MISSING_ERROR);
        }

        validationHelper.processErrors(errors);

        ItemGroup itemGroup = itemGroupService.getById(itmGroupId);
        Long userId = userHolder.getUser().getId();
        Long groupOwnerId = itemGroup.getOwner().getId();

        if (!Objects.equals(userId, groupOwnerId)) {
            throw new ForbiddenException("Item group does not belong to a current user");
        }

        item.setItemGroup(itemGroup);
        return save(item);
    }

    public Item getItem(Long id) {
        return findItemById(id);
    }

    @Override
    public Item updateName(Long id, String name) {
        User user = userHolder.getUser();
        Item item = findItemById(id);
        User owner = item.getItemGroup().getOwner();

        if (!Objects.equals(user.getId(), owner.getId())) {
            throw new ForbiddenException("Only owner can change item name");
        }

        ErrorsDto errorsDto = validationHelper.getErrors();

        if (isBlank(name)) {
            validationHelper.addError(errorsDto, NAME_FIELD, NAME_MISSING_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        item.setName(name);
        return save(item);
    }

    @Override
    public Item updateDescription(Long id, String description) {
        User user = userHolder.getUser();
        Item item = findItemById(id);
        User owner = item.getItemGroup().getOwner();

        if (!Objects.equals(user.getId(), owner.getId())) {
            throw new ForbiddenException("Only owner can change group name");
        }

        item.setDescription(description);
        return save(item);
    }

    @Override
    public Item addImage(Long itemId, ImageDto imageDto) {
        User user = userHolder.getUser();
        Item item = findItemById(itemId);
        Long ownerId = item.getItemGroup()
                .getOwner()
                .getId();

        if (!Objects.equals(ownerId, user.getId())) {
            throw new ForbiddenException("Access denied");
        }

        ImageDto result = imageClient.store(imageDto);
        item.getImages().add(result.toImage());
        return save(item);
    }

    @Override
    public Item deleteImage(Long itemId, String objectId) {
        User user = userHolder.getUser();
        Item item = findItemById(itemId);
        Long ownerId = item.getItemGroup()
                .getOwner()
                .getId();

        if (!Objects.equals(ownerId, user.getId())) {
            throw new ForbiddenException("Access denied");
        }

        List<Image> images = item.getImages().stream()
                .filter(image -> !image.getObjectId().equals(objectId))
                .collect(toList());
        item.setImages(images);
        Item persistentItem = save(item);
        imageClient.delete(objectId);
        return persistentItem;
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public List<Item> getItems(Long itemGroupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAllByItemGroupId(itemGroupId, pageable);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Item save(Item item) {
        return itemRepository.save(item);
    }
}
