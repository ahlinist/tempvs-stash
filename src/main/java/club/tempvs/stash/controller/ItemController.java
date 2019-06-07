package club.tempvs.stash.controller;

import static java.util.stream.Collectors.toList;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/{itemGroupId}/item")
    public ItemDto create(@PathVariable Long itemGroupId, @RequestBody ItemDto itemDto) {
        Item item = itemService.create(itemGroupId, itemDto.toItem());
        return item.toItemDto();
    }

    @GetMapping("/{itemGroupId}/item")
    public List<ItemDto> getItems(@PathVariable Long itemGroupId) {
        List<Item> items = itemService.getItems(itemGroupId);
        return items.stream()
                .map(Item::toItemDto)
                .collect(toList());
    }
}
