package club.tempvs.stash.dao;

import club.tempvs.stash.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByItemGroupId(Long itemGroupId, Pageable pageable);
}
