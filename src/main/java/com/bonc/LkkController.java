package com.bonc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LkkController {
	//确定路由规则，增加post filter匹配替换。AbstractUrlHandlerMapping，找到最优匹配
	//区内，异常
	@RequestMapping("/rr")
	public String redirect() {
		return "redirect:http://localhost:8763/login.html";
	}
	//区外，异常
	@RequestMapping("/rr2")
	public String redirect2() {
		return "redirect:https://www.baidu.com";
	}
	//区外，异常
	@RequestMapping("/rr3")
	public String redirect3() {
		return "redirect:https://cloud.spring.io/spring-cloud-static/spring-cloud-netflix/2.2.1.RELEASE/reference/html/#uploading-files-through-zuul";
	}
	//跳转
	@RequestMapping("/fw")
	public String forward() {
		return "forward:/test2";
	}
	//cookie传递
	@RequestMapping("/rrTest")
	@ResponseBody
	public String rrTest(HttpServletRequest hsr) {
		return "hello world,ltl";
	}
}
