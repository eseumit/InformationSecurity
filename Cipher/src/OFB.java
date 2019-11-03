import java.util.ArrayList;

public class OFB {
    void ofb_process(String IV, String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd) throws Exception {
        ENCRYPT enc = new ENCRYPT();
        XOR xor = new XOR();
        WRITEFILE wf = new WRITEFILE();
        READFILE rf = new READFILE();
        PADDING pd = new PADDING();

        String iv1 = IV.substring(0,blockSize);
        byte[] IV_bytes = iv1.getBytes("UTF-8");
        ArrayList<byte[]> cipherTextList = new ArrayList<>();

        if(eORd.equalsIgnoreCase("-e")){
            byte[] plainText = rf.readByteFromFile(inputFile);
            byte[] paddingPlainText = pd.padding(plainText, blockSize);
            byte[] _encryptedArray = new byte[paddingPlainText.length];
            int itr = 0;
            prc(algorithm, outputFile, blockSize, key, enc, xor, wf, IV_bytes, cipherTextList, paddingPlainText, _encryptedArray, itr);
            System.out.println("Encrypted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
        else if(eORd.equalsIgnoreCase("-d")){
            byte[] plainText = rf.readByteFromFile(inputFile);
            byte[] _decryptedArray = new byte[plainText.length];
            int itr = 0;
            prc(algorithm, outputFile, blockSize, key, enc, xor, wf, IV_bytes, cipherTextList, plainText, _decryptedArray, itr);
            System.out.println("Decryted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
    }

    private void prc(String algorithm, String outputFile, int blockSize, String key, ENCRYPT enc, XOR xor, WRITEFILE wf, byte[] IV_bytes, ArrayList<byte[]> cipherTextList, byte[] paddingPlainText, byte[] _encryptedArray, int itr) throws Exception {
        for (int i=0; i<paddingPlainText.length; i+=blockSize)
        {
            byte[] tmpArr = new byte[blockSize];
            System.arraycopy(paddingPlainText, i, tmpArr, 0, blockSize);
            if(itr == 0)
            {
                byte[] _encrypted = enc.encrypt(IV_bytes, key, algorithm);
                cipherTextList.add(_encrypted);
                byte[] xorPlaintTemp = xor.xor(tmpArr, _encrypted, blockSize);
                System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                itr++;
            }
            else if(itr > 0) {
                byte[] _encrypted = enc.encrypt(cipherTextList.get(itr-1), key, algorithm);
                cipherTextList.add(_encrypted);
                byte[] xorPlaintTemp = xor.xor(tmpArr, _encrypted, blockSize);
                System.arraycopy(xorPlaintTemp, 0, _encryptedArray, i, blockSize);
                itr++;
            }
        }
        wf.writeFile(outputFile, _encryptedArray);
    }
}
