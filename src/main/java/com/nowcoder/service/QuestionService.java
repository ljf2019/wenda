package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/17 0017.
 */
@Service
public class QuestionService {

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	SensitiveService sensitiveService;

	public Question getById(int id){
		return questionDAO.getById(id);
	}

	public int addQuestion(Question question){
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		//敏感词过滤
		question.setTitle(sensitiveService.filter(question.getTitle()));
		question.setContent(sensitiveService.filter(question.getContent()));
		//System.out.println(question.getTitle());

		return questionDAO.addQuestion(question)>0?question.getId():0;
	}

	public List<Question> getLatestQuestions(int userId, int offset, int limit){
		return questionDAO.selectLatestQuestions(userId, offset, limit);
	}

	public int updateCommentCount(int id, int count) {
		return questionDAO.updateCommentCount(id, count);
	}

}
