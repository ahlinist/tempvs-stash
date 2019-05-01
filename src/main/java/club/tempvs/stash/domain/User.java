package club.tempvs.stash.domain;

import club.tempvs.stash.dto.UserInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Locale;

@Data
@NoArgsConstructor
@Entity(name = "app_user")
public class User {

    @Id
    private Long id;
    @NotBlank
    private String userName;
    private transient Locale locale;

    public User(UserInfoDto userInfoDto) {
        this.id = userInfoDto.getUserId();
        this.userName = userInfoDto.getUserName();
        this.locale = new Locale(userInfoDto.getLang());
    }

    public UserInfoDto toUserInfoDto() {
        return new UserInfoDto(this);
    }
}
