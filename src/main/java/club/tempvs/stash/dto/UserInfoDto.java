package club.tempvs.stash.dto;

import club.tempvs.stash.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@Data
@NoArgsConstructor
public class UserInfoDto {

    private Long userId;
    private String userName;
    private String lang = "en";

    public UserInfoDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.lang = Optional.ofNullable(user.getLocale())
                .map(Locale::getLanguage)
                .orElse("");
    }

    public User toUser() {
        return new User(this);
    }
}
