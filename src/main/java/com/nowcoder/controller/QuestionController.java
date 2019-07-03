package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/18 0018.
 */
@Controller
public class QuestionController {
	private static  final Logger logger = LoggerFactory.getLogger(QuestionController.class);

	@Autowired
	QuestionService questionService;

	@Autowired
	HostHolder hostHolder;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	UserService userService;

	@Autowired
	CommentService commentService;

	@Autowired
	LikeService likeService;

	@Autowired
	FollowService followService;

	@Autowired
	EventProducer eventProducer;


	/**
	 * 写数据时，所以用post请求，我们现在返回的是一个AJAX请求，
	 * @param title
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/question/add", method = {RequestMethod.POST})
	@ResponseBody //返回一个Jason串
	public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
		try{
			Question question = new Question();
			question.setContent(content);
			question.setTitle(title);
			question.setCreatedDate(new Date());
			question.setCommentCount(0);
			if(hostHolder.getUser() == null){
//				question.setUserId(WendaUtil.ANONYMOUS_USERID);
				return WendaUtil.getJSONString(999);
			}else{
				question.setUserId(hostHolder.getUser().getId());
			}

			if(questionService.addQuestion(question)>0){
				eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setActorId(question.getUserId())
				.setEntityId(question.getId()).setExt("title",question.getTitle()).setExt("content",question.getContent()));
				return WendaUtil.getJSONString(0);
			}
		}catch (Exception e){
			logger.error("增加题目失败" + e.getMessage());
		}
		return WendaUtil.getJSONString(1,"失败");
	}

	@RequestMapping(value = "/question/{qid}")
	public String questionDetail(Model model,
								 @PathVariable("qid") int qid){
		Question question = questionService.getById(qid);
		model.addAttribute("question", question);
		model.addAttribute("user", userService.getUser(question.getUserId()));

		List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
		List<ViewObject> comments = new ArrayList<ViewObject>();
		for(Comment comment : commentList){
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			if(hostHolder.getUser() == null){
				vo.set("liked", 0);
			}else{
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT, comment.getId()));
			}

			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("user", userService.getUser(comment.getUserId()));
			comments.add(vo);
		}

		model.addAttribute("comments", comments);

		List<ViewObject> followUsers = new ArrayList<ViewObject>();
		// 获取关注的用户信息
		List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
		for (Integer userId : users) {
			ViewObject vo = new ViewObject();
			User u = userService.getUser(userId);
			if (u == null) {
				continue;
			}
			vo.set("name", u.getName());
			vo.set("headUrl", u.getHeadUrl());
			vo.set("id", u.getId());
			followUsers.add(vo);
		}
		model.addAttribute("followUsers", followUsers);
		if (hostHolder.getUser() != null) {
			model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
		} else {
			model.addAttribute("followed", false);
		}

		return "detail";
	}
}
