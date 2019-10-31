import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CBC {
    public void cbc_process(String IV, String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd) throws Exception {
        String iv1 = IV.substring(0,blockSize);
        byte[] IV_bytes = iv1.getBytes("UTF-8");
        ArrayList<byte[]> cipherTextList = new ArrayList<>();
        if(eORd.equalsIgnoreCase("-e")) {
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
                    byte[] xorPlaintTemp = xor(tmpArr, IV_bytes, blockSize);
                    byte[] _encrypted = encrypt(xorPlaintTemp, key, algorithm);
                    cipherTextList.add(_encrypted);
                    System.arraycopy(_encrypted, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] xorPlainTemp = xor(tmpArr, cipherTextList.get(itr - 1), blockSize);
                    byte[] _encrypted = encrypt(xorPlainTemp, key, algorithm);
                    cipherTextList.add(_encrypted);
                    System.arraycopy(_encrypted, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
            }
            writeFile(outputFile, _encryptedArray);
            System.out.println("Encrypted Completed and Write to " + outputFile);
        }
        else if(eORd.equalsIgnoreCase("-d")){
            ArrayList<byte[]> ciphertext = new ArrayList<>();
            int itr = 0;
            byte[] cipherText = readByteFromFile(inputFile);
            byte[] _decryptedArray = new byte[cipherText.length];

            for (int i=0; i<cipherText.length; i+=blockSize) {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(cipherText, i, tmpArr, 0, blockSize);

                if(itr == 0) {
                    byte[] _decrypted = decrypt(tmpArr, key, algorithm);
                    byte[] xorPlainTemp = xor(_decrypted, IV_bytes, blockSize);

                    System.arraycopy(xorPlainTemp, 0, _decryptedArray, i, blockSize);
                    ciphertext.add(tmpArr);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] _decrypted = decrypt(tmpArr, key, algorithm);
                    byte[] xorPlainTemp = xor(_decrypted, ciphertext.get(itr-1), blockSize);
                    System.arraycopy(xorPlainTemp, 0, _decryptedArray, i, blockSize);
                    ciphertext.add(tmpArr);
                    itr++;
                }

            }
            writeFile(outputFile,_decryptedArray);
            System.out.println("Decrypted Completed and Write to " + outputFile);
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
