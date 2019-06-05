package club.tempvs.stash.dao;

import club.tempvs.stash.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
