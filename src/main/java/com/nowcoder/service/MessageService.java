package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/20 0020.
 */
@Service
public class MessageService {

	@Autowired
	MessageDAO messageDAO;

	@Autowired
	SensitiveService sensitiveService;

	public int addMessage(Message message){
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDAO.addMessage(message) > 0? message.getId() : 0;
	}

	public List<Message> getConversationDetail(String conversationId, int offset, int limit){
		return messageDAO.getConversationDetail(conversationId, offset, limit);
	}

	public List<Message> getConversationList(int userId, int offset, int limit){
		return messageDAO.getConversationList(userId, offset, limit);
	}

	public int getConversationUnreadCount(int userId, String conversationId){
		return messageDAO.getConversationUnreadCount(userId, conversationId);
	}
}
