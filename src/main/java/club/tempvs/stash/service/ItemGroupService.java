package club.tempvs.stash.service;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.StashDto;

public interface ItemGroupService {

    ItemGroup create(ItemGroup itemGroup);

    StashDto getStash(Long userId);

    ItemGroup getById(Long id);

    ItemGroup updateName(Long id, String name);

    ItemGroup updateDescription(Long id, String description);
}
