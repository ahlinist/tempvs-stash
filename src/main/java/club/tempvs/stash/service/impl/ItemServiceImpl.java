package club.tempvs.stash.service.impl;

import static org.apache.commons.lang.StringUtils.isBlank;
import static java.util.Objects.isNull;

import club.tempvs.stash.dao.ItemRepository;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ErrorsDto;
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

import java.util.List;
import java.util.Objects;

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
