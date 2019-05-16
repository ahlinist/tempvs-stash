package club.tempvs.stash.dto;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

@Data
@NoArgsConstructor
public class ItemGroupDto {

    private Long id;
    private String name;
    private String description;
    private User owner;

    public ItemGroupDto(ItemGroup itemGroup) {
        this.id = itemGroup.getId();
        this.name = itemGroup.getName();
        this.description = itemGroup.getDescription();
        this.owner = itemGroup.getOwner();
    }

    public ItemGroup toGroup() {
        return new ItemGroup(this);
    }
}
