package club.tempvs.stash.service;

import club.tempvs.stash.domain.ItemGroup;

import java.util.List;

public interface ItemGroupService {

    ItemGroup create(ItemGroup itemGroup);

    List<ItemGroup> findAllByUserId(Long userId);

    ItemGroup getById(Long id);
}
