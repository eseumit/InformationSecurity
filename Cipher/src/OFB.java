import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.ArrayList;

public class OFB {
    public void ofb_process(String IV, String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd) throws Exception {
        String iv1 = IV.substring(0,blockSize);
        byte[] IV_bytes = iv1.getBytes("UTF-8");
        ArrayList<byte[]> cipherTextList = new ArrayList<>();

        if(eORd.equalsIgnoreCase("-e")){
            byte[] plainText = readByteFromFile(inputFile);
            byte[] paddingPlainText = padding(plainText, blockSize);
            byte[] _encryptedArray = new byte[paddingPlainText.length];
            int itr = 0;
            for (int i=0; i<paddingPlainText.length; i+=blockSize)
            {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(paddingPlainText, i, tmpArr, 0, blockSize);
                if(itr == 0)
                {
                    System.out.println(new String(tmpArr,"UTF-8"));
                    byte[] _encrypted = encrypt(IV_bytes, key, algorithm);
                    cipherTextList.add(_encrypted);
                    byte[] xorPlaintTemp = xor(tmpArr, _encrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    System.out.println(new String(tmpArr,"UTF-8"));
                    byte[] _encrypted = encrypt(cipherTextList.get(itr-1), key, algorithm);
                    cipherTextList.add(_encrypted);
                    byte[] xorPlaintTemp = xor(tmpArr, _encrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
            }
            writeFile(outputFile, _encryptedArray);
            System.out.println("Encrypted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
        else if(eORd.equalsIgnoreCase("-d")){
            byte[] plainText = readByteFromFile(inputFile);
            byte[] _decryptedArray = new byte[plainText.length];
            int itr = 0;
            for (int i=0; i<plainText.length; i+=blockSize)
            {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(plainText, i, tmpArr, 0, blockSize);
                if(itr == 0)
                {
                    byte[] _decrypted = encrypt(IV_bytes, key, algorithm);
                    cipherTextList.add(_decrypted);
                    byte[] xorPlaintTemp = xor(tmpArr, _decrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _decryptedArray, i, blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] _decrypted = encrypt(cipherTextList.get(itr-1), key, algorithm);
                    cipherTextList.add(_decrypted);
                    byte[] xorPlaintTemp = xor(tmpArr, _decrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _decryptedArray, i, blockSize);
                    itr++;
                }
            }
            writeFile(outputFile, _decryptedArray);
            System.out.println("Decryted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
    }
    private static byte[] xor(byte[] text, byte[] IV, int blockSize) {
        byte[] xor = new byte[blockSize];

        for (int i = 0; i < blockSize; i++) {
            xor[i] = (byte) ((text[i]) ^ (IV[i]));
        }
        return xor;
    }

    private static void writeFile(String fileName, byte[] data) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readByteFromFile(String fileName) {
        File file = new File(fileName);
        byte[] result = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            fileInputStream.read(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static byte[] padding(byte[] text, int blockSize){
        byte[] padding = new byte[text.length + blockSize-(text.length%blockSize)];
        byte[] temp = new byte[blockSize-(text.length%blockSize)];
        for (int i=0; i<blockSize-(text.length%blockSize); i++) {
            temp[i] = ' ';
        }
        System.arraycopy(text, 0, padding, 0, text.length);
        System.arraycopy(temp, 0, padding, text.length, temp.length);
        return padding;
    }

    private static byte[] encrypt (byte[] plainText, String key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm+"/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }

    private static byte[] decrypt (byte[] cipherText, String key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm+"/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cipherText);
    }
}
