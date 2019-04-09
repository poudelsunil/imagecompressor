package com.ctech.imagecompressor.png;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author sunil
 */
public class Main {
    public static void main(String[] args) throws IOException {

        String folderPath = "/home/sunil/Pictures/compress/";
        String fileName = "sample.png";
        File file = new File(folderPath + fileName);
        byte[] uncompressedBytes = FileUtils.readFileToByteArray(file);

        long now = new Date().getTime();
        final MyPngCompressor encoder = new MyPngCompressor(true);
        byte[] compressedBytes = encoder.compress(uncompressedBytes);
        long timeTake = new Date().getTime() - now;

        FileUtils.writeByteArrayToFile(new File(folderPath + "compressed_" + fileName),
                compressedBytes);

        System.out.println("Given file compressed [ total time take : " + timeTake + "ms ] and " +
                "saved as : " + folderPath + "compressed_" + fileName);



    }

}
