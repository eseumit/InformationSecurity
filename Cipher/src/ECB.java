import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class ECB {
    public void process(String algorithm, String eORd, String inputFile, String outputFile, String key) throws Exception {
        int block_size = getBlockSize(algorithm);
        if(eORd.equalsIgnoreCase("-e")) {
            byte[] plainText = readByteFromFile(inputFile);
            //System.out.print(new String(plainText,"UTF-8"));
            byte[] paddingPlainText = padding(plainText, algorithm);
            byte[] _encryptedArray = new byte[paddingPlainText.length];
            for (int i=0; i<paddingPlainText.length; i+=block_size)
            {
                byte[] tmpArr = new byte[block_size];
                System.arraycopy(paddingPlainText, i, tmpArr, 0, block_size);
                byte[] _encrypted = encrypt(tmpArr, key, algorithm);
                System.arraycopy(_encrypted, 0, _encryptedArray, i, block_size);
            }
            writeFile(outputFile, _encryptedArray);
            System.out.println("Encrypted Completed and Write to " + outputFile);
        }
        else if(eORd.equalsIgnoreCase("-d")) {
            byte[] cipherText = readByteFromFile(inputFile);
            byte[] _decrypted = decrypt(cipherText, key, algorithm);
            //System.out.println(new String(_decrypted, "UTF-8"));
            writeFile(outputFile,_decrypted);
            System.out.println("Decrypted Completed and Write to " + outputFile);
        }
    }
    private static int getBlockSize(String algorithm) {
        int block_size = 0;
        if (algorithm.equalsIgnoreCase("AES")) {
            block_size = 16;
        } else if (algorithm.equalsIgnoreCase("DES")) {
            block_size = 8;
        }
        return block_size;
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

    private static byte[] padding(byte[] text, String algorithm){
        int block_size = getBlockSize(algorithm);
        byte[] padding = new byte[text.length + block_size-(text.length%block_size)];
        byte[] temp = new byte[block_size-(text.length%block_size)];
        for (int i=0; i<block_size-(text.length%block_size); i++)
        {
            temp[i] = ' ';
        }
        System.arraycopy(text, 0, padding, 0, text.length);
        System.arraycopy(temp, 0, padding, text.length, temp.length);
        return padding;

    }

}
