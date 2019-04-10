package com.ctech.imagecompressor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author sunil
 */
public class ImageUtils {
    private static final Logger LOG = Logger.getLogger(ImageUtils.class.getName());


    public static boolean isImageTypePng(String format) {
        return "PNG".equalsIgnoreCase(format);
    }

    public static boolean isImageTypePng(byte[] bytes) {
        return isImageTypePng(getImageFormat(bytes));
    }

    public static boolean isImageTypeJpg(String format) {
        return "JPG".equalsIgnoreCase(format) || "JPEG".equalsIgnoreCase(format);
    }

    public static boolean isImageTypeJpg(byte[] bytes) {
        return isImageTypeJpg(getImageFormat(bytes));
    }

    public static String getImageFormat(byte[] bytes) {
        String format ="";
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            LOG.info(e.getMessage());
            return format;
        }
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

        if (!iter.hasNext()) {

            LOG.info("NO READER FOUND");
            return format;
        }

        ImageReader reader = iter.next();
        try {
            format = reader.getFormatName();
            LOG.info("Format: "+format);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return format;
    }

    public static byte[] convertPngToJpeg(byte[] pngBinary) throws IOException {
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
    }
}
