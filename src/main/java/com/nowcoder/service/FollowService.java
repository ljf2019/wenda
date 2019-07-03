package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by 刘佳凤 on 2019/3/25 0025.
 */
@Service
public class FollowService {
	@Autowired
	JedisAdapter jedisAdapter;

	public boolean follow(int userId, int entityType, int entityId){
		String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		Date date = new Date();

		Jedis jedis = jedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zadd(followerKey,date.getTime(), String.valueOf(userId));
		tx.zadd(followeeKey, date.getTime(),String.valueOf(entityId));
		List<Object> ret = jedisAdapter.exec(tx,jedis);
		return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
	}

	public boolean unfollow(int userId, int entityType, int entityId){
		String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		Date date = new Date();

		Jedis jedis = jedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zrem(followerKey, String.valueOf(userId));
		tx.zrem(followeeKey, String.valueOf(entityId));
		List<Object> ret = jedisAdapter.exec(tx,jedis);
		return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
	}

	private List<Integer> getIdsFromSet(Set<String> idset){
		List<Integer> ids = new ArrayList<>();
		for(String str : idset){
			ids.add(Integer.parseInt(str));
		}
		return ids;
	}
	public List<Integer> getFollowers(int entityType, int entityId, int count){
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
	}

	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,count));
	}

	public List<Integer> getFollowees(int entityType, int entityId, int count){
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
	}

	public List<Integer> getFollowees(int entityType, int entityId, int offset, int count){
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
	}

	public long getFolloweeCount(int entityType, int entityId){
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return jedisAdapter.zcard(followeeKey);
	}

	public long getFollowerCount(int entityType, int entityId){
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
//		System.out.println(followerKey);
		return jedisAdapter.zcard(followerKey);
	}

	public boolean isFollower(int userId, int entityType, int entityId){
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return jedisAdapter.zscore(followeeKey, String.valueOf(userId))!= null;
	}


}
