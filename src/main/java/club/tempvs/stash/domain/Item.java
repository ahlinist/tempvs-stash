package club.tempvs.stash.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
