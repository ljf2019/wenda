package com.nowcoder.async;

import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/25 0025.
 */
public interface EventHandler {
	void doHandle(EventModel eventModel);

	List<EventType> getSupportEventTypes();


}
