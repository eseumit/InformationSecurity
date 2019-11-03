import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CTR {
    public void ctr_process(String algorithm, String inputFile, String outputFile, int blockSize, String key, String eORd, String nonce) throws Exception {
        XOR xor = new XOR();
        ENCRYPT enc = new ENCRYPT();
        WRITEFILE wf = new WRITEFILE();
        READFILE rf = new READFILE();
        PADDING pd = new PADDING();

        if(eORd.equalsIgnoreCase("-e")){
            byte[] plainText = rf.readByteFromFile(inputFile);
            byte[] paddingPlainText = pd.padding(plainText, blockSize);
            prc(blockSize,nonce,paddingPlainText,key,algorithm,outputFile,enc,xor,wf);
            System.out.println("Encrypted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
        else if(eORd.equalsIgnoreCase("-d")){
            byte[] plainText = rf.readByteFromFile(inputFile);
            prc(blockSize,nonce,plainText,key,algorithm,outputFile,enc,xor,wf);
            System.out.println("Decryted Completed and Write to " + outputFile + " with " + algorithm + " OFB mode ");
        }
    }

    private void prc(int blockSize, String nonce, byte[] text, String key, String algorithm, String outputFile, ENCRYPT enc, XOR xor, WRITEFILE wf) throws Exception {
        int counter = 0;
        byte[] counter_bytes = counterCreate(counter, blockSize);
        byte[] nonce_bytes = nonce.getBytes("UTF-8");
        byte[] concatenate = concat(nonce_bytes, counter_bytes, blockSize);
        byte[] _decryptedArray = new byte[text.length];
        for (int i=0; i<text.length; i+=blockSize)
        {
            byte[] tmpArr = new byte[blockSize];
            System.arraycopy(text, i, tmpArr, 0, blockSize);
            byte[] _decrypted = enc.encrypt(concatenate, key, algorithm);
            byte[] xorPlaintTemp = xor.xor(tmpArr, _decrypted, blockSize);
            System.arraycopy(xorPlaintTemp, 0, _decryptedArray, i, blockSize);
            counter +=1;
            counter_bytes = counterCreate(counter, blockSize);
            concatenate = concat(nonce_bytes, counter_bytes,blockSize);
        }
        wf.writeFile(outputFile, _decryptedArray);
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

    private static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

}

