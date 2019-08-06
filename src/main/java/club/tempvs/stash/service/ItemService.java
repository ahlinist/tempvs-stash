package club.tempvs.stash.service;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ImageDto;

import java.util.List;

public interface ItemService {

    Item create(Long itemGroupId, Item item);

    List<Item> getItems(Long itemGroupId, int page, int size);

    Item getItem(Long id);

    Item updateName(Long id, String name);

    Item updateDescription(Long id, String name);

    void addImage(Long itemId, ImageDto imageDto);

    void deleteImage(Long itemId, String objectId);

    void delete(Long id);

    Item linkSource(Long itemId, Long sourceId);

    Item unlinkSource(Long itemId, Long sourceId);
}
