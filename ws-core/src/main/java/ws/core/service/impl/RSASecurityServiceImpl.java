package ws.core.service.impl;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ws.core.services.PropsService;
import ws.core.services.RSASecurityService;
@Slf4j
@Service
public class RSASecurityServiceImpl implements RSASecurityService{
	
	
	
	private final String RSA = "RSA";

	@Autowired
	private	PropsService propsService;
	
	@Override
	public KeyPair generateRSAKkeyPair() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
		keyPairGenerator.initialize(2048, secureRandom);
		return keyPairGenerator.generateKeyPair();
	}

	@Override
	public String doEncryption(String plainText, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	@Override
	public String doDecryption(String cipherText, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes);
	}

	@Override
	public String publicKeyToString(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	@Override
	public String privateKeyToString(PrivateKey privateKey) {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}

	@Override
	public PublicKey stringToPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePublic(keySpec);
	}

	@Override
	public PrivateKey stringToPrivateKey(String privateKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		return keyFactory.generatePrivate(keySpec);
	}

	@Override
	public boolean isValid(String data) {
		String result=null;
		try {
			result = doDecryption(data, stringToPrivateKey(propsService.getSecurityApiPartnerRSAPrivateKey()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException("X-API-KEY không hợp lệ");
		}
		
		JSONObject jsonObject = new JSONObject(result);
        String apiKey = jsonObject.getString("data");
        long salt = jsonObject.getLong("salt");
        
        long currentTime = Instant.now().getEpochSecond();
        long expirationTime = propsService.getSecurityApiPartnerRSASecondsExpiration();
        
        
        System.out.println("salt ne ->>>>>>>"+salt);
        log.debug("salt ne->>>>>> "+salt);
        
        System.out.println("current time ->>>" + currentTime);
        log.debug("Current time: "+currentTime);
        
        if (Math.abs(currentTime - salt) > expirationTime) {
            throw new BadCredentialsException("X-API-KEY chỉ hợp lệ trong "+expirationTime+" giây");
        }

        if (apiKey == null || !apiKey.equals(propsService.getSecurityApiPartnerXApiKey())) {
            throw new BadCredentialsException("API Key không đúng");
        }
        
		return true;
	}

	@Override
	public String createDataTest() {
		try {
			Document data=new Document();
			data.put("data", propsService.getSecurityApiPartnerXApiKey());
			data.put("salt", (long)Instant.now().getEpochSecond());
			
			String cipherText = this.doEncryption(data.toJson().toString(), stringToPublicKey(propsService.getSecurityApiPartnerRSAPublicKey()));
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException("Không thể tạo");
		}
	}
}
