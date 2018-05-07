package com.web.manager.controller;

import com.alibaba.fastjson.JSON;
import com.sweet.gen.mapper.tm.AdminUserMapper;
import com.sweet.gen.model.tm.AdminUser;
import com.sweet.util.support.StringUtil;
import com.web.manager.config.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {


    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private RedisClient redisClinet;

    @RequestMapping(value="/test")
    public Map<String,Object> test(){
        Map<String,Object> mp = new HashMap<>();
        mp.put("code",1000);
        mp.put("msg","电子猪系统即将上线");
        return  mp;
    }

    @RequestMapping(value="/user")
    public List<AdminUser> queryList(){
        return adminUserMapper.selectByExample(null);
    }


    //redis添加用户
    @RequestMapping(value = "/user/save")
    public  AdminUser addUser(Integer id) throws Exception{
        if(null == id){
            return null;
        }
        AdminUser user = adminUserMapper.selectByPrimaryKey(id);
        String us = JSON.toJSONString(user);
        redisClinet.set(user.getId()+"", us);
        return user;
    }

    //redis获取用户
    @RequestMapping(value = "/user/query")
    public  Object queryUser(String key) throws Exception{
        if(StringUtil.isBlank(key)){
            return null;
        }
        String userStr = redisClinet.get(key);
        if(StringUtil.isBlank(userStr)){
            return null;
        }
        //String userStr1= "{id:2, userName:'sweet22', password:'e10adc3949ba59abbe56e057f20f883e', name:'sadasdasd', mobile:'null', email:'null', status:1, isDelete:0, createTime:'2017-08-08'}";
        return JSON.parse(userStr);
    }

}
