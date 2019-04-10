package com.ctech.imagecompressor.png;

import com.ctech.imagecompressor.ImageUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author sunil
 */
public class Main {


    private static final Logger LOG = Logger.getLogger(ImageUtils.class.getName());
    private static final Float IMAGE_COMPRESSION_RATIO = 0.1f;
    private static final boolean CONVERT_TO_JPG_BEFORE_COMPRESS = false;

    public static void main(String[] args) throws IOException {

        String folderPath = "/home/sunil/Pictures/compress/test/";
        String fileName = "sample.png";
        File file = new File(folderPath + fileName);
        byte[] uncompressedBytes = FileUtils.readFileToByteArray(file);
        long now = new Date().getTime();
        byte[] compressedBytes;
        String imageFormat = ImageUtils.getImageFormat(uncompressedBytes);

        if (ImageUtils.isImageTypePng(imageFormat)) {

            if(CONVERT_TO_JPG_BEFORE_COMPRESS) {
                compressedBytes = compressJpg(ImageUtils.convertPngToJpeg(uncompressedBytes));
            }else {
                compressedBytes = compressPng(uncompressedBytes);
            }
        } else if (ImageUtils.isImageTypeJpg(imageFormat)) {

            compressedBytes = compressJpg(uncompressedBytes);
        } else {
            LOG.info("Invalid image format; only PNG format allowed");
            return;
        }


        String compressedFileName = folderPath + "compressed_" +
                (CONVERT_TO_JPG_BEFORE_COMPRESS ?
                 fileName.replaceAll("png","jpg") : fileName);
        long timeTake = new Date().getTime() - now;
        FileUtils.writeByteArrayToFile(new File(compressedFileName), compressedBytes);

        LOG.info("Given file compressed [total time take: " + timeTake + "ms ] and saved as : "
                + compressedFileName);

    }

    private static byte[] compressPng(byte[] uncompressedBytes) throws IOException {

        final MyPngCompressor encoder = new MyPngCompressor(true);
        return encoder.compress(uncompressedBytes);
    }

    public static byte[] compressJpg(byte[] inputByte) throws IOException {

        LOG.info("Compressing JPG");
        if (inputByte.length == 0)
            return new byte[0];
        byte[] compressedImageBytes;
        ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpegWriteParam = jpegWriter.getDefaultWriteParam();
        jpegWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegWriteParam.setCompressionQuality(IMAGE_COMPRESSION_RATIO);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ImageOutputStream output =
                new MemoryCacheImageOutputStream(baos)) {
            jpegWriter.setOutput(output);
            IIOImage outputImage = new IIOImage(ImageIO.read(new ByteArrayInputStream(inputByte))
                    , null, null);
            jpegWriter.write(null, outputImage, jpegWriteParam);
            compressedImageBytes = baos.toByteArray();
        }
        jpegWriter.dispose();
        return compressedImageBytes;
    }

}
