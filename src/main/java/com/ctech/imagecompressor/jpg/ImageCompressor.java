package com.ctech.imagecompressor.jpg;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Iterator;

/**
 * @author sunil
 */
public class ImageCompressor {


    public static void main(String[] args) throws Exception {

        String folderPath = "/home/sunil/Pictures/compress/";
        String fileName = "sample.png";


        BufferedImage bImage = ImageIO.read(new File(folderPath + fileName));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte[] orginalImageByte = bos.toByteArray();

        long now = new Date().getTime();
        byte[] compressedImageByte = compressJpeg(orginalImageByte);
        System.out.println("Time take to convert png to jpg : " + (new Date().getTime() - now));

        ByteArrayInputStream bis = new ByteArrayInputStream(compressedImageByte);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File(folderPath + "compressed_JPG_" + fileName));
        System.out.println("JPEG Image compressed");


        BufferedImage pngBufferImage = ImageIO.read(new File(folderPath + fileName));
        ByteArrayOutputStream pngOS = new ByteArrayOutputStream();
        ImageIO.write(pngBufferImage, "png", pngOS);
        byte[] orginalPngImageByte = pngOS.toByteArray();

        now = new Date().getTime();
        byte[] convertedJpeg = convertPngToJpeg(orginalPngImageByte);
        System.out.println("Time take to convert png to jpg : " + (new Date().getTime() - now));

        byte[] compressedPngImageByte = compressJpeg(convertedJpeg);
        //        byte [] compressedPngImageByte =  compress(orginalPngImageByte, 0.5f, "png");
        System.out.println("Time take to convert and compress converted jpg : " + (new Date().getTime() - now));
        ByteArrayInputStream pngBAInputStream = new ByteArrayInputStream(compressedPngImageByte);
        BufferedImage pngBufferImage2 = ImageIO.read(pngBAInputStream);
        ImageIO.write(pngBufferImage2, "jpg",
                new File(folderPath + "compressed_PNG_" + fileName.replaceAll("png", "jpg")));
        System.out.println("PNG Image converted and compressed as jpg");


        //        BufferedImage compressedBI = ImageUtils.resizeImage
        //        ("/home/sunil/Pictures/compress" +
        //                        "/profile.png", ImageUtils.IMAGE_PNG, 100,
        //                100);
        //
        //        ImageIO.write(compressedBI, "png", new File("/home/sunil/Pictures/compress" +
        //                "/profileCompress.png"));
        //        System.out.println("PNG Image converted and compressed as jpg");

    }


    private static final Float IMAGE_COMPRESSION_RATIO = 0.1f;

    public static byte[] compressJpeg(byte[] inputByte) throws IOException {
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

    static private byte[] convertPngToJpeg(byte[] pngBinary) throws IOException {
        //        byte[] pngBinary = DatatypeConverter.parseBase64Binary(pngBase64);
        InputStream in = new ByteArrayInputStream(pngBinary);
        BufferedImage pngImage = ImageIO.read(in);

        int width = pngImage.getWidth(), height = pngImage.getHeight();
        BufferedImage jpgImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = jpgImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.drawImage(pngImage, 0, 0, width, height, null);
        g.dispose();

        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.setOutput(ImageIO.createImageOutputStream(baos));
        writer.write(null, new IIOImage(jpgImage, null, null), null);

        return baos.toByteArray();
        //        String jpgBase64 = DatatypeConverter.printBase64Binary(baos.toByteArray());
        //        return jpgBase64;
    }


    public static byte[] compress(byte[] imgContent, float quality, String extName) {
        if (quality > 1 || quality <= 0 || imgContent == null || extName == null || extName.equals("")) {
            System.out.println("Invalid params");
            return null;
        }
        try (InputStream is = new ByteArrayInputStream(imgContent);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(os);) {
            BufferedImage image = ImageIO.read(is);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extName);
            ImageWriter writer = writers.next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();

            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
