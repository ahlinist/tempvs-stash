package club.tempvs.stash.controller;

import static java.util.stream.Collectors.toList;

import club.tempvs.stash.domain.Item;
import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class ItemController {

    private static final int MAX_SIZE_VALUE = 40;

    private final ItemService itemService;

    @PostMapping("/{itemGroupId}/item")
    public ItemDto create(@PathVariable Long itemGroupId, @RequestBody ItemDto itemDto) {
        Item item = itemService.create(itemGroupId, itemDto.toItem());
        return item.toItemDto();
    }

    @GetMapping("/{itemGroupId}/item")
    public List<ItemDto> getItems(@PathVariable Long itemGroupId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @Max(MAX_SIZE_VALUE) @RequestParam(defaultValue = "40") int size) {
        List<Item> items = itemService.getItems(itemGroupId, page, size);
        return items.stream()
                .map(Item::toItemDto)
                .collect(toList());
    }
}
