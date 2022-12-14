package com.raycodingx.serviceverificationcode.controller;

import com.raycodingx.internalcommon.dto.ResponseResult;
import com.raycodingx.internalcommon.response.NumberCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeController {
    @GetMapping("/numberCode/{size}")
    public ResponseResult numberCode(@PathVariable("size") int size){

        System.out.println("size:"+size);

        //生成验证码
        double mathRandom = (Math.random()*9+1) *(Math.pow(10,size-1));
        System.out.println(mathRandom);
        int resultInt = (int) mathRandom;
        System.out.println("generator src code: "+resultInt);

//        JSONObject result = new JSONObject();
//        result.put("code",1);
//        result.put("message","success");
//        JSONObject data = new JSONObject();
//        data.put("numberCode",resultInt);
//        result.put("data",data);
        //定义返回值
        NumberCodeResponse response = new NumberCodeResponse();
        response.setNumberCode(resultInt);


        return ResponseResult.success(response);
    }
}
