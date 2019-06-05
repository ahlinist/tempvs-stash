package club.tempvs.stash.dto;

import club.tempvs.stash.domain.Image;
import club.tempvs.stash.domain.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private String classification;
    private String period;
    private ItemGroupDto itemGroup;
    private List<Image> images = new ArrayList<>();
    private Set<Long> sources = new HashSet<>();

    public ItemDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.classification = item.getClassification().name();
        this.period = item.getPeriod().name();
        this.itemGroup = item.getItemGroup().toItemGroupDto();
        this.images = item.getImages();
        this.sources = item.getSources();
    }

    public Item toItem() {
        return new Item(this);
    }
}
