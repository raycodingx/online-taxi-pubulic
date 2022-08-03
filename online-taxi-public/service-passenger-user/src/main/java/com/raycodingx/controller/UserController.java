package com.raycodingx.controller;

import com.raycodingx.internalcommon.dto.ResponseResult;
import com.raycodingx.internalcommon.request.VerificationCodeDTO;
import com.raycodingx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationCodeDTO verificationCodeDTO){
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("乘客手机号： "+passengerPhone);
        return userService.loginOrRegister(passengerPhone);
    }
}
