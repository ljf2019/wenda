package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 刘佳凤 on 2019/3/24 0024.
 */
@Controller
public class LikeController {
	private static  final Logger logger = LoggerFactory.getLogger(LikeController.class);

	@Autowired
	HostHolder hostHolder;

	@Autowired
	LikeService likeService;

	@Autowired
	EventProducer eventProducer;

	@Autowired
	CommentService commentService;

	@RequestMapping(path={"/like"}, method = {RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("commentId") int commentId){
		if(hostHolder.getUser() == null){
			return WendaUtil.getJSONString(999);
		}

		Comment comment = commentService.getCommentById(commentId);

		eventProducer.fireEvent(new EventModel(EventType.LIKE)
				.setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
				.setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
				.setExt("questionId", String.valueOf(comment.getEntityId())));

		long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}

	@RequestMapping(path={"/dislike"}, method = {RequestMethod.POST})
	@ResponseBody
	public String dislike(@RequestParam("commentId") int commentId){
		if(hostHolder.getUser() == null){
			return WendaUtil.getJSONString(999);
		}

		long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}
}
