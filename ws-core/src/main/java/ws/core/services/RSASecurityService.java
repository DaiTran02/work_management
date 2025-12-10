package ws.core.services;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface RSASecurityService {
	
	public KeyPair generateRSAKkeyPair() throws NoSuchAlgorithmException;
	
	public String doEncryption(String plainText, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
	
	public String doDecryption(String cipherText, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
	
	public String publicKeyToString(PublicKey publicKey);
	
	public String privateKeyToString(PrivateKey privateKey);
	
	public PublicKey stringToPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException;
	
	public PrivateKey stringToPrivateKey(String privateKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException;
	
	public String createDataTest();
	
	public boolean isValid(String data);
}
