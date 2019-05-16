package club.tempvs.stash.dto;

import club.tempvs.stash.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDto {

    private Long userId;
    private String userName;
    private String lang = "en";

    public User toUser() {
        return new User(this);
    }
}
