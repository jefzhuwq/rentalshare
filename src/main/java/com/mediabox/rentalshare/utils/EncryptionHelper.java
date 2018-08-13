package com.mediabox.rentalshare.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionHelper {
	
	public synchronized String encrypt(String plaintext)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw e;
		}

		byte raw[] = md.digest(); // step 4

		String hash = Base64.getEncoder().encodeToString(raw); // step 5
		return hash; // step 6
	}
}
