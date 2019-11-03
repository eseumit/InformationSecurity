import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class FileCipher {

    public static void main(String[] args) throws Exception {
        String eORd = args[0];
        String inputFile = args[2];
        String outputFile = args[4];
        String algorithm = args[5];
        String mode = args[6];
        String key_file = args[7];

        CBC cbc = new CBC();
        OFB ofb = new OFB();
        CTR ctr = new CTR();


        String keyFile = readStringFromFile(key_file);
        assert keyFile != null;
        String key = keyFile.split("-")[0].trim();
        String IV = keyFile.split("-")[1].trim();
        String nonce = keyFile.split("-")[2].trim();

        int blockSize = getBlockSize(algorithm);
        if(blockSize==8) {
            key = key.substring(8,16);
            nonce = nonce.substring(4,8);
        }
        else if(blockSize==16)
        {
            key = key.substring(0,16);
            nonce = nonce.substring(0,8);
        }


        String eORd_str = "";
        if(eORd.equalsIgnoreCase("-e"))
            eORd_str = "enc";
        else if(eORd.equalsIgnoreCase("-d"))
            eORd_str = "dec";

        if(mode.equalsIgnoreCase("CBC")) {
            System.out.println("CBC PROCESS");
            long StartTime = System.currentTimeMillis();
            cbc.cbc_process(IV,algorithm,inputFile,outputFile,blockSize,key,eORd);
            long EndTime = System.currentTimeMillis();
            long ExecutionTime = EndTime - StartTime;
            writeFile((inputFile + " " + outputFile + " " + eORd_str + " " + algorithm + " " + mode + " " + ExecutionTime).getBytes());
        }
        else if(mode.equalsIgnoreCase("OFB")){
            System.out.println("OFB PROCESS");
            long StartTime = System.currentTimeMillis();
            ofb.ofb_process(IV,algorithm,inputFile,outputFile,blockSize,key,eORd);
            long EndTime = System.currentTimeMillis();
            long ExecutionTime = EndTime - StartTime;
            writeFile((inputFile + " " + outputFile + " " + eORd_str + " " + algorithm + " " + mode + " " + ExecutionTime).getBytes());
        }
        else if(mode.equalsIgnoreCase("CTR")){
            System.out.println("CTR PROCESS");
            long StartTime = System.currentTimeMillis();
            ctr.ctr_process(algorithm,inputFile,outputFile,blockSize,key,eORd,nonce);
            long EndTime = System.currentTimeMillis();
            long ExecutionTime = EndTime - StartTime;
            writeFile((inputFile + " " + outputFile + " " + eORd_str + " " + algorithm + " " + mode + " " + ExecutionTime).getBytes());
        }
        else{
            System.out.println("Wrong Mode!");
        }

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

    private static void writeFile(byte[] data) {
        try {
            FileOutputStream out = new FileOutputStream("run.log", true);
            out.write(data);
            out.write("\n".getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
