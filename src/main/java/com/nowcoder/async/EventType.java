package com.nowcoder.async;

/**
 * Created by 刘佳凤 on 2019/3/25 0025.
 */
public enum EventType {
	LIKE(0),
	COMMENT(1),
	LOGIN(2),
	MAIL(3),
	FOLLOW(4),
	UNFOLLOW(5),
	ADD_QUESTION(6);

	private int value;
	EventType(int value){
		this.value = value;
	}
	public int getValue(){
		return value;
	}
}
