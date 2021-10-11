package com.example.springbootchocourse.service;

import com.example.springbootchocourse.bean.User;
import com.example.springbootchocourse.exception.GlobalException;
import com.example.springbootchocourse.mapper.UserMapper;
import com.example.springbootchocourse.redis.RedisService;
import com.example.springbootchocourse.redis.UserKey;
import com.example.springbootchocourse.result.CodeMsg;
import com.example.springbootchocourse.result.Result;
import com.example.springbootchocourse.util.MD5Util;
import com.example.springbootchocourse.util.UUIDUtil;
import com.example.springbootchocourse.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    RedisService redisService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String login(LoginVo loginVo){
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String id = loginVo.getId();
        String formPass = loginVo.getPassword();
        User user = getById(Long.parseLong(id));
        if(user == null){
            throw new GlobalException(CodeMsg.ID_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成唯一id作为token
//        String token = UUIDUtil.uuid();
//        return token;
        return user.getRole();
    }

    public User getById(long id) {
        //对象缓存
        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = userMapper.getById(id);
        //再存入缓存
        if (user != null) {
            redisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    public Result editLimit(int limit){
        int res = userMapper.editLimit(limit);
        if(res < 0){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
//        redisService.delete(UserKey.getById, "" + id)
        //删除redis中原先设置的键值对，用户重新登录更新缓存
        Set<String> keysList = stringRedisTemplate.keys("UserKey:" + "*");
        stringRedisTemplate.delete(keysList);
        return Result.success("success");
    }
}
