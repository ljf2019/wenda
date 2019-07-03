package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 刘佳凤 on 2019/3/17 0017.
 */
@Service
public class UserService {
	@Autowired
	UserDAO userDAO;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	public User selectByName(String name) {
		return userDAO.selectByName(name);
	}

	public Map<String, Object> register(String username, String password){
		Map<String, Object> map = new HashMap<String, Object>();

		if(StringUtils.isBlank(username)){
			map.put("msg","用户名不能为空");
			return map;
		}

		if(StringUtils.isBlank(password)){
			map.put("msg","密码不能为空");
			return map;
		}

		User user = userDAO.selectByName(username);
		if(user != null){
			map.put("msg", "用户名已经被注册");
			return map;
		}

		user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0,5));
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setPassword(WendaUtil.MD5(password + user.getSalt()));
		userDAO.addUser(user);

		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);

		return map;
	}

	public Map<String, Object> login(String username, String password){
		Map<String, Object> map = new HashMap<String, Object>();

		if(StringUtils.isBlank(username)){
			map.put("msg","用户名不能为空");
			return map;
		}

		if(StringUtils.isBlank(password)){
			map.put("msg","密码不能为空");
			return map;
		}

		User user = userDAO.selectByName(username);
		if(user == null){
			map.put("msg", "用户名不存在");
			return map;
		}

		if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
			map.put("msg", "密码错误");
			return map;
		}

		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		map.put("userId", user.getId());

		return map;
	}

	private String addLoginTicket(int userId){
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setUserId(userId);
//		Date now = new Date();
//		now.setTime(3600*24*100 + now.getTime());
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String nowTime = sdf.format(now);
//		java.sql.Time dates = new java.sql.Time(now.getTime());

//		loginTicket.setExpired(dates);
		Date date = new Date();
		date.setTime(date.getTime() + 1000*3600*24);
		loginTicket.setExpired(date);
		loginTicket.setStatus(0);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
		loginTicketDAO.addTicket(loginTicket);
		return loginTicket.getTicket();
	}

	public User getUser(int id){
		return userDAO.selectById(id);
	}

	public void logout(String ticket){
		loginTicketDAO.updateStatus(ticket,1);
	}
}
