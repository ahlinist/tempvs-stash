package club.tempvs.stash.service;

import club.tempvs.stash.domain.Item;

import java.util.List;

public interface ItemService {

    Item create(Long itemGroupId, Item item);

    List<Item> getItems(Long itemGroupId, int page, int size);

    Item getItem(Long id);

    Item updateName(Long id, String name);

    Item updateDescription(Long id, String name);
}
