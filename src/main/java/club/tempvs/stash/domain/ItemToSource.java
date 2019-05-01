package club.tempvs.stash.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ItemToSource {

    @Id
    @GeneratedValue
    private Long id;
    private Long itemId;
    private Long sourceId;
}
