package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/16 0016.
 */
@Mapper
public interface FeedDAO {
	String TABLE_NAME = "feed";
	String INSERT_FIELDS = "user_id, data, created_date, type";
	String SELECT_FIELDS = "id, " + INSERT_FIELDS;
	@Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
			") values(#{userId}, #{data}, #{createdDate}, #{type})"})
	int addFeed(Feed feed);

    /**
     * 推模式
     * @param id
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);


    /**
     * 拉模式
     * @param maxId
     * @param userIds
     * @param count
     * @return
     */
	List<Feed> selectUserFeeds(@Param("maxId") int maxId,
							   @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
