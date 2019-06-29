package club.tempvs.stash;

import club.tempvs.stash.dao.ItemGroupRepository;
import club.tempvs.stash.dao.ItemRepository;
import club.tempvs.stash.dao.UserRepository;
import club.tempvs.stash.domain.Image;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.Item.Period;
import club.tempvs.stash.domain.Item.Classification;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityHelper {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemGroupRepository itemGroupRepository;

    public EntityHelper(ObjectMapper objectMapper,
                        UserRepository userRepository,
                        ItemRepository itemRepository,
                        ItemGroupRepository itemGroupRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
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

    public Item createItem(ItemGroup itemGroup, String name, String description, Classification classification, Period period) {
        return createItem(itemGroup, name, description, classification, period, new ArrayList<>());
    }

    public Item createItem(ItemGroup itemGroup, String name, String description, Classification classification, Period period, List<Image> images) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setClassification(classification);
        item.setPeriod(period);
        item.setItemGroup(itemGroup);
        item.setImages(images);
        return itemRepository.saveAndFlush(item);
    }
}
