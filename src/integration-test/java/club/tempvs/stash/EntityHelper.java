package club.tempvs.stash;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.dao.UserRepository;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class EntityHelper {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ItemGroupRepository itemGroupRepository;

    public EntityHelper(ObjectMapper objectMapper, UserRepository userRepository, ItemGroupRepository itemGroupRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.itemGroupRepository = itemGroupRepository;
    }

    public String composeUserInfo(Long id, String name, String lang) throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(id);
        userInfoDto.setUserName(name);
        userInfoDto.setLang(lang);
        return objectMapper.writeValueAsString(userInfoDto);
    }

    public User createUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setUserName(name);
        return userRepository.saveAndFlush(user);
    }

    public ItemGroup createItemGroup(User user, String name, String description) {
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setName(name);
        itemGroup.setDescription(description);
        itemGroup.setOwner(user);
        return itemGroupRepository.saveAndFlush(itemGroup);
    }
}
