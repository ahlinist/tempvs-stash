package club.tempvs.stash.controller;

import static java.util.stream.Collectors.toList;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ImageDto;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private static final int MAX_SIZE_VALUE = 40;

    private final ItemService itemService;

    @PostMapping("/group/{itemGroupId}/item")
    public ItemDto create(@PathVariable Long itemGroupId, @RequestBody ItemDto itemDto) {
        Item item = itemService.create(itemGroupId, itemDto.toItem());
        return item.toItemDto();
    }

    @GetMapping("/group/{itemGroupId}/item")
    public List<ItemDto> getItems(@PathVariable Long itemGroupId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @Max(MAX_SIZE_VALUE) @RequestParam(defaultValue = "40") int size) {
        List<Item> items = itemService.getItems(itemGroupId, page, size);
        return items.stream()
                .map(Item::toItemDto)
                .collect(toList());
    }

    @GetMapping("/item/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        Item item = itemService.getItem(itemId);
        return item.toItemDto();
    }

    @PatchMapping("/item/{id}/name")
    public ItemDto updateName(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return itemService.updateName(id, payload.get("name"))
                .toItemDto();
    }

    @PatchMapping("/item/{id}/description")
    public ItemDto updateDescription(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return itemService.updateDescription(id, payload.get("description"))
                .toItemDto();
    }

    @PostMapping("/item/{itemId}/images")
    public void addImage(@PathVariable Long itemId, @RequestBody ImageDto imageDto) {
        itemService.addImage(itemId, imageDto);
    }


    @DeleteMapping("/item/{itemId}/images/{objectId}")
    public void deleteImage(@PathVariable Long itemId, @PathVariable String objectId) {
        itemService.deleteImage(itemId, objectId);
    }

    @DeleteMapping("/item/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @PostMapping("/item/{itemId}/source/{sourceId}")
    public ItemDto linkSource(@PathVariable Long itemId, @PathVariable Long sourceId) {
        return itemService.linkSource(itemId, sourceId)
                .toItemDto();
    }

    @DeleteMapping("/item/{itemId}/source/{sourceId}")
    public ItemDto unlinkSource(@PathVariable Long itemId, @PathVariable Long sourceId) {
        return itemService.unlinkSource(itemId, sourceId)
                .toItemDto();
    }
}
