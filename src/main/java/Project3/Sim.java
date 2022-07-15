package Project3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Sim {

    public static void main(String[] args) {
        int m = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        String traceFile = args[2];

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0".repeat(Math.max(0, n)));

        // Initialize the BHR and PC to all zeroes
        StringBuilder BHR = new StringBuilder(stringBuilder.toString());
        String PC = stringBuilder.toString();

        // Get the size of the table
        int numPredictionReg = (int) Math.pow(2, m);
        // Create a table using the size computed previously
        ArrayList<Integer> predictionTable = new ArrayList<>(Collections.nCopies(numPredictionReg, 2));

        // Read the file and get the final result
        int[] result = readFile(n, m, PC, BHR, predictionTable, traceFile);
        // Trackers
        int miss = result[0];
        int access = result[1];

        // Display the results
        display(predictionTable, access, miss, m, n);
    }

    private static int[] readFile (int n, int m, String PC, StringBuilder BHR, ArrayList<Integer> predictionTable, String filePath) {
        int numMiss = 0;
        int numAccess = 0;
        // Read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Read the trace file per line
                String[] info = line.split(" ");
                String hex = info[0];
                String state = info[1];

                // Manipulate the given address
                Address address = new Address(hex, m, n);
                // Get the first n bits, to be used when XOR-ing with BHR
                String binary1 = address.getNBitsAddress();
                // Get the remaining m bits starting from the index n
                String binary2 = address.getMBitsAddress();

                // XOR the BHR and the m bits address
                PC = performXOR(PC, n, binary1, binary2, BHR);
                // Convert the PC in binary to decimal to be used to find the index in the table
                int entryIndex = Integer.parseInt(PC, 2);

                // Keep track of the number of miss predictions
                numMiss = predict(predictionTable, entryIndex, state, numMiss);

                // Update BHR by shifting it to the right
                BHR = updateBHR(state, BHR);

                numAccess++;
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new int[]{numMiss, numAccess};
    }

    private static void display (ArrayList<Integer> predictionTable, int access, int miss, int m, int n) {
        // Display the result
        DecimalFormat format = new DecimalFormat("##.00");
        String missPredictionRate = format.format((((double)miss/access)*100));

        System.out.println(m + " " + n + " " + missPredictionRate);
    }

    // Update BHR by shifting the binary to the right and inserting either 1 or 0 in front
    private static StringBuilder updateBHR (String state, StringBuilder BHR) {
        if (state.equals("t")) {
            BHR.insert(0, '1');
        }
        else if (state.equals("n")) {
            BHR.insert(0, '0');
        }

        return new StringBuilder(BHR.substring(0, BHR.length() - 1));
    }

    private static int predict (ArrayList<Integer> predictionTable, int entryIndex, String state, int miss) {
        switch (predictionTable.get(entryIndex)) {
            case 0:
                if (state.equals("t")){
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr + 1);
                    miss = miss + 1;
                }
                break;

            case 1:
                if (state.equals("t")){
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr + 1);
                    miss = miss + 1;
                }
                else if (state.equals("n")) {
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr - 1);
                }
                break;

            case 2:
                if (state.equals("t")) {
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr + 1);
                }
                else if (state.equals("n")) {
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr - 1);
                    miss = miss + 1;
                }
                break;
            case 3:
                if (state.equals("n")) {
                    int curr = predictionTable.get(entryIndex);
                    predictionTable.set(entryIndex, curr - 1);
                    miss = miss + 1;
                }
                break;
        }
        return miss;
    }

    private static String performXOR (String PC, int n, String binary1, String binary2, StringBuilder BHR) {
        // XOR the first n bits of the address and the BHR
        StringBuilder sb = new StringBuilder(PC);
        for (int i = 0; i < n; i++)
            sb.insert(i, binary1.charAt(i) ^ BHR.charAt(i));

        // Identify and return the found index
        PC = sb.toString();
        PC = PC.substring(0, n);
        PC += binary2;

        return PC;
    }
}
