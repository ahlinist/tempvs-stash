package club.tempvs.stash.dao;

import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long> {

    List<ItemGroup> findAllByOwner(User owner);
}
