package com.bonc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ZuulClient01 {
	@Value("${server.port}")
	String port;
	@RequestMapping("/test")
	public String getInfo() {
		return String.format("My port is %1s", port);
	}
	@RequestMapping("/test2")
	public String test2() {
		return String.format("test2", port);
	}

	@RequestMapping("/test3")
	public String test3() {
		return String.format("test3");
	}
	public static void main(String[] args) {
		SpringApplication.run(ZuulClient01.class, args);
	}

}
