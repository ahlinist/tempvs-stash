package club.tempvs.stash.controller;

import club.tempvs.stash.EntityHelper;
import club.tempvs.stash.domain.ItemGroup;
import club.tempvs.stash.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemGroupControllerIntegrationTest {

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
        entityHelper.createUser(userId, userName);
        File createGroupFile = ResourceUtils.getFile("classpath:group/create.json");
        String createGroupJson = new String(Files.readAllBytes(createGroupFile.toPath()));
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/api/group")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createGroupJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is("group name")))
                    .andExpect(jsonPath("description", is("group description")))
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)));
    }

    @Test
    public void testCreateForInvalidInput() throws Exception {
        Long userId = 1L;
        String userName = "Name Surname";
        String lang = "en";
        entityHelper.createUser(userId, userName);
        File createGroupFile = ResourceUtils.getFile("classpath:group/create-invalid.json");
        String createGroupJson = new String(Files.readAllBytes(createGroupFile.toPath()));
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(post("/api/group")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createGroupJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.name", is("Group name can't be blank")));
    }

    @Test
    public void testFindAllByUserId() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String group1Name = "group 1 name";
        String group1Description = "group 1 desc";
        String group2Name = "group 2 name";
        String group2Description = "group 2 desc";
        User user = entityHelper.createUser(userId, userName);
        entityHelper.createItemGroup(user, group1Name, group1Description);
        entityHelper.createItemGroup(user, group2Name, group2Description);
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, "en");

        mvc.perform(get("/api/group?userId=" + userId)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)))
                    .andExpect(jsonPath("groups[0].name", is("group 1 name")))
                    .andExpect(jsonPath("groups[0].description", is("group 1 desc")))
                    .andExpect(jsonPath("groups[0].owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("groups[0].owner.userName", is(userName)))
                    .andExpect(jsonPath("groups[1].name", is("group 2 name")))
                    .andExpect(jsonPath("groups[1].description", is("group 2 desc")))
                    .andExpect(jsonPath("groups[1].owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("groups[1].owner.userName", is(userName)));
    }

    @Test
    public void testFindAllByUserIdForMissingId() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String group1Name = "group 1 name";
        String group1Description = "group 1 desc";
        String group2Name = "group 2 name";
        String group2Description = "group 2 desc";
        User user = entityHelper.createUser(userId, userName);
        entityHelper.createItemGroup(user, group1Name, group1Description);
        entityHelper.createItemGroup(user, group2Name, group2Description);
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, "en");

        mvc.perform(get("/api/group")
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)))
                    .andExpect(jsonPath("groups[0].name", is("group 1 name")))
                    .andExpect(jsonPath("groups[0].description", is("group 1 desc")))
                    .andExpect(jsonPath("groups[0].owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("groups[0].owner.userName", is(userName)))
                    .andExpect(jsonPath("groups[1].name", is("group 2 name")))
                    .andExpect(jsonPath("groups[1].description", is("group 2 desc")))
                    .andExpect(jsonPath("groups[1].owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("groups[1].owner.userName", is(userName)));
    }

    @Test
    public void testGetById() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String groupName = "group name";
        String groupDescription = "group desc";
        String lang = "en";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Long groupId = itemGroup.getId();
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        mvc.perform(get("/api/group/" + groupId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is(groupName)))
                    .andExpect(jsonPath("description", is(groupDescription)))
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)));
    }

    @Test
    public void testUpdateName() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String groupName = "group name";
        String groupDescription = "group desc";
        String lang = "en";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Long groupId = itemGroup.getId();
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        File updateNameFile = ResourceUtils.getFile("classpath:group/update-name.json");
        String updateNameJson = new String(Files.readAllBytes(updateNameFile.toPath()));

        mvc.perform(patch("/api/group/" + groupId + "/name")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateNameJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is("new name")))
                    .andExpect(jsonPath("description", is(groupDescription)))
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)));
    }

    @Test
    public void testUpdateDescription() throws Exception {
        Long userId = 1L;
        String userName = "Tony Stark";
        String groupName = "group name";
        String groupDescription = "group desc";
        String lang = "en";
        User user = entityHelper.createUser(userId, userName);
        ItemGroup itemGroup = entityHelper.createItemGroup(user, groupName, groupDescription);
        Long groupId = itemGroup.getId();
        String userInfoValue = entityHelper.composeUserInfo(userId, userName, lang);

        File updateDescriptionFile = ResourceUtils.getFile("classpath:group/update-description.json");
        String updateDescriptionJson = new String(Files.readAllBytes(updateDescriptionFile.toPath()));

        mvc.perform(patch("/api/group/" + groupId + "/description")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateDescriptionJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is(groupName)))
                    .andExpect(jsonPath("description", is("new description")))
                    .andExpect(jsonPath("owner.id", is(userId.intValue())))
                    .andExpect(jsonPath("owner.userName", is(userName)));
    }
}
