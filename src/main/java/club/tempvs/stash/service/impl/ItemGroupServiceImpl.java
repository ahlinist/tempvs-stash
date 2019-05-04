package club.tempvs.stash.service.impl;

import static java.util.Objects.*;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.ItemGroupService;
import club.tempvs.stash.service.UserService;
import club.tempvs.stash.util.ValidationHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemGroupServiceImpl implements ItemGroupService {

    private static final String NAME_FIELD = "name";
    private static final String NAME_BLANK = "group.name.blank.error";

    private final UserHolder userHolder;
    private final ItemGroupRepository itemGroupRepository;
    private final ValidationHelper validationHelper;
    private final UserService userService;

    @Override
    public ItemGroup create(ItemGroup itemGroup) {
        ErrorsDto errors = validationHelper.getErrors();

        if (isNull(itemGroup.getName())) {
            validationHelper.addError(errors, NAME_FIELD, NAME_BLANK);
        }

        validationHelper.processErrors(errors);
        Long userId = userHolder.getUser().getId();
        User user = userService.getById(userId);
        itemGroup.setOwner(user);
        return save(itemGroup);
    }

    @Override
    public StashDto getStash(Long userId) {
        Long id = Optional.ofNullable(userId)
                .orElseGet(() -> userHolder.getUser().getId());
        User user = userService.getById(id);
        List<ItemGroup> groups = findGroupsByUser(user);
        return new StashDto(user, groups);
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
    private List<ItemGroup> findGroupsByUser(User user) {
        return itemGroupRepository.findAllByOwner(user);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private ItemGroup save(ItemGroup itemGroup) {
        return itemGroupRepository.save(itemGroup);
    }
}
