package club.tempvs.stash.controller;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.dto.ItemGroupDto;
import club.tempvs.stash.dto.StashDto;
import club.tempvs.stash.service.ItemGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public StashDto findGroupsByUserId(@RequestParam(required = false) Long userId) {
        return itemGroupService.getStash(userId);
    }

    @GetMapping("/{id}")
    public ItemGroupDto findGroupById(@PathVariable Long id) {
        return itemGroupService.getById(id)
                .toItemGroupDto();
    }

    @PatchMapping("/{id}/name")
    public ItemGroupDto updateName(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return itemGroupService.updateName(id, payload.get("name"))
                .toItemGroupDto();
    }

    @PatchMapping("/{id}/description")
    public ItemGroupDto updateDescription(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return itemGroupService.updateDescription(id, payload.get("description"))
                .toItemGroupDto();
    }
}
