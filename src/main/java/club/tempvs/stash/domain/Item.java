package club.tempvs.stash.domain;

import club.tempvs.stash.dto.ItemDto;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Classification classification;
    @NotNull
    private Period period;
    @OneToOne
    private ItemGroup itemGroup;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> sources = new HashSet<>();

    public Item(ItemDto itemDto) {
        this.name = itemDto.getName();
        this.description = itemDto.getDescription();

        try {
            this.classification = Classification.valueOf(itemDto.getClassification());
        } catch (Exception e) {
            //do nothing
        }

        try {
            this.period = Period.valueOf(itemDto.getPeriod());
        } catch (Exception e) {
            //do nothing
        }
    }

    public ItemDto toItemDto() {
        return new ItemDto(this);
    }

    public enum Classification {

        CLOTHING,
        FOOTWEAR,
        HOUSEHOLD,
        WEAPON,
        ARMOR,
        OTHER
    }

    public enum Period {

        ANCIENT,
        ANTIQUITY,
        EARLY_MIDDLE_AGES,
        HIGH_MIDDLE_AGES,
        LATE_MIDDLE_AGES,
        RENAISSANCE,
        MODERN,
        WWI,
        WWII,
        CONTEMPORARY,
        OTHER
    }
}
