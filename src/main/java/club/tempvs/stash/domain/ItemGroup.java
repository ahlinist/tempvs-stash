package club.tempvs.stash.domain;

import static javax.persistence.CascadeType.ALL;

import club.tempvs.stash.dto.ItemGroupDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
public class ItemGroup {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @OneToOne(cascade = ALL)
    private User owner;

    public ItemGroup(ItemGroupDto itemGroupDto) {
        this.name = itemGroupDto.getName();
        this.description = itemGroupDto.getDescription();
    }

    public ItemGroupDto toItemGroupDto() {
        return new ItemGroupDto(this);
    }
}
