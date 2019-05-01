package club.tempvs.stash.service.impl;

import club.tempvs.stash.dao.UserRepository;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
