package cn.pipilu.plus.common.iterceptor;

import cn.pipilu.plus.common.exception.AppException;
import cn.pipilu.plus.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.err.println("经过jwt 拦截器");
        //拦截器无论如何都放行，这里只对有 authorization 的信息解析
        String authorization = request.getHeader("authorization");
        if (StringUtils.isNotBlank(authorization)){
            if (authorization.startsWith("Bearer ")){
                String token = authorization.substring(7);
                try {
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    if (Objects.equals("admin",roles)){
                        request.setAttribute("claims_admin",token);
                    }
                    if (Objects.equals("user",roles)){
                        request.setAttribute("claims_user",token);
                    }
                }catch (Exception e){
                    throw new AppException("1003","令牌不合法");
                }
            }
        }
        return true;
    }
}
