package club.tempvs.stash.service.impl;

import static java.util.Objects.*;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.ItemGroupService;
import club.tempvs.stash.util.ValidationHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemGroupServiceImpl implements ItemGroupService {

    private static final String NAME_FIELD = "name";
    private static final String NAME_BLANK = "group.name.blank.error";

    private final UserHolder userHolder;
    private final ItemGroupRepository itemGroupRepository;
    private final ValidationHelper validationHelper;

    @Override
    public ItemGroup create(ItemGroup itemGroup) {
        ErrorsDto errors = validationHelper.getErrors();

        if (isNull(itemGroup.getName())) {
            validationHelper.addError(errors, NAME_FIELD, NAME_BLANK);
        }

        validationHelper.processErrors(errors);
        User user = userHolder.getUser();
        itemGroup.setOwner(user);
        return save(itemGroup);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public List<ItemGroup> findAllByUserId(Long userId) {
        Long id;

        if (userId == null) {
            User user = userHolder.getUser();
            id = user.getId();
        } else {
            id = userId;
        }

        return itemGroupRepository.findAllByUserId(id);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public ItemGroup getById(Long id) {
        return itemGroupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No group with id " + id + " found!"));
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private ItemGroup save(ItemGroup itemGroup) {
        return itemGroupRepository.save(itemGroup);
    }
}
