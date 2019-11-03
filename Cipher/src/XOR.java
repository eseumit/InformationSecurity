public class XOR {
    public byte[] xor(byte[] text, byte[] IV, int blockSize) {
        byte[] xor = new byte[blockSize];

        for (int i = 0; i < blockSize; i++) {
            xor[i] = (byte) ((text[i]) ^ (IV[i]));
        }
        return xor;
    }
}
