package club.tempvs.stash.dao;

import club.tempvs.stash.domain.ItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long> {

    @Query("SELECT g FROM ItemGroup g WHERE g.owner.id = :userId")
    List<ItemGroup> findAllByUserId(@Param("userId") Long userId);
}
