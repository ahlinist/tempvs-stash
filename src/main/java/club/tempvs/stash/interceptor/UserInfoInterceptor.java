package club.tempvs.stash.interceptor;

import club.tempvs.stash.domain.User;
import club.tempvs.stash.dto.UserInfoDto;
import club.tempvs.stash.holder.UserHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {

    private static final String USER_INFO_HEADER = "User-Info";

    private final ObjectMapper objectMapper;
    private final UserHolder userHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userInfoHeaderValue = request.getHeader(USER_INFO_HEADER);
        response.setHeader(USER_INFO_HEADER, userInfoHeaderValue);

        try {
            UserInfoDto userInfoDto = objectMapper.readValue(userInfoHeaderValue, UserInfoDto.class);
            User user = userInfoDto.toUser();
            userHolder.setUser(user);
        } catch (Exception e) {
            //do nothing
        }

        return true;
    }
}
