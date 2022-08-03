package com.raycodingx.apipassenger.service;

import com.raycodingx.apipassenger.remote.ServicePassengerUserClient;
import com.raycodingx.apipassenger.remote.ServiceVerificationCodeClient;
import com.raycodingx.internalcommon.constant.CommonStatusEnum;
import com.raycodingx.internalcommon.constant.IdentityConstants;
import com.raycodingx.internalcommon.constant.TokenConstants;
import com.raycodingx.internalcommon.dto.ResponseResult;
import com.raycodingx.internalcommon.request.VerificationCodeDTO;
import com.raycodingx.internalcommon.response.NumberCodeResponse;
import com.raycodingx.internalcommon.response.TokenResponse;
import com.raycodingx.internalcommon.utils.JwtUtils;
import com.raycodingx.internalcommon.utils.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 生成验证码
     * @param passengerPhone
     * @return
     */
    public ResponseResult generatorCode(String passengerPhone){
        //调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();
        System.out.println("remote number code:" +numberCode);

        //存入redis
        System.out.println("存入Redis");
        //key,value，过期时间
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
        //存入redis
        stringRedisTemplate.opsForValue().set(key,numberCode+"",60, TimeUnit.SECONDS);


//        JSONObject result = new JSONObject();
//        result.put("code",1);
//        result.put("message","success");

        //通过对应的短信服务商，将对应的验证码发送到手机上，阿里短信服务，腾讯短信服务，华信，容联

        return ResponseResult.success("");
    }



    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;


    /**
     * 验证验证码
     * @param passengerPhone
     * @param verificationCode
     * @return
     */
    public ResponseResult checkCode(String passengerPhone,String verificationCode){
        //根据手机号，去redis读取验证码
        System.out.println("根据手机号，去redis读取验证码");

        //生成key
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);

        //根据key获取value
        String rediscode = stringRedisTemplate.opsForValue().get(key);
        System.out.println("redis中的value: " + rediscode);

        //校验验证码
        if (StringUtils.isBlank(rediscode)){
            return  ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if (!verificationCode.trim().equals(rediscode.trim())){
            return  ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());

        }

        //判断原来是否有用户，并进行对应的处理
        System.out.println("判断原来是否有用户，并进行对应的处理");
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        //颁发令牌,不应该用魔法值，用枚举
        System.out.println("颁发令牌");
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        //将token存入redis中
        String tokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone,IdentityConstants.PASSENGER_IDENTITY,TokenConstants.ACCESS_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(tokenKey,accessToken,30,TimeUnit.DAYS);

        //响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }
}
