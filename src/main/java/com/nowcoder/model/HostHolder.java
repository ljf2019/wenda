package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by 刘佳凤 on 2019/3/18 0018.
 */
@Component
public class HostHolder {
	private static ThreadLocal<User> users = new ThreadLocal<User>();

	public User getUser(){
		return users.get();
	}

	public void setUser(User user){
		users.set(user);
	}

	public void clear(){
		users.remove();
	}
}
