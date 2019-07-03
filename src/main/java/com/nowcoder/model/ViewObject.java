package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 刘佳凤 on 2019/3/17 0017.
 */
public class ViewObject {
	private Map<String, Object> objs = new HashMap<>();

	public void set(String key, Object value){
		objs.put(key, value);
	}

	public Object get(String key){
		return objs.get(key);
	}
}
