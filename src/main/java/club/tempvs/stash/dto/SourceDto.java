package club.tempvs.stash.dto;

import club.tempvs.stash.model.Classification;
import club.tempvs.stash.model.Period;
import lombok.Data;

@Data
public class SourceDto {

    private Long id;
    private String name;
    private String description;
    private Classification classification;
    private Period period;
}
