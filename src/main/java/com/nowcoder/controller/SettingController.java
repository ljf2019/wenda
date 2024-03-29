package com.nowcoder.controller;

import com.nowcoder.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 刘佳凤 on 2019/3/15 0015.
 */
@Controller
public class SettingController {
	@Autowired
	WendaService wendaService;

	@RequestMapping(path = {"/setting"}, method = {RequestMethod.GET})
	@ResponseBody
	public String setting(HttpSession httpSession){
		return "Setting OK." + wendaService.getMessage(1);
	}
}
