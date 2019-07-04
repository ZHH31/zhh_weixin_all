package com.example.weixin.menu.controller;

import com.example.weixin.menu.domain.SelfMenu;
import com.example.weixin.menu.service.SelfMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/zhh_1/menu")
public class SelfMenuController {

	@Autowired
	private SelfMenuService selfMenuService;

	@GetMapping
	public ModelAndView index() {
		return new ModelAndView("/WEB-INF/views/menu/index.jsp");
	}

	@GetMapping(produces = "application/json") 
	@ResponseBody 
	public SelfMenu data() {
		return selfMenuService.getMenu();
	}

	@PostMapping
	@ResponseBody
	public String save(@RequestBody SelfMenu selfMenu) {
		this.selfMenuService.saveMenu(selfMenu);
		return "保存成功";
	}
}
