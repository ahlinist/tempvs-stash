package club.tempvs.stash.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.stash.dao.UserRepository;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private User user;
    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testSave() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);

        assertEquals("User object is returned", user, result);
    }
}
