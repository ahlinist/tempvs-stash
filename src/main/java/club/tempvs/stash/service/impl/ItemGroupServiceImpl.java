package club.tempvs.stash.service.impl;

import static org.apache.commons.lang.StringUtils.isBlank;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.ErrorsDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.exception.ForbiddenException;
import club.tempvs.stash.holder.UserHolder;
import club.tempvs.stash.service.ItemGroupService;
import club.tempvs.stash.service.UserService;
import club.tempvs.stash.util.ValidationHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemGroupServiceImpl implements ItemGroupService {

    private static final String NAME_FIELD = "name";
    private static final String NAME_BLANK_ERROR = "group.name.blank.error";

    private final UserHolder userHolder;
    private final ItemGroupRepository itemGroupRepository;
    private final ValidationHelper validationHelper;
    private final UserService userService;

    @Override
    public ItemGroup create(ItemGroup itemGroup) {
        ErrorsDto errors = validationHelper.getErrors();

        if (isBlank(itemGroup.getName())) {
            validationHelper.addError(errors, NAME_FIELD, NAME_BLANK_ERROR);
        }

        validationHelper.processErrors(errors);
        Long userId = userHolder.getUser().getId();
        User user = userService.getById(userId);
        itemGroup.setOwner(user);
        return save(itemGroup);
    }

    @Override
    public StashDto getStash(Long userId, int page, int size) {
        Long id = Optional.ofNullable(userId)
                .orElseGet(() -> userHolder.getUser().getId());
        User user = userService.getById(id);
        Pageable pageable = PageRequest.of(page, size);
        List<ItemGroup> groups = findGroupsByUser(user, pageable);
        return new StashDto(user, groups);
    }

    @Override
    public ItemGroup updateName(Long id, String name) {
        User user = userHolder.getUser();
        ItemGroup itemGroup = getById(id);
        User owner = itemGroup.getOwner();

        if (!Objects.equals(user.getId(), owner.getId())) {
            throw new ForbiddenException("Only owner can change group name");
        }

        ErrorsDto errorsDto = validationHelper.getErrors();

        if (isBlank(name)) {
            validationHelper.addError(errorsDto, NAME_FIELD, NAME_BLANK_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        itemGroup.setName(name);
        return save(itemGroup);
    }

    @Override
    public ItemGroup updateDescription(Long id, String description) {
        User user = userHolder.getUser();
        ItemGroup itemGroup = getById(id);
        User owner = itemGroup.getOwner();

        if (!Objects.equals(user.getId(), owner.getId())) {
            throw new ForbiddenException("Only owner can change group name");
        }

        itemGroup.setDescription(description);
        return save(itemGroup);
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
    private List<ItemGroup> findGroupsByUser(User user, Pageable pageable) {
        return itemGroupRepository.findAllByOwner(user, pageable);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private ItemGroup save(ItemGroup itemGroup) {
        return itemGroupRepository.save(itemGroup);
    }
}
