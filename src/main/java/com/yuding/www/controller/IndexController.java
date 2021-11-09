package com.yuding.www.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping("/index")
	public String index() {
		return "index";
	}
	
	@RequestMapping("/index2")
	public String index2() {
		return "index2";
	}
	
	@RequestMapping("/index3")
	public String index3() {
		return "index3";
	}
	
	@RequestMapping("/index4")
	public String index4() {
		return "index4";
	}
	
	@RequestMapping("/index5")
	public String index5() {
		return "index5";
	}
	
}
