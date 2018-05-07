package com.web.manager;

import org.springframework.boot.CommandLineRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InitCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... var1) throws Exception{
        //项目运行初始化数据
        Map<String,Object> map = new HashMap<>();
        map.put("code",1000);
        map.put("msg","项目是这个时候启动的————"+new Date());
        System.out.print(map.get("msg"));
    }
}
