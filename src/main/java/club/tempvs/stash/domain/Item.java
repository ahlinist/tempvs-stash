package club.tempvs.stash.domain;

import club.tempvs.stash.dto.ItemDto;
import club.tempvs.stash.model.Classification;
import club.tempvs.stash.model.Period;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Classification classification;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Period period;
    @OneToOne
    private ItemGroup itemGroup;
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
}
