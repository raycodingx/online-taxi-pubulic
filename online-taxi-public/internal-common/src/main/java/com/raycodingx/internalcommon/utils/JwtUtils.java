package com.raycodingx.internalcommon.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.raycodingx.internalcommon.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    //
    private static final String SIGN = "DJSDHJ$#@!";
    private static final String JWT_KEY_PHONE = "passengerPhone";
    //假定乘客是1，司机是2
    private static final String JWT_KEY_IDENTITY = "identity";
    //token类型
    private static final String JWT_TOKEN_TYPE = "tokenType";

    //生成token
    public static String generatorToken(String passengerPhone,String identity,String tokenType){
        Map<String,String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE,passengerPhone);
        map.put(JWT_KEY_IDENTITY,identity);
        map.put(JWT_TOKEN_TYPE,tokenType);
        //token过期时间
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        //整合map
        map.forEach(
                (k,v)->{
                    builder.withClaim(k,v);
                }
        );
        //整合过期时间
        //builder.withExpiresAt(date);

        //生成token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));


        return sign;
    }


    //解析token
    public static TokenResult parseToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).asString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).asString();
        TokenResult tokenResult = new TokenResult();
        tokenResult.setPhone(phone);
        tokenResult.setIdentity(identity);
        return tokenResult;
    }

    public static void main(String[] args) {

        String s = generatorToken("13923453533","1","access");
        System.out.println("生成的token: "+ s);
        System.out.println("解析.....................");
        TokenResult tokenResult = parseToken(s);
        System.out.println("手机号： "+tokenResult.getPhone());
        System.out.println("身份： "+tokenResult.getIdentity());
    }
}