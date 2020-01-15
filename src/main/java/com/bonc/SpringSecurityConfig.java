//package com.bonc;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//@EnableWebSecurity//启用安全配置
//@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled=true)//启用权限
//@Configuration
//public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
//	@Value("${spring.security.enabled:true}")
//	String enable;
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		//禁用csrf
//		http.csrf().disable();
//		//放行h2
//		http.headers().frameOptions().disable();
//		//开启验证
//		if(Boolean.valueOf(enable)) {
// 			http.httpBasic()//开启http basic登录
////				.and()//endpoint安全配置，要求必须为admin角色
////				.requestMatcher(EndpointRequest.toAnyEndpoint())
////				.authorizeRequests()
////				.anyRequest().permitAll()//.hasRole("admin")
//				.and()//controller安全配置
//				.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
//				.authorizeRequests()
//				.antMatchers("/test","/test3")
//				.authenticated()
//				.anyRequest().permitAll()
//				.and()//自动构建登录界面，允许所有访问
//				.formLogin().defaultSuccessUrl("/test")
//				.loginPage("/login.html").loginProcessingUrl("/login") //.formLogin().loginPage()用于指定自定义的多路页面路径
//				.and()//登出，清除JSESSIONID
//				.logout().permitAll().deleteCookies("JSESSIONID");
//		}else {
//			http.authorizeRequests().anyRequest().permitAll()
//				.and().csrf().disable();
//		}
//	}
//	/*
//	 * 托管AuthenticationManager
//	 * (non-Javadoc)
//	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#authenticationManagerBean()
//	 */
//	@Bean
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//	/**
//	 * 加密策略，托管给spring，便于保存用户时加密密码
//	 * encode()，加密密码
//	 * @return
//	 */
//	@Bean
//	public PasswordEncoder passwordEncoder(){
//		//不加密，已过时
////		return NoOpPasswordEncoder.getInstance();
//		return new BCryptPasswordEncoder();
//	}
//	@Bean
//	public JwtAuthFilter jwtAuthFilter() {
//		return new JwtAuthFilter();
//	}
//	/**
//	 * 配置本地验证服务，必须设置username、password、authorities
//	 * @return 
//	 * @throws Exception 
//	 */
//	@Override
//	  public void configure(AuthenticationManagerBuilder builder) throws Exception {
//	    builder.inMemoryAuthentication()
//		    .withUser("ltl")
//		    .password(passwordEncoder().encode("lkk"))
//		    .authorities("ADMIN");
//	  }
//	
//}
