package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/16 0016.
 */
@Controller
public class HomeController {

	private static  final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	UserService userService;

	@Autowired
	QuestionService questionService;

	@Autowired
	FollowService followService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	CommentService commentService;

	@RequestMapping(path={"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String userIndex(Model model, @PathVariable("userId") int userId){
		model.addAttribute("vos", getQuestions(userId, 0, 10));
		User user = userService.getUser(userId);
		ViewObject vo = new ViewObject();
		vo.set("user", user);
		vo.set("commentCount", commentService.getUserCommentCount(userId));
		vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
		if (hostHolder.getUser() != null) {
			vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
		} else {
			vo.set("followed", false);
		}
		model.addAttribute("profileUser", vo);
		return "profile";

	}


	@RequestMapping(path={"/","/index"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String index(Model model,@RequestParam(value="pop", defaultValue = "0") int pop){
		model.addAttribute("vos", getQuestions(0, 0, 10));
		return "index";
	}

	private List<ViewObject> getQuestions(int useId, int offset, int limit) {
		List<Question> questionList = questionService.getLatestQuestions(useId,offset,limit);
		List<ViewObject> vos = new ArrayList<>();
		for(Question question: questionList){
			ViewObject vo = new ViewObject();
			vo.set("question", question);
			vo.set("user", userService.getUser(question.getUserId()));
			vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
			vos.add(vo);
		}
		return vos;
	}
}
