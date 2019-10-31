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

        if(eORd.equalsIgnoreCase("-e")){
            int counter = 0;
            byte[] counter_bytes = counterCreate(counter, blockSize);
            byte[] nonce_bytes = nonce.getBytes("UTF-8");
            byte[] concatenate = concat(nonce_bytes, counter_bytes, blockSize);
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
                    byte[] _encrypted = encrypt(concatenate, key, algorithm);
                    byte[] xorPlaintTemp = xor(tmpArr, _encrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                    counter +=1;
                    counter_bytes = counterCreate(counter, blockSize);
                    concatenate = concat(nonce_bytes, counter_bytes,blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] _encrypted = encrypt(concatenate, key, algorithm);
                    byte[] xorPlaintTemp = xor(tmpArr, _encrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                    counter +=1;
                    counter_bytes = counterCreate(counter, blockSize);
                    concatenate = concat(nonce_bytes, counter_bytes,blockSize);
                    itr++;
                }
            }
            writeFile(outputFile, _encryptedArray);
            System.out.println("Encrypted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
        else if(eORd.equalsIgnoreCase("-d")){
            int counter = 0;
            byte[] counter_bytes = counterCreate(counter, blockSize);
            byte[] nonce_bytes = nonce.getBytes("UTF-8");
            byte[] concatenate = concat(nonce_bytes, counter_bytes, blockSize);
            byte[] plainText = readByteFromFile(inputFile);
            byte[] _decryptedArray = new byte[plainText.length];
            int itr = 0;
            for (int i=0; i<plainText.length; i+=blockSize)
            {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(plainText, i, tmpArr, 0, blockSize);
                if(itr == 0)
                {
                    byte[] _decrypted = encrypt(concatenate, key, algorithm);
                    byte[] xorPlaintTemp = xor(tmpArr, _decrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _decryptedArray, i, blockSize);
                    counter +=1;
                    counter_bytes = counterCreate(counter, blockSize);
                    concatenate = concat(nonce_bytes, counter_bytes,blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] _decrypted = encrypt(concatenate, key, algorithm);
                    byte[] xorPlaintTemp = xor(tmpArr, _decrypted, blockSize);
                    System.arraycopy(xorPlaintTemp, 0, _decryptedArray, i, blockSize);
                    counter +=1;
                    counter_bytes = counterCreate(counter, blockSize);
                    concatenate = concat(nonce_bytes, counter_bytes,blockSize);
                    itr++;
                }
            }
            writeFile(outputFile, _decryptedArray);
            System.out.println("Decryted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
    }

    private byte[] concat(byte[] nonce_bytes, byte[] counter_bytes, int blockSize) {
        byte[] concatenate = new byte[blockSize];
        System.arraycopy(nonce_bytes, 0, concatenate, 0, nonce_bytes.length);
        System.arraycopy(counter_bytes, 0, concatenate, 0, counter_bytes.length);
        return concatenate;
    }

    private byte[] counterCreate(int counter, int blockSize) {
        byte[] counter_bytes = new byte[blockSize / 2];
        if(blockSize == 8){
            counter_bytes = intToByteArray(counter);
        }
        else if(blockSize == 16){
            byte[] temp1 = intToByteArray(counter);
            byte[] temp2 = intToByteArray(counter);
            System.arraycopy(temp1, 0, counter_bytes, 0, temp1.length);
            System.arraycopy(temp2, 0, counter_bytes, temp1.length, blockSize/4);
        }
        return counter_bytes;
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

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

}

