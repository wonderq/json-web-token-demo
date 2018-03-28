package com.hxuhao.utils;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class JWTUtil {
	

    private static final String profiles="hxhxhxhxh";
	
	/**
	 * 由字符串生成加密key
	 * @return
	 */
    private static SecretKey generalKey(){
		String stringKey = profiles;
		//对自己定义的一个string变量进行base64解码
        //解码完毕则为：aHhoeGh4aHho
		byte[] encodedKey = Base64.decodeBase64(stringKey);
		//采用AES方式，生成加密KEY
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}

	/**
	 * 创建jwt，json web token
	 * @param id 用户id
	 * @param subject  用户name
	 * @param ttlMillis token有效时间
	 * @return  字符串的 token
	 * @throws Exception
	 */
	public static String createJWT(String id, String subject, long ttlMillis) throws Exception {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
			.setId(id)
			.setIssuedAt(now)
			.setSubject(subject)
		    .signWith(signatureAlgorithm, key);
		if (ttlMillis >= 0) {
		    long expMillis = nowMillis + ttlMillis;
		    Date exp = new Date(expMillis);
		    builder.setExpiration(exp);
		}
		return builder.compact();
	}
	
	/**
	 * 解析jwt
	 * @param jwt json web token
	 * @return
	 * @throws Exception
	 */
	public static Claims parseJWT(String jwt) throws Exception{
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()         
		   .setSigningKey(key)
		   .parseClaimsJws(jwt).getBody();

		return claims;
	}

	// test
	public static void main(String[] args){
		
		try {
			String token = JWTUtil.createJWT("233", "payload", 1000*60);
			System.out.println(token);
			Claims c = JWTUtil.parseJWT(token);
			System.out.println(c.getSubject());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("this token is invalid");
		}
	}
}