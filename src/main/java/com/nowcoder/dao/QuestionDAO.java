package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by 刘佳凤 on 2019/3/16 0016.
 */
@Mapper
public interface QuestionDAO {
	String TABLE_NAME = "question";
	String INSERT_FIELDS = "title, content, created_date, user_id, comment_count";
	String SELECT_FIELDS = "id, " + INSERT_FIELDS;
	@Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
			") values(#{title}, #{content}, #{createdDate}, #{userId}, #{commentCount})"})
	int addQuestion(Question question);

	@Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
	Question getById(int id);


	List<Question> selectLatestQuestions(@Param("userId") int userId,
										@Param("offset") int offset,
										@Param("limit") int limit);

	@Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
	int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);


}
