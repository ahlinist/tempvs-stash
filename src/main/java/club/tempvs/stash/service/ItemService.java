package club.tempvs.stash.service;

import club.tempvs.stash.domain.Item;

import java.util.List;

public interface ItemService {

    Item create(Long itemGroupId, Item item);

    List<Item> getItems(Long itemGroupId, int page, int size);
}
