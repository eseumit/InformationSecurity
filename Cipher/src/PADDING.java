public class PADDING {
    public byte[] padding(byte[] text, int blockSize){
        byte[] padding = new byte[text.length + blockSize-(text.length%blockSize)];
        byte[] temp = new byte[blockSize-(text.length%blockSize)];
        for (int i=0; i<blockSize-(text.length%blockSize); i++) {
            temp[i] = ' ';
        }
        System.arraycopy(text, 0, padding, 0, text.length);
        System.arraycopy(temp, 0, padding, text.length, temp.length);
        return padding;
    }


}
