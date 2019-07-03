package com.nowcoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by 刘佳凤 on 2019/3/15 0015.
 */
@Service
public class WendaService {
	public String getMessage(int userId){
		return "Hello Message" + String.valueOf(userId);
	}
}
