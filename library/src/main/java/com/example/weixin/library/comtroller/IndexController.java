package com.example.weixin.library.comtroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.weixin.library.domain.Book;
import com.example.weixin.library.service.LibraryService;

@Controller
@RequestMapping("/zhh_1/library")
public class IndexController {

	@Autowired
	private LibraryService libraryService;
	
	@RequestMapping
	public String index(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber, Model model
	) {
		
		Page<Book> page = this.libraryService.search(keyword, pageNumber);
		model.addAttribute("page", page);
		return "/WEB-INF/views/library/index.jsp";
	}
}
