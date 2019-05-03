package club.tempvs.stash.dto;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class StashDto {

    private User owner;
    private List<ItemGroupDto> groups;

    public StashDto(User owner, List<ItemGroup> groups) {
        this.owner = owner;
        this.groups = groups.stream()
                .map(ItemGroup::toItemGroupDto)
                .collect(toList());
    }
}
