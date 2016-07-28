package com.tuxt.item.common;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.tuxt.item.bean.User;
import com.tuxt.item.util.RedisExpire;

public class UserManage {

	@Autowired
	protected RedisTemplate<Serializable, Serializable> redisTemplate;
	public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String getJointName(String sid) {
		String key = RedisExpire.ONLINEUSER + ":" + sid;//":"为文件夹形式
		return key;
	}
	/**
	 * 添加在线用户
	 * 
	 * @param sid 生成对用户的唯一id.即Session中的sessionid
	 *            source 为来源为后续app预留
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean addOnLinuUser(final String sid, final User user,
			final String source) throws Exception {
		if (user != null && sid.trim().length() > 0) {
			final String key;
			key = getJointName(sid);
			Boolean falg = redisTemplate.execute(new RedisCallback<Boolean>() {
				public Boolean doInRedis(RedisConnection connection)
						throws DataAccessException {
					/*这里是存入时,序列换操作,可选
					 * @SuppressWarnings("unchecked")
					 * RedisSerializer<User> valueSerializer =
					 * (RedisSerializer<User>) redisTemplate
					 * .getValueSerializer();
					 */
					connection.select(2);//切换redis的DB可以不需要,redis默认配置为0-15共16个库,可以通过这行代码实现切换
					connection.setEx(key.getBytes(),
							RedisExpire.ThirtyMinuteSecend,
							stringToByte(JSONObject.toJSONString(user)));//序列化采用了fastjson
					return true;
				}
			});

			return falg; 
		}
		return false;
	}
	protected byte[] stringToByte(String jsonString) {
		return jsonString.getBytes();
	}

	/**
	 * 移除在线登陆用户
	 * 
	 * @param sid
	 *            source 为来源为后续app预留
	 * @return
	 * @throws Exception
	 */
	public boolean removeOnLinuUser(final String sid, final String source)
			throws Exception {
		if (sid != null) {
			final String key;
			key = getJointName(sid);
			Boolean falg = redisTemplate.execute(new RedisCallback<Boolean>() {
				public Boolean doInRedis(RedisConnection connection)
						throws DataAccessException {
					connection.select(2);
					Long del = connection.del(key.getBytes());
					if (del == 1) {
						return true;
					} else {
						return false;
					}

				}
			});

			return falg;
		}
		return false;
	}



	/**
	 * 获取在线用户信息
	 * 
	 * @param sid
	 *            source 为来源为后续app预留
	 * @return
	 * @throws Exception
	 */
	public User getOnLinuUser(String sid, final String source)
			throws Exception {
		final String key;
		key = getJointName(sid);
		User userInfo = redisTemplate
				.execute(new RedisCallback<User>() {
					public User doInRedis(RedisConnection connection)
							throws DataAccessException {
						connection.select(2);
						byte[] bs = connection.get(key.getBytes());
						String byteToString = byteToString(bs);
						User userinfo = JSONObject.parseObject(
								byteToString, User.class);
						if (userinfo != null) {
							// 如果用户已登录，则增加在线时间
							connection.expire(key.getBytes(),
									RedisExpire.ThirtyMinuteSecend);

						}
						return userinfo;
					}
				});

		return userInfo;
	}

	protected String byteToString(byte[] bs) {
		if (bs!=null) {
			return new String(bs);
		}
		return null;
	}
}
