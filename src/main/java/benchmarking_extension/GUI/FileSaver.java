package benchmarking_extension.GUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {
    private BufferedWriter writer;
    private final String data;

    public FileSaver(String data) {
        this.data = data;

        try{
            writer = new BufferedWriter(new FileWriter("output.csv"));
            saveFile();
            writer.close();
        }catch(Exception e){
            System.out.println("Could not write to file...");
        }

    }

    private void saveFile() throws IOException {
        // ballX, ballY, ballTimestamp, gazeX, gazeY, gazeTimeStamp
        writer.write("ballX, ballY, ballTimeStamp, gazeX, gazeY, gazeTimeStamp\n");
        writer.write(data);
/*
        for(double[] arr : data){
            writer.write(arr[0] + "," + arr[1] + "\n");
        }
        */

    }
}
