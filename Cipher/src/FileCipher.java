import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class FileCipher {

    public static void main(String[] args) throws Exception {
        String eORd = args[0];
        String inputFile = args[2];
        String outputFile = args[4];
        String algorithm = args[5];
        String mode = args[6];
        String key_file = args[7];
        File logFile = new File("run.log");


        String keyFile = readStringFromFile(key_file);
        String key = keyFile.split("-")[0].trim();
        String IV = keyFile.split("-")[1].trim();
        String nonce = keyFile.split("-")[2].trim();
        int blockSize = getBlockSize(algorithm);

        CBC cbc = new CBC();
        if(mode.equalsIgnoreCase("CBC")) {
            System.out.println("CBC işlemleri");
            //starttime
            cbc.cbc_process(IV,algorithm,inputFile,outputFile,blockSize,key,eORd);
            //endtime
        }
        else if(mode.equalsIgnoreCase("OFB")){
            System.out.println("OFB işlemleri");
        }
        else if(mode.equalsIgnoreCase("CTR")){
            System.out.println("CTR işlemleri");
        }
        else{
            System.out.println("Wrong Mode!");
        }

        //ecb.process(algorithm, eORd, inputFile, outputFile, key);

    }



    private static String readStringFromFile(String fileName) throws FileNotFoundException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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



}
