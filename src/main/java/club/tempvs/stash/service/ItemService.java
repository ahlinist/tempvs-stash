package club.tempvs.stash.service;

import club.tempvs.stash.domain.Item;

public interface ItemService {

    Item create(Long itemGroupId, Item item);
}
