/*
Name: Joshua Samontanez
Course: EEL 4768 Summer 2022
Assignment title: Project 2 - Branch Predictor
*/
package Project3;

public class Address {
    private final String nBitsAddress;
    private final String mBitsAddress;

    public Address (String hex, int m, int n) {
        // Convert the hex address to decimal
        int addressDecimal = Integer.parseInt(hex, 16);
        // Convert the decimal address to binary
        String binary = Integer.toBinaryString(addressDecimal);
        // Remove the offset
        binary = binary.substring((binary.length()- 2- m),(binary.length() - 2));

        // Get the first n bits
        nBitsAddress = binary.substring(0, n);
        // Get the remaining m bits starting from the index n
        mBitsAddress = binary.substring(n, m);
    }

    public String getMBitsAddress() {
        return mBitsAddress;
    }

    public String getNBitsAddress() {
        return nBitsAddress;
    }
}
