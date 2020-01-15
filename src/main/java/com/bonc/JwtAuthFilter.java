//package com.bonc;
//
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.List;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.GenericFilterBean;
//
//public class JwtAuthFilter extends GenericFilterBean {
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		// 根据jwt信息验证
//		String jwtToken=request.getParameter("jwtToken");
//		boolean flag=jwtToken!=null;
//		if(!flag) {
//			Cookie[] cookies=((HttpServletRequest)request).getCookies();
//			if(cookies!=null) {
//				for(Cookie cookie : cookies) {
//					if(cookie.getName().equals("ltl")) {
//						flag=true;
////						System.out.println("ltl");
//					}
//				}
//			}
//		}
//		if(flag) {
//			Authentication auth=new UsernamePasswordAuthenticationToken("ltl","lkk");
//			SecurityContextHolder.getContext().setAuthentication(auth);
//		}
//		chain.doFilter(request, response);
//		//清除
//		if(flag) {
//			SecurityContextHolder.getContext().setAuthentication(null);
//		}
//	}
//
//}
