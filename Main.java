package asg1;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Main {

	public static void main(String[] args) throws Exception 
	{
		String eORd = args[1];
		String inputFile = args[3];
		String outputFile = args[5];
		String algorithm = args[6];
		String mode = args[7];
		String key_file = args[8];
		
		String plainText = "     deneme metin text deneme metin text hadi o zaman beni veli deli meli merhaba veli deli meli merhaba umit merhaba merhaba merhaba  ";
		plainText = plainText.trim();
		System.out.println("Original Text : " +plainText + " ---- " + plainText.length());
		byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        
        byte[] arr = plainText.getBytes("UTF-8");
        //System.out.println(arr.length);
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(128);

        // Generate Key
        SecretKey key = keyGenerator.generateKey();
        int forItr = 0;
        if(plainText.length()%16 == 0)
        	forItr = plainText.length()/16;
        else
        	forItr = plainText.length()/16+1;
        String[] _arrPlainText = new String[forItr];
        int _addedChar = 0;
        
       
        for (int i = 0; i < forItr; i++) 
        {
        	if(i == (forItr-1) && plainText.length() < 16*i+16 && plainText.length()%16 !=0) 
        	{
        		String _addStr = "";
            	for (int j = 0; j < 16 - (plainText.length()%16); j++) 
            	{
            		_addedChar++;
    				_addStr += " ";
    			}
        		String _last = plainText.substring(i*16, plainText.length()) +  _addStr;
        		_arrPlainText[i] = _last;
        	}
        	else if(i != (plainText.length()/16))
        	{
    			_arrPlainText[i] = plainText.substring(i*16, 16*i+16);
        	}
		}
        
        
        List<byte[]> _listCipherTest = new ArrayList<byte[]> (); 
        for (int i = 0; i < _arrPlainText.length; i++) 
        {
        	byte[] cipherText = encrypt(_arrPlainText[i].getBytes(), key, algorithm);
        	_listCipherTest.add(cipherText);
		}
        List<byte[]> _arrDecryptedText = new ArrayList<byte[]>();
        String _strDecrypted = "";
        for (int i = 0; i < _listCipherTest.size(); i++) 
        {
        	byte[] decryptedText = decrypt(_listCipherTest.get(i),key);
        	_arrDecryptedText.add(decryptedText);
        	_strDecrypted += new String(decryptedText);
		}
        /*/System.out.println("_arrDecryptedtext size " + _arrDecryptedText.size());
        for (int i = 0; i < _arrDecryptedText.size(); i++) 
        {
        	System.out.println(_arrDecryptedText.get(i));
        	_strDecrypted += new String(_arrDecryptedText.get(i), "UTF-8");
		}/*/
        System.out.println("Decrypted Text :" +_strDecrypted.trim() + " ---- "+ _strDecrypted.trim().length());
        
        
       
        

	}
	 public static byte[] encrypt (byte[] plaintext,SecretKey key, String algorithm) throws Exception
	    {
	        //Get Cipher Instance
	        Cipher cipher = Cipher.getInstance(algorithm+"/ECB/NoPadding");
	        
	        
	        //Initialize Cipher for ENCRYPT_MODE
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        
	        //Perform Encryption
	        byte[] cipherText = cipher.update(plaintext);
	        
	        return cipherText;
	    }
	    
	    public static byte[] decrypt (byte[] cipherText, SecretKey key) throws Exception
	    {
	        //Get Cipher Instance
	        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

	        
	        //Initialize Cipher for DECRYPT_MODE
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        
	        //Perform Decryption
	        byte[] decryptedText = cipher.update(cipherText);
	        
	        return decryptedText;
	    }
}

