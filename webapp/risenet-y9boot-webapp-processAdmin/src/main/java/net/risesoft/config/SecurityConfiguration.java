package net.risesoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers("/**/*.{js,html}").antMatchers("/**");
	}
}