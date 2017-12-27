package me.chuang6.jz.test;

import java.util.Random;

import org.junit.Test;

import me.chuang6.jz.util.AESUtils;

public class AesTest {

	@Test
	public void encrypt() {
		String result = AESUtils.encrypt("liuchuang#" + new Random().nextInt(Integer.MAX_VALUE));
		System.out.println(result);

	}

	@Test
	public void decrypt() {
		String result = AESUtils.decrypt("VHHa69wCwyi4M2/hDUAEYahI3p/oj4DCHEY5gJJuwBA=");
		System.out.println(result);

	}
}
