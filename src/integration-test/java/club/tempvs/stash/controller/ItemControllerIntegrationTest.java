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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

        mvc.perform(post("/api/group/" + itemGroup.getId() + "/item")
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
}
