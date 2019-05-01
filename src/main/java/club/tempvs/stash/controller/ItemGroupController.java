package club.tempvs.stash.controller;

import static java.util.stream.Collectors.toList;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ItemGroupDto;
import club.tempvs.stash.service.ItemGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class ItemGroupController {

    private final ItemGroupService itemGroupService;

    @PostMapping
    public ItemGroupDto create(@RequestBody ItemGroupDto itemGroupDto) {
        ItemGroup itemGroup = itemGroupService.create(itemGroupDto.toGroup());
        return itemGroup.toItemGroupDto();
    }

    @GetMapping
    public List<ItemGroupDto> findGroupsByUserId(@RequestParam Long userId) {
        return itemGroupService.findAllByUserId(userId).stream()
                .map(ItemGroup::toItemGroupDto)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public ItemGroupDto findGroupById(@PathVariable Long id) {
        return itemGroupService.getById(id)
                .toItemGroupDto();
    }
}
