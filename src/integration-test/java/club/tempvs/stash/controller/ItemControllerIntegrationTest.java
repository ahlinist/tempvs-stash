package club.tempvs.stash.controller;

import club.tempvs.stash.EntityHelper;
import club.tempvs.stash.domain.Item;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import club.tempvs.stash.model.Classification;
import club.tempvs.stash.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 8910, stubs = "classpath:/mappings/source")
public class ItemControllerIntegrationTest {

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    @Autowired
    private EntityHelper entityHelper;
    @Autowired
    private MockMvc mvc;

    @Test
    public void testCreate() throws Exception {
        Long userId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);

        File createItemFile = ResourceUtils.getFile("classpath:item/create.json");
        String createItemJson = new String(Files.readAllBytes(createItemFile.toPath()));
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/group/" + itemGroup.getId() + "/item")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createItemJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is("group name")))
                    .andExpect(jsonPath("description", is("group description")))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("period", is("ANTIQUITY")))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testGetItems() throws Exception {
        Long userId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String item1Name = "item 1 name";
        String item2Name = "item 2 name";
        String item1Desc = "item 1 desc";
        String item2Desc = "item 2 desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        entityHelper.createItem(itemGroup, item1Name, item1Desc, Classification.ARMOR, Period.ANCIENT);
        entityHelper.createItem(itemGroup, item2Name, item2Desc, Classification.WEAPON, Period.ANTIQUITY);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(get("/group/" + itemGroup.getId() + "/item")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("$[0].name", is(item1Name)))
                    .andExpect(jsonPath("$[0].description", is(item1Desc)))
                    .andExpect(jsonPath("$[0].classification", is("ARMOR")))
                    .andExpect(jsonPath("$[0].period", is("ANCIENT")))
                    .andExpect(jsonPath("$[0].itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("$[0].itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("$[0].itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("$[0].itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("$[0].itemGroup.owner.userName", is(userName)))
                    .andExpect(jsonPath("$[1].id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("$[1].name", is(item2Name)))
                    .andExpect(jsonPath("$[1].description", is(item2Desc)))
                    .andExpect(jsonPath("$[1].classification", is("WEAPON")))
                    .andExpect(jsonPath("$[1].period", is("ANTIQUITY")))
                    .andExpect(jsonPath("$[1].itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("$[1].itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("$[1].itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("$[1].itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("$[1].itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testGetItem() throws Exception {
        Long userId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String itemName = "item 1 name";
        String itemDesc = "item 1 desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDesc, Classification.ARMOR, Period.ANCIENT);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(get("/item/" + item.getId())
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is(itemName)))
                    .andExpect(jsonPath("description", is(itemDesc)))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("period", is("ANCIENT")))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testUpdateName() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String itemName = "item name";
        String itemDescription = "item desc";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String lang = "en";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDescription, Classification.ARMOR, Period.ANCIENT);
        Long itemId = item.getId();
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        File updateNameFile = ResourceUtils.getFile("classpath:item/update-name.json");
        String updateNameJson = new String(Files.readAllBytes(updateNameFile.toPath()));

        mvc.perform(patch("/item/" + itemId + "/name")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateNameJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is("new name")))
                    .andExpect(jsonPath("description", is(itemDescription)))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("period", is("ANCIENT")))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testUpdateDescription() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String itemName = "item name";
        String itemDescription = "item desc";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String lang = "en";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDescription, Classification.ARMOR, Period.ANCIENT);
        Long itemId = item.getId();
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        File updateDescriptionFile = ResourceUtils.getFile("classpath:item/update-description.json");
        String updateDescriptionJson = new String(Files.readAllBytes(updateDescriptionFile.toPath()));

        mvc.perform(patch("/item/" + itemId + "/description")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateDescriptionJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is(itemName)))
                    .andExpect(jsonPath("description", is("new description")))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("period", is("ANCIENT")))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testAddImage() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String itemName = "item name";
        String itemDescription = "item desc";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String lang = "en";
        File uploadImageFile = ResourceUtils.getFile("classpath:item/upload-image.json");
        String uploadImageFileJson = new String(Files.readAllBytes(uploadImageFile.toPath()));

        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDescription, Classification.ARMOR, Period.ANCIENT);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/item/" + item.getId() + "/images")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(uploadImageFileJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testDeleteImage() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String itemName = "item name";
        String itemDescription = "item desc";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String lang = "en";
        String objectId1 = "objectId1";

        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDescription, Classification.ARMOR, Period.ANCIENT);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(delete("/item/" + item.getId() + "/images/" + objectId1)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                     .andExpect(status().isOk());
    }

    @Test
    public void testDeleteItem() throws Exception {
        Long userId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String itemName = "item 1 name";
        String itemDesc = "item 1 desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDesc, Classification.ARMOR, Period.ANCIENT);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        //item is retrieved
        mvc.perform(get("/item/" + item.getId())
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is(itemName)))
                    .andExpect(jsonPath("description", is(itemDesc)))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("period", is("ANCIENT")))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));

        //item is deleted
        mvc.perform(delete("/item/" + item.getId())
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());

        //item can no longer be retrieved, 404 is returned
        mvc.perform(get("/item/" + item.getId())
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isNotFound());
    }

    @Test
    public void testLinkSource() throws Exception {
        Long userId = 1L;
        Long sourceId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String itemName = "item 1 name";
        String itemDesc = "item 1 desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDesc, Classification.CLOTHING, Period.ANTIQUITY);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/item/" + item.getId() + "/source/" + sourceId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id", isA(Integer.TYPE)))
                    .andExpect(jsonPath("name", is(itemName)))
                    .andExpect(jsonPath("description", is(itemDesc)))
                    .andExpect(jsonPath("classification", is("CLOTHING")))
                    .andExpect(jsonPath("period", is("ANTIQUITY")))
                    .andExpect(jsonPath("sources[0]", is(sourceId.intValue())))
                    .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                    .andExpect(jsonPath("itemGroup.name", is(groupName)))
                    .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                    .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }

    @Test
    public void testUnlinkSource() throws Exception {
        Long userId = 1L;
        Long sourceId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        String groupName = "my group";
        String groupDescription = "my group desc";
        String itemName = "item 1 name";
        String itemDesc = "item 1 desc";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Item item = entityHelper.createItem(itemGroup, itemName, itemDesc, Classification.CLOTHING, Period.ANTIQUITY);

        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/item/" + item.getId() + "/source/" + sourceId));

        mvc.perform(delete("/item/" + item.getId() + "/source/" + sourceId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", isA(Integer.TYPE)))
                .andExpect(jsonPath("name", is(itemName)))
                .andExpect(jsonPath("description", is(itemDesc)))
                .andExpect(jsonPath("classification", is("CLOTHING")))
                .andExpect(jsonPath("period", is("ANTIQUITY")))
                .andExpect(jsonPath("sources", hasSize(0)))
                .andExpect(jsonPath("itemGroup.id", is(itemGroup.getId().intValue())))
                .andExpect(jsonPath("itemGroup.name", is(groupName)))
                .andExpect(jsonPath("itemGroup.description", is(groupDescription)))
                .andExpect(jsonPath("itemGroup.owner.id", is(userId.intValue())))
                .andExpect(jsonPath("itemGroup.owner.userName", is(userName)));
    }
}
