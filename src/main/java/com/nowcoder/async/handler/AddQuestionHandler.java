package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.controller.QuestionController;
import com.nowcoder.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author JiafengLiu
 * @date 2019/7/2 19:31
 * Email jfliu_2017@stu.xidian.edu.cn
 */
@Component
public class AddQuestionHandler implements EventHandler {
	private static  final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

	@Autowired
	SearchService searchService;

	@Override
	public void doHandle(EventModel model) {
		try{
			searchService.indexQuestion(model.getEntityId(),
					model.getExt("title"),model.getExt("content"));
		}catch (Exception e){
			logger.error("增加题目索引失败");
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.ADD_QUESTION);
	}

}
