import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CTR {
    public void ctr_process(String IV, String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd, String nonce) throws Exception {
        String iv1 = IV.substring(0,blockSize);
        byte[] IV_bytes = iv1.getBytes("UTF-8");
        ArrayList<byte[]> cipherTextList = new ArrayList<>();
        String counter = "";

        if(eORd.equalsIgnoreCase("-e")){
            byte[] b = "00000000".getBytes();
            byte[] nonce_bytes = nonce.getBytes();
            byte[] concatenate = new byte[b.length + nonce_bytes.length];
            //System.out.println("boyut " + concatenate.length);
            //System.arraycopy(text, 0, padding, 0, text.length);
            //System.arraycopy(temp, 0, padding, text.length, temp.length);
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

    private static void incrementCtr(byte[] ctr, int index) {

        ctr[index]++;

        //If byte = 0, it means I have a carry so I'll call
        //function again with previous index
        if(ctr[index] == 0) {
            if(index != 0)
                incrementCtr(ctr, index - 1);
            else
                return;
        }
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

