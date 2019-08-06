package club.tempvs.stash.domain;

import club.tempvs.stash.dto.UserInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Entity(name = "app_user")
public class User {

    @Id
    private Long id;
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String userName;

    public User(UserInfoDto userInfoDto) {
        this.id = userInfoDto.getUserId();
        this.userName = userInfoDto.getUserName();
    }
}
