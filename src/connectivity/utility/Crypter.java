package connectivity.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;



/**
 * 
 * This class handles 1024 bit RSA encryption and decryption, aswell as a variety of Hash-algorithms.
 * Thanks to Apache and CodeCrack
 * @author Leif Andreas Rudlang
 * @date 28.01.2014
 */
public class Crypter {


	public static final int SHA1 = 1;
	public static final int SHA256 = 2;
	public static final int SHA384 = 3;
	public static final int SHA512 = 4;
	public static final int MD5 = 5;

	public static final String RSA = "RSA";


	private KeyPair keypair;
	private Cipher cipher;


	/**
	 * 
	 * @param keypath
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public Crypter(String keypath) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{

		this.keypair = loadKeyPair(keypath);
		this.cipher = Cipher.getInstance(RSA);

	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public Crypter() throws NoSuchAlgorithmException, NoSuchPaddingException{

		clear();
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public void clear() throws NoSuchAlgorithmException, NoSuchPaddingException{

		KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
		kpg.initialize(1024);

		this.keypair = kpg.generateKeyPair();

		// save key to file and load for more use
		// load from file also, constructor!!


		this.cipher = Cipher.getInstance(RSA);

	}

	/**
	 * 
	 * @param plaintext
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String plaintext) throws Exception{

		this.cipher.init(Cipher.ENCRYPT_MODE, this.keypair.getPublic());
		byte[] bytes = plaintext.getBytes("UTF-8");

		byte[] encrypted = blockCipher(bytes,Cipher.ENCRYPT_MODE);

		char[] encryptedTranspherable = Hex.encodeHex(encrypted);


		return new String(encryptedTranspherable);
	}


	/**
	 * 
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String encrypted) throws Exception{

		this.cipher.init(Cipher.DECRYPT_MODE, this.keypair.getPrivate());
		byte[] bts = Hex.decodeHex(encrypted.toCharArray());

		byte[] decrypted = blockCipher(bts,Cipher.DECRYPT_MODE);

		return new String(decrypted,"UTF-8");
	}


	private byte[] blockCipher(byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException{
		// string initialize 2 buffers.
		// scrambled will hold intermediate results
		byte[] scrambled = new byte[0];

		// toReturn will hold the total result
		byte[] toReturn = new byte[0];
		// if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
		int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;

		// another buffer. this one will hold the bytes that have to be modified in this step
		byte[] buffer = new byte[length];

		for (int i=0; i< bytes.length; i++){

			// if we filled our buffer array we have our block ready for de- or encryption
			if ((i > 0) && (i % length == 0)){
				//execute the operation
				scrambled = cipher.doFinal(buffer);
				// add the result to our total result.
				toReturn = append(toReturn,scrambled);
				// here we calculate the length of the next buffer required
				int newlength = length;

				// if newlength would be longer than remaining bytes in the bytes array we shorten it.
				if (i + length > bytes.length) {
					newlength = bytes.length - i;
				}
				// clean the buffer array
				buffer = new byte[newlength];
			}
			// copy byte into our buffer.
			buffer[i%length] = bytes[i];
		}

		// this step is needed if we had a trailing buffer. should only happen when encrypting.
		// example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
		scrambled = cipher.doFinal(buffer);

		// final step before we can return the modified data.
		toReturn = append(toReturn,scrambled);

		return toReturn;
	}


	private byte[] append(byte[] prefix, byte[] suffix){

		byte[] toReturn = new byte[prefix.length + suffix.length];
		for (int i=0; i< prefix.length; i++){
			toReturn[i] = prefix[i];
		}
		for (int i=0; i< suffix.length; i++){
			toReturn[i+prefix.length] = suffix[i];
		}
		return toReturn;
	}


	public KeyPair getKeyPair(){
		return keypair;
	}


	public void setKeyPair(KeyPair in){
		this.keypair = in;
	}



	/**
	 * Returns the hash from the given input string and input algorithm
	 * Returns a empty string if a invalid algorithm was given.
	 * @param data
	 * @param algorithm
	 * @return
	 */
	public static String hash(String data, int algorithm){

		String result = "";

		switch(algorithm){


		case SHA1:
			result = DigestUtils.sha1Hex(data);
			break;

		case SHA256:
			result = DigestUtils.sha256Hex(data);
			break;

		case SHA384:
			result = DigestUtils.sha384Hex(data);
			break;

		case SHA512:
			result = DigestUtils.sha512Hex(data);
			break;

		case MD5:
			result = DigestUtils.md5Hex(data);
			break;
		}


		return result;
	}



	/**
	 * 
	 * @param path
	 * @param keyPair
	 * @throws IOException
	 */
	public void saveKeyPair(String path, KeyPair keyPair) throws IOException {


		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();

	}


	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public KeyPair loadKeyPair(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		// Read Public Key.
		File filePublicKey = new File(path + "/public.key");
		FileInputStream fis = new FileInputStream(path + "/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		// Read Private Key.
		File filePrivateKey = new File(path + "/private.key");
		fis = new FileInputStream(path + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}


	public static void main(String[] args) throws Exception{


		Crypter crypt = new Crypter("C:\\DEV\\");

		
		
		//crypt.saveKeyPair("C:\\DEV\\", crypt.getKeyPair());
	
		
		//String encrypted = crypt.encrypt("this is a test");
		String decrypted = crypt.decrypt("8cdf9d6b47ca27d14a3df7f5ad2b4865b257884d5bffecc7412d7d311b9527be686999f977234be1f22979f4c743bdbb46054baad12efc6badc2670a533bdecec8a895f220856481d657f32ee537e6d2d98100c9aec466ed525f393617e53b8c74411909cf1351585858ec27fc73a61c262720aa5eb11343ef9d91f1e41704aa");
		//System.out.println(encrypted);
		System.out.println(decrypted);

	}



}
