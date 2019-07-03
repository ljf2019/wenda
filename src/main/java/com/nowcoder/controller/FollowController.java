package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by 刘佳凤 on 2019/3/19 0019.
 */
@Controller
public class FollowController {
	private static  final Logger logger = LoggerFactory.getLogger(FollowController.class);

	@Autowired
	HostHolder hostHolder;

	@Autowired
	CommentService commentService;

	@Autowired
	QuestionService questionService;

	@Autowired
	FollowService followService;

	@Autowired
	UserService userService;

	@Autowired
	EventProducer eventProducer;


	@RequestMapping(path={"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public String follow(@RequestParam("userId") int userId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
				.setActorId(hostHolder.getUser().getId()).setEntityId(userId)
				.setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
		return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
	}

	@RequestMapping(path={"/unfollowUser"}, method = {RequestMethod.POST})
	@ResponseBody
	public String unfollow(@RequestParam("userId") int userId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
		eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
				.setActorId(hostHolder.getUser().getId()).setEntityId(userId)
				.setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
		return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
	}

	@RequestMapping(path={"/followQuestion"}, method = {RequestMethod.POST})
	@ResponseBody
	public String followQuestion(@RequestParam("questionId") int questionId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		Question q = questionService.getById(questionId);
		if(q == null){
			return WendaUtil.getJSONString(1,"问题不存在");
		}
		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
				.setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
				.setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

		Map<String, Object> info = new HashMap<String,Object>();
		info.put("headUrl",hostHolder.getUser().getHeadUrl());
		info.put("name", hostHolder.getUser().getName());
		info.put("id",hostHolder.getUser().getId());
		info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION,questionId));

		return WendaUtil.getJSONString(ret ? 0 : 1, info);
	}

	@RequestMapping(path={"/unfollowQuestion"}, method = {RequestMethod.POST})
	@ResponseBody
	public String unfollowQuestion(@RequestParam("questionId") int questionId) {
		if (hostHolder.getUser() == null) {
			return WendaUtil.getJSONString(999);
		}

		Question q = questionService.getById(questionId);
		if(q == null){
			return WendaUtil.getJSONString(1,"问题不存在");
		}

		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
		eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
				.setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
				.setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

		Map<String, Object> info = new HashMap<String,Object>();
		info.put("headUrl",hostHolder.getUser().getHeadUrl());
		info.put("name", hostHolder.getUser().getName());
		info.put("id",hostHolder.getUser().getId());
		info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION,questionId));
		return WendaUtil.getJSONString(ret ? 0 : 1, info);
	}

	@RequestMapping(path={"/user/{uid}/followees"}, method = {RequestMethod.GET})
	public String followees(Model model, @PathVariable("uid") int userId) {
		List<Integer> followeesIds = followService.getFollowees(userId, EntityType.ENTITY_USER,0,10);
		if(hostHolder.getUser() != null){
			model.addAttribute("followees",getUsersInfo(hostHolder.getUser().getId(), followeesIds));
		}else{
			model.addAttribute("followees",getUsersInfo(0, followeesIds));
		}
		model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followees";
	}

	@RequestMapping(path={"/user/{uid}/followers"}, method = {RequestMethod.GET})
	public String followers(Model model, @PathVariable("uid") int userId) {
		List<Integer> followersIds = followService.getFollowers(EntityType.ENTITY_USER,userId,0,10);
		if(hostHolder.getUser() != null){
			model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(), followersIds));
		}else{
			model.addAttribute("followers",getUsersInfo(0, followersIds));
		}
//		System.out.println(userId);
		model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followers";
	}

	private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds){
		List<ViewObject> userInfos = new ArrayList<>();
		for(Integer uid:userIds){
			User user = userService.getUser(uid);
			if(user == null){
				continue;
			}

			ViewObject vo = new ViewObject();
			vo.set("user", user);
			vo.set("commentCount", commentService.getUserCommentCount(uid));
			vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
			vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
			if(localUserId != 0){
				vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER,uid));
			}else{
				vo.set("followed",false);
			}
			userInfos.add(vo);
		}
		return userInfos;
	}
}
