import java.util.ArrayList;

public class CBC {
    public void cbc_process(String IV, String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd) throws Exception {
        DECRYPT dec = new DECRYPT();
        ENCRYPT enc = new ENCRYPT();
        XOR xor = new XOR();
        WRITEFILE wf = new WRITEFILE();
        READFILE rf = new READFILE();
        PADDING pd = new PADDING();


        String iv1 = IV.substring(0,blockSize);
        byte[] IV_bytes = iv1.getBytes("UTF-8");
        ArrayList<byte[]> cipherTextList = new ArrayList<>();
        if(eORd.equalsIgnoreCase("-e")) {
            byte[] plainText = rf.readByteFromFile(inputFile);
            byte[] paddingPlainText = pd.padding(plainText, blockSize);
            byte[] _encryptedArray = new byte[paddingPlainText.length];
            int itr = 0;
            for (int i=0; i<paddingPlainText.length; i+=blockSize)
            {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(paddingPlainText, i, tmpArr, 0, blockSize);
                if(itr == 0)
                {
                    byte[] xorPlaintTemp = xor.xor(tmpArr, IV_bytes, blockSize);
                    byte[] _encrypted = enc.encrypt(xorPlaintTemp, key, algorithm);
                    cipherTextList.add(_encrypted);
                    System.arraycopy(_encrypted, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] xorPlainTemp = xor.xor(tmpArr, cipherTextList.get(itr - 1), blockSize);
                    byte[] _encrypted = enc.encrypt(xorPlainTemp, key, algorithm);
                    cipherTextList.add(_encrypted);
                    System.arraycopy(_encrypted, 0, _encryptedArray, i, blockSize);
                    itr++;
                }
            }
            wf.writeFile(outputFile, _encryptedArray);
            System.out.println("Encrypted Completed and Write to " + outputFile + " with " + algorithm);
        }
        else if(eORd.equalsIgnoreCase("-d")){
            ArrayList<byte[]> ciphertext = new ArrayList<>();
            int itr = 0;
            byte[] cipherText = rf.readByteFromFile(inputFile);
            byte[] _decryptedArray = new byte[cipherText.length];

            for (int i=0; i<cipherText.length; i+=blockSize) {
                byte[] tmpArr = new byte[blockSize];
                System.arraycopy(cipherText, i, tmpArr, 0, blockSize);

                if(itr == 0) {
                    byte[] _decrypted = dec.decrypt(tmpArr, key, algorithm);
                    byte[] xorPlainTemp = xor.xor(_decrypted, IV_bytes, blockSize);

                    System.arraycopy(xorPlainTemp, 0, _decryptedArray, i, blockSize);
                    ciphertext.add(tmpArr);
                    itr++;
                }
                else if(itr > 0) {
                    byte[] _decrypted = dec.decrypt(tmpArr, key, algorithm);
                    byte[] xorPlainTemp = xor.xor(_decrypted, ciphertext.get(itr-1), blockSize);
                    System.arraycopy(xorPlainTemp, 0, _decryptedArray, i, blockSize);
                    ciphertext.add(tmpArr);
                    itr++;
                }
            }
            wf.writeFile(outputFile,_decryptedArray);
            System.out.println("Decrypted Completed and Write to " + outputFile);
        }
    }
}
