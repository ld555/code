package me.chuang6.jz.service;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.chuang6.jz.bean.User;
import me.chuang6.jz.bean.UserExample;
import me.chuang6.jz.bean.UserExample.Criteria;
import me.chuang6.jz.dao.UserMapper;
import me.chuang6.jz.util.AESUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JedisPool jedisPool;

	public List<User> getList() {
		UserExample example = new UserExample();
		example.setOrderByClause("id desc");
		return userMapper.selectByExample(example);
	}

	public void add(User user) throws Exception {
		// 使用sha1生成openId
		user.setOpenid(DigestUtils.sha1Hex(new Date().toString()));
		user.setAddtime(new Date());
		userMapper.insert(user);
	}

	public String loginUser(String openid) {
		UserExample example = new UserExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andOpenidEqualTo(openid);
		long countByExample = userMapper.countByExample(example);

		// 登录成功
		if (countByExample > 0) {
			String uuid = AESUtils.encrypt(openid + "#" + new Random().nextInt(Integer.MAX_VALUE));

			// redis缓存
			Jedis jedis = jedisPool.getResource();
			jedis.select(1);
			jedis.set(openid, uuid);
			jedis.expire(openid, 3600 * 24 * 30);
			jedis.close();

			return uuid;
		}
		return null;
	}

	public int vaild(String uuid, String timestamp, String digest) {
		if (uuid == null || timestamp == null || digest == null) {
			return -1002;// 参数错误
		}
		int nowTime = (int) (System.currentTimeMillis() / 1000);
		if (nowTime - Integer.valueOf(timestamp) > 3) {
			return -1006;// 接口处理超时
		}
		// 判断摘要信息
		String key = "sschelper";
		String data = uuid + timestamp + key;
		String md5Hex = DigestUtils.md5Hex(data);
		if (!md5Hex.equals(digest)) {
			return -1003;// 摘要错误
		}
		String openid = AESUtils.decrypt(uuid).split("#")[0];
		Jedis jedis = jedisPool.getResource();
		jedis.select(1);
		String cacheUUID = jedis.get(openid);
		jedis.close();
		if (cacheUUID == null) {
			return -1004;// 登录过期
		}
		if (!uuid.equals(cacheUUID)) {
			return -1005;// 有其他人顶替登录
		}
		return 0;
	}

}
