package cn.pipilu.plus.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * authorizeRequests(): 所有 spring security 全注解配置的开始，说明需要的权限.
         *                      需要的权限分为两部分，1、拦截的路径 2、访问该路径需要的权限
         * antMatchers()：表示拦截的什么路径
         * permitAll()：任何权限都可以访问，直接放行
         * anyRequest()：任何的请求
         * authenticated()：认证后才能访问
         * and().csrf().disable()：固定写法，表示使 csrf 攻击失效
         */
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
