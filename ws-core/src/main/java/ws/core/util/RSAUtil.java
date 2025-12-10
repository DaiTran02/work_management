package ws.core.util;

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
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bson.Document;
import org.json.JSONObject;

public class RSAUtil {

	private static final String RSA = "RSA";

	public static KeyPair generateRSAKkeyPair() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
		keyPairGenerator.initialize(2048, secureRandom);
		return keyPairGenerator.generateKeyPair();
	}

	public static String doEncryption(String plainText, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String doDecryption(String cipherText, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes);
	}

	public static String publicKeyToString(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	public static String privateKeyToString(PrivateKey privateKey) {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}

	public static PublicKey stringToPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePublic(keySpec);
	}

	public static PrivateKey stringToPrivateKey(String privateKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		return keyFactory.generatePrivate(keySpec);
	}

	public static void main(String args[]) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
//		demo();
		//demoJson();
		//generateRSAKeyPair();
		
		System.out.println(Instant.now().getEpochSecond());
	}
	
	@SuppressWarnings("unused")
	private static void demo() {
		try {
			String plainText = "This is a x-api-key";
			System.out.println("Value: "+plainText);

			// Encrypt
			String cipherText = doEncryption(plainText, stringToPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1l471aXm5qH//ze72AvPl8OK5JH3L62V0J7WWpONgJJ1mDOPq031ckDX9DcvFBuTDJxrHtM5CkWhsWj9rU131zRaZujtPVrBNhFLngKuwSohdtFANUcgm6MpAm/ZaOzUWtVAN/fWyytzrQ9vVOCsurPfLjwatl7gcz1c7z0i8OWrOYF5rGA1C4Mc/sHWfSOJWy9U8xHduuaRtMR1yiQWC2uazuWY5E7oMvNK300d3jqDvAvd5icBDe8MTTyjW89+u8+cvmRQER+juuNFjeKtYklUUToduf29/u1ftEy2CAvHJNPgaLaAh7HnFh8wKCRuEX/RliVCivT/0O+zdvlzuwIDAQAB"));
			System.out.println("Encryption: "+cipherText);
			
			// Decrypt
			String result = doDecryption(cipherText, stringToPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDWXjvVpebmof//N7vYC8+Xw4rkkfcvrZXQntZak42AknWYM4+rTfVyQNf0Ny8UG5MMnGse0zkKRaGxaP2tTXfXNFpm6O09WsE2EUueAq7BKiF20UA1RyCboykCb9lo7NRa1UA399bLK3OtD29U4Ky6s98uPBq2XuBzPVzvPSLw5as5gXmsYDULgxz+wdZ9I4lbL1TzEd265pG0xHXKJBYLa5rO5ZjkTugy80rfTR3eOoO8C93mJwEN7wxNPKNbz367z5y+ZFARH6O640WN4q1iSVRROh25/b3+7V+0TLYIC8ck0+BotoCHsecWHzAoJG4Rf9GWJUKK9P/Q77N2+XO7AgMBAAECggEAA+/nzMJm8utyG9+L1/73iW5c+a+4nivjqYORT56QJcFgNDMWO2uLdiq8t06K0yVigQPKb97UmFyzszdzbqBn8rrnwiYbCAUdBqowp66dmQygtWGdz/IGJkr2hjCZtJ9M6HzW2MWjhfMUBSStwx2mmSiT5nHSJ8am/AGE2HFYrUXx+DoXU5EU3kxkJcMe/YmWdssDKQmBFncBS8gqEONS0WUHKEoZ26kbN8aMhbhGv/viUd7JgVCDqYSUm1BWbS/JeQRKvRYJxiEWtKEykvmcXorUVH1s6L+Y93PDEHiPfZaJQiKG01lkXLOj24ej8jDKoLXAkjJb1SKLPT+ShYnWYQKBgQD4JjF+ryfKQOMb8RG7kpD34IdPMzcitPXoUaW6ZDjWwQlXa00gPO0wQA2KfFuVsALrFqNogZpdSr/6yGmgwjdJZgqZSMryd9InZP1VWrmvKV/sVHeBB2lSn1TCLus4kKcR0R0QAoO3RUeuli4AODmEw6BHMs3fikgoH1MH/jN08QKBgQDdJnDmSjIoJAnlg4dAa9T/AEMUIWfa3ISZMpnoedQObu1h8q02RsKWFhxZCTvfCrS7OVeTpKJEeTB1txmtIaqkd+t9h7mCtkHAmI9DCe5caJTbMaDsWJPpsmNDgaHe2ARTEPosF5nbdZmsBYsRoVn4gsAlO4N6FIQ2GTqZy6TDawKBgQCedfVBSqSEGS9t1mFWgF+minCdeL0KorgtZIYKIpnROW+3rUORBJcKKrz65DKvwbf7W3d58tkaHfO+N2CMUblwW4MFI2DFuRIwKeNEMC1G14rzpXSypoLxSeGcJs36TxUA4aJHaPOngswlhq1VYpIIX8vn619gEjPFu0sQXZmKYQKBgFJaX06SsKzcBsduPG6T+URgf5o+emM5HTPDcSbuPzaEvmdsLG562Tl00GejlaUQYo8Z4m/7reWbz5z2a1xaniYS9iHP6hgv8Lpc1XScfSPyWgRqLcf1kFdFc8uOuY23mZypRNAolcXwvCIgClKtaEivwAfdaq+KdlWC/ZyKBjgzAoGBAIJuRXsw/av5eiy150cNdUfjumtXApLtnjgus5lwqj/SuTnZTvQ2f6QhgemrIgnLEI4wub8STYAP63jT0F/mfqooEl7fAVgfoTovzEz2nOwc94KCs7gBU+M95pDqIk5zvRaKEfaBoOrZztG9I9J85wzEdh4XZQq6qrzmYsS9rMHS"));
			
			System.out.println("Decryption: "+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void demoJson() {
		try {
			Document data=new Document();
			data.put("data", "hello");
			data.put("salt", (long)Instant.now().getEpochSecond());
			
			System.out.println("Value: "+data.toJson());

			// Encrypt
			String cipherText = doEncryption(data.toJson().toString(), stringToPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1l471aXm5qH//ze72AvPl8OK5JH3L62V0J7WWpONgJJ1mDOPq031ckDX9DcvFBuTDJxrHtM5CkWhsWj9rU131zRaZujtPVrBNhFLngKuwSohdtFANUcgm6MpAm/ZaOzUWtVAN/fWyytzrQ9vVOCsurPfLjwatl7gcz1c7z0i8OWrOYF5rGA1C4Mc/sHWfSOJWy9U8xHduuaRtMR1yiQWC2uazuWY5E7oMvNK300d3jqDvAvd5icBDe8MTTyjW89+u8+cvmRQER+juuNFjeKtYklUUToduf29/u1ftEy2CAvHJNPgaLaAh7HnFh8wKCRuEX/RliVCivT/0O+zdvlzuwIDAQAB"));
			System.out.println("Encryption: "+cipherText);
			
			// Decrypt
			String result = doDecryption(cipherText, stringToPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDWXjvVpebmof//N7vYC8+Xw4rkkfcvrZXQntZak42AknWYM4+rTfVyQNf0Ny8UG5MMnGse0zkKRaGxaP2tTXfXNFpm6O09WsE2EUueAq7BKiF20UA1RyCboykCb9lo7NRa1UA399bLK3OtD29U4Ky6s98uPBq2XuBzPVzvPSLw5as5gXmsYDULgxz+wdZ9I4lbL1TzEd265pG0xHXKJBYLa5rO5ZjkTugy80rfTR3eOoO8C93mJwEN7wxNPKNbz367z5y+ZFARH6O640WN4q1iSVRROh25/b3+7V+0TLYIC8ck0+BotoCHsecWHzAoJG4Rf9GWJUKK9P/Q77N2+XO7AgMBAAECggEAA+/nzMJm8utyG9+L1/73iW5c+a+4nivjqYORT56QJcFgNDMWO2uLdiq8t06K0yVigQPKb97UmFyzszdzbqBn8rrnwiYbCAUdBqowp66dmQygtWGdz/IGJkr2hjCZtJ9M6HzW2MWjhfMUBSStwx2mmSiT5nHSJ8am/AGE2HFYrUXx+DoXU5EU3kxkJcMe/YmWdssDKQmBFncBS8gqEONS0WUHKEoZ26kbN8aMhbhGv/viUd7JgVCDqYSUm1BWbS/JeQRKvRYJxiEWtKEykvmcXorUVH1s6L+Y93PDEHiPfZaJQiKG01lkXLOj24ej8jDKoLXAkjJb1SKLPT+ShYnWYQKBgQD4JjF+ryfKQOMb8RG7kpD34IdPMzcitPXoUaW6ZDjWwQlXa00gPO0wQA2KfFuVsALrFqNogZpdSr/6yGmgwjdJZgqZSMryd9InZP1VWrmvKV/sVHeBB2lSn1TCLus4kKcR0R0QAoO3RUeuli4AODmEw6BHMs3fikgoH1MH/jN08QKBgQDdJnDmSjIoJAnlg4dAa9T/AEMUIWfa3ISZMpnoedQObu1h8q02RsKWFhxZCTvfCrS7OVeTpKJEeTB1txmtIaqkd+t9h7mCtkHAmI9DCe5caJTbMaDsWJPpsmNDgaHe2ARTEPosF5nbdZmsBYsRoVn4gsAlO4N6FIQ2GTqZy6TDawKBgQCedfVBSqSEGS9t1mFWgF+minCdeL0KorgtZIYKIpnROW+3rUORBJcKKrz65DKvwbf7W3d58tkaHfO+N2CMUblwW4MFI2DFuRIwKeNEMC1G14rzpXSypoLxSeGcJs36TxUA4aJHaPOngswlhq1VYpIIX8vn619gEjPFu0sQXZmKYQKBgFJaX06SsKzcBsduPG6T+URgf5o+emM5HTPDcSbuPzaEvmdsLG562Tl00GejlaUQYo8Z4m/7reWbz5z2a1xaniYS9iHP6hgv8Lpc1XScfSPyWgRqLcf1kFdFc8uOuY23mZypRNAolcXwvCIgClKtaEivwAfdaq+KdlWC/ZyKBjgzAoGBAIJuRXsw/av5eiy150cNdUfjumtXApLtnjgus5lwqj/SuTnZTvQ2f6QhgemrIgnLEI4wub8STYAP63jT0F/mfqooEl7fAVgfoTovzEz2nOwc94KCs7gBU+M95pDqIk5zvRaKEfaBoOrZztG9I9J85wzEdh4XZQq6qrzmYsS9rMHS"));
			System.out.println("Decryption: "+result);
			
			JSONObject jsonObject = new JSONObject(result);
            String apiKey = jsonObject.getString("data");
            long timestamp = jsonObject.getLong("salt");
            
            Thread.sleep(new Random().nextInt(20)*1000);
            
            // Kiểm tra thời gian TTL (5 giây)
            long currentTime = Instant.now().getEpochSecond();
            if (currentTime - timestamp > 500) {
                throw new IllegalArgumentException("Request expired");
            }

            // Kiểm tra x-api-key
            if (!"hello".equals(apiKey)) {
                throw new IllegalArgumentException("Invalid API key");
            }
            
            System.out.println("Valid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unused")
	private static void generateRSAKeyPair() {
		try {
			// Generate public key and private key
			KeyPair keyPair = generateRSAKkeyPair();

			// Export keys to string
			String publicKey = publicKeyToString(keyPair.getPublic());
			String privateKey = privateKeyToString(keyPair.getPrivate());

			System.out.println("Public key: " + publicKey);
			System.out.println("Private key: " + privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
