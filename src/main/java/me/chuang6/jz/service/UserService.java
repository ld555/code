package me.chuang6.jz.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.chuang6.jz.bean.User;
import me.chuang6.jz.bean.UserExample;
import me.chuang6.jz.bean.UserExample.Criteria;
import me.chuang6.jz.dao.UserMapper;
import me.chuang6.jz.util.AESUtils;
import redis.clients.jedis.Jedis;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

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

	public String loginUser(String opidId) {
		UserExample example = new UserExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andOpenidEqualTo(opidId);
		long countByExample = userMapper.countByExample(example);

		// 登录成功
		if (countByExample > 0) {
			String uuid = AESUtils.encrypt(opidId);

			// redis缓存
			Jedis jedis = new Jedis("127.0.0.1", 6379);
			jedis.select(1);
			jedis.set(opidId, uuid);
			jedis.expire(opidId, 3600 * 24 * 30);
			jedis.close();
			
			return uuid;
		}
		return null;
	}

}
