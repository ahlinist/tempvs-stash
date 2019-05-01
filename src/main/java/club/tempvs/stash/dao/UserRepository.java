package club.tempvs.stash.dao;

import club.tempvs.stash.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
