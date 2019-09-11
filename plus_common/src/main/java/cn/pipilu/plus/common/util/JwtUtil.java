package cn.pipilu.plus.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;


@ConfigurationProperties("spring.jwt.config")
@Data
public class JwtUtil {

    private String key ;

    private long ttl ;//一个小时


    /**
     * 生成JWT
     *
     * @param id
     * @param subject
     * @return
     */
    public String createJWT(String id, String subject, String roles) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder().setId(id)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key).claim("roles", roles);
        if (ttl > 0) {
            builder.setExpiration(new Date(now.getTime() + ttl));
        }
        return builder.compact();
    }

    /**
     * 解析JWT
     * @param jwt
     * @return
     */
    public Claims parseJWT(String jwt){
        return  Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt)
                .getBody();
    }

}
