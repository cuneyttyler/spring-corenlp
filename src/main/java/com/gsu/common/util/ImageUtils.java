package com.gsu.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

/**
 * Created by cnytync on 20/04/15.
 */
@Component
public class ImageUtils {

    private static Logger logger = Logger.getLogger(ImageUtils.class);

    public static String KB_IMG_LOCATION = "/var/www/images/knowledge_base/";
    public static String HI_IMG_LOCATION = "/var/www/images/historical_istanbul/";

    public static int SCALED_IMAGE_WIDTH = 300;
    public static int SCALED_IMAGE_HEIGHT = 300;

    public BufferedImage readImageFromUri(String urlString)  {
        String str = "";

        if (urlString.contains("commons.wikimedia"))
            try {
                str = getRedirectUrl(urlString);
            } catch (Exception e) {
                str = urlString;
            }
        else
            str = urlString;

        try {
        URL url = new URL(str);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


            return ImageIO.read(urlConnection.getInputStream());
        } catch(Exception e) {
            System.err.println("ERROR : Exception thrown while reading image.");
            e.printStackTrace();
            return null;
        }
    }

    private String getRedirectUrl(String url) throws Exception {

        String redirecterUrl = "http://wheregoes.com/retracer.php";

        URL obj = new URL(redirecterUrl);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8");
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");

        String data = "traceme=" + URLEncoder.encode(url);
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(data);
        out.close();

//        InputStreamReader isr = new InputStreamReader(conn.getInputStream());

        StringWriter writer = new StringWriter();
        IOUtils.copy(conn.getInputStream(), writer, "UTF-8");
        String html = writer.toString();

        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("tracecontent");

        String subhtml = elements.get(0).html();
        int i = subhtml.indexOf("https://upload.wikimedia");
        int j = subhtml.indexOf(".jpg", i);
        if (j == -1) {
            j = subhtml.indexOf(".png", i);
        }

        return subhtml.substring(i, j + 4);
    }

//    public static void main(String args[]) throws IOException {
//        String stringUrl = "https://qualysapi.qualys.eu/api/2.0/fo/report/?action=list";
//        URL url = new URL(stringUrl);
//        URLConnection uc = url.openConnection();
//
//        uc.setRequestProperty("X-Requested-With", "Curl");
//
//        String userpass = "username" + ":" + "password";
//        String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
//        uc.setRequestProperty("Authorization", basicAuth);
//
//        InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
//        // read this input
//
//    }

    public BufferedImage readImageFromUriOld(String urlString) throws Exception {
        String str = "";
        if (urlString.contains("commons.wikimedia"))
            str = urlString.replace("FilePath/", "Redirect/file/");
        else
            str = urlString;


        URL url = new URL(str);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setRequestProperty("User-Agent", "runscope/0.1");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        urlConnection.connect();

        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            URL redirectUrl = new URL(urlConnection.getHeaderField("Location"));
            System.out.println(redirectUrl);
        }

        return ImageIO.read(urlConnection.getInputStream());
//        return ImageIO.read(new URL(urlSt
    }

    public BufferedImage convertRenderedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable<String, Object> properties = new Hashtable<>();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);

        return result;
    }

    public String getImageType(String base64) {
        return base64.split(":")[1].split(";")[0];
    }


    public String getImageFilename(String uuid){
        return uuid + ".jpg";
    }

    public String getScaledImageFilename(String uuid){
        return uuid + "_k." + "jpg";
    }

    public String saveScaledJpegPngImage(BufferedImage bufferedImage, String uuid) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();

        byte[] bytes = baos.toByteArray();
        baos.close();

        InputStream in = new ByteArrayInputStream(bytes);
        File file = File.createTempFile("uuid", "." + "jpg");
        file.deleteOnExit();

        OutputStream os = new FileOutputStream(file);
        IOUtils.copy(in, os);
        os.close();

        BufferedImage image = new JpegReader().readImage(file);

        int height = (int) (((double) SCALED_IMAGE_WIDTH / image.getWidth()) * image.getHeight());
        BufferedImage scaledImage = getScaledImage(image, SCALED_IMAGE_WIDTH, height);

        String smallFileName = uuid + "_k." + "jpg";
        File smallTargetFile = new File(KB_IMG_LOCATION + smallFileName);

        ImageIO.write(scaledImage, "jpg", smallTargetFile);

//        ImageData im = new ImageData(smallFileName, smallTargetFile.length(), image.getWidth(), image.getHeight(), getDPI(smallTargetFile), extension);
//        jdbcProductRepository.saveImage(im);

        return smallFileName;
    }

    public String saveJpegPngImage(BufferedImage bufferedImage, String uuid) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();

        byte[] bytes = baos.toByteArray();
        baos.close();

        InputStream in = new ByteArrayInputStream(bytes);
        File file = File.createTempFile("uuid", "." + "jpg");
        file.deleteOnExit();

        OutputStream os = new FileOutputStream(file);
        IOUtils.copy(in, os);
        os.close();

        BufferedImage image = new JpegReader().readImage(file);

        String fileName = uuid + ".jpg";
        File targetFile = new File(KB_IMG_LOCATION + fileName);

        ImageIO.write(image, "jpg", targetFile);

//        ImageData im = new ImageData(smallFileName, smallTargetFile.length(), image.getWidth(), image.getHeight(), getDPI(smallTargetFile), extension);
//        jdbcProductRepository.saveImage(im);

        return fileName;
    }

    // Sonradan ekledim
    public String convertPngToJpegSmall(byte[] bytes, String uuid) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);

        BufferedImage image = ImageIO.read(in);

        // create a blank, RGB, same width and height, and a white background
        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        int height = (int) (((double) SCALED_IMAGE_WIDTH / newBufferedImage.getWidth()) * newBufferedImage.getHeight());
        BufferedImage scaledImage = getScaledImage(newBufferedImage, SCALED_IMAGE_WIDTH, height);

        String fileName = uuid + "_k.jpg";
        saveJpeg(scaledImage, 300, 0.99f, KB_IMG_LOCATION + fileName, fileName);

        File file = new File(fileName);
        ImageData im = new ImageData(fileName, file.length(), image.getWidth(), image.getHeight(), getDPI(file), "jpeg");

//        jdbcProductRepository.saveImage(im);

        return fileName;
    }

    public String convertPngToJpeg(byte[] bytes, String uuid) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);

        BufferedImage image = ImageIO.read(in);

        // create a blank, RGB, same width and height, and a white background
        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        String fileName = uuid + ".jpg";
        saveJpeg(newBufferedImage, 300, 0.99f, KB_IMG_LOCATION + fileName, fileName);

        ImageData im = new ImageData(fileName, new File(fileName).length(), image.getWidth(), image.getHeight(), 300, "jpeg");

//        jdbcProductRepository.saveImage(im);

        return fileName;
    }

    public String saveJpegImage(byte[] bytes, String uuid, String extension) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File file = File.createTempFile("uuid", "." + extension);
        file.deleteOnExit();

        OutputStream os = new FileOutputStream(file);
        IOUtils.copy(in, os);
        os.close();

        BufferedImage bufferedImage = new JpegReader().readImage(file);

        String fileName = uuid + "." + extension;
        saveJpeg(bufferedImage, 300, 0.99f, KB_IMG_LOCATION + fileName, fileName);

        ImageData im = new ImageData(fileName, new File(fileName).length(), bufferedImage.getWidth(), bufferedImage.getHeight(), 300, "jpeg");

//        jdbcProductRepository.saveImage(im);

        return fileName;
    }

//    public String saveJpegImage(BufferedImage bf, String uuid, String extension) throws Exception {
//        InputStream in = new ByteArrayInputStream(bytes);
//        File file = File.createTempFile("uuid", "." + extension);
//        file.deleteOnExit();
//
//        OutputStream os = new FileOutputStream(file);
//        IOUtils.copy(in, os);
//        os.close();
//
//        BufferedImage bufferedImage = new JpegReader().readImage(file);
//
//        String fileName = uuid + "." + extension;
//        saveJpeg(bf, 300, 0.99f, KB_IMG_LOCATION + fileName, fileName);
//
//        ImageData im = new ImageData(fileName, new File(fileName).length(), bufferedImage.getWidth(), bufferedImage.getHeight(), 300, "jpeg");
//
////        jdbcProductRepository.saveImage(im);
//
//        return fileName;
//    }

    public String savePngImage(byte[] bytes, String uuid, String extension) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);

        BufferedImage bufferedImage = ImageIO.read(in);

        String fileName = uuid + "." + extension;
        savePng(bufferedImage, KB_IMG_LOCATION + fileName, fileName);

        File file = new File(fileName);
        ImageData im = new ImageData(fileName, file.length(), bufferedImage.getWidth(), bufferedImage.getHeight(), getDPI(file), "png");

//        jdbcProductRepository.saveImage(im);

        return fileName;
    }


    public BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        BufferedImage thumbnail =
                Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                        width, height, Scalr.OP_ANTIALIAS);

        return thumbnail;
    }

    public void saveJpeg(BufferedImage bufferedImage, int dpi, float quality, String file, String fileName) throws Exception {

        // Image writer
        JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File(file));
        imageWriter.setOutput(ios);

        // Compression
        JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
        jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);

        // Metadata (dpi)
        IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bufferedImage), jpegParams);
        Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
        Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
        jfif.setAttribute("Xdensity", Integer.toString(dpi));
        jfif.setAttribute("Ydensity", Integer.toString(dpi));
        jfif.setAttribute("resUnits", "1"); // density is dots per inch

        data.setFromTree("javax_imageio_jpeg_image_1.0", tree);

        // Write and clean up
        imageWriter.write(null, new IIOImage(bufferedImage, null, data), jpegParams);
        ios.close();
        imageWriter.dispose();
    }

    private void savePng(BufferedImage image, String file, String fileName) throws Exception {
        File output = new File(file);

        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }

    private void setDPI(IIOMetadata metadata) throws Exception {

        // for PMG, it's dots per millimeter
        double dotsPerMilli = 1.0 * 300 / 10 / 2.54;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    public int getDPI(MultipartFile file) throws IOException {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(multipartToFile(file));
            Iterator<ImageReader> it = ImageIO.getImageReaders(iis);

            if (!it.hasNext()) {
                System.err.println("No reader for this format");
                return 0;
            }

            ImageReader reader = (ImageReader) it.next();
            reader.setInput(iis);

            IIOMetadata meta = reader.getImageMetadata(0);
            IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree("javax_imageio_1.0");
            NodeList nodes = root.getElementsByTagName("HorizontalPixelSize");
            if (nodes.getLength() > 0) {
                IIOMetadataNode dpcWidth = (IIOMetadataNode) nodes.item(0);
                NamedNodeMap nnm = dpcWidth.getAttributes();
                Node item = nnm.item(0);
                int xDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
                System.out.println("xDPI: " + xDPI);
            } else {
                System.out.println("xDPI: -");
            }

            if (nodes.getLength() > 0) {
                nodes = root.getElementsByTagName("VerticalPixelSize");
                IIOMetadataNode dpcHeight = (IIOMetadataNode) nodes.item(0);
                NamedNodeMap nnm = dpcHeight.getAttributes();
                Node item = nnm.item(0);
                int yDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
                System.out.println("yDPI: " + yDPI);
                return yDPI;
            } else {
                System.out.println("yDPI: -");
                return 0;
            }
        } catch (Exception e) {
            logger.error("Error at getDPI : ", e);
            return 0;
        }
    }

    public int getDPI(File file) throws IOException {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> it = ImageIO.getImageReaders(iis);

            if (!it.hasNext()) {
                System.err.println("No reader for this format");
                return 0;
            }

            ImageReader reader = (ImageReader) it.next();
            reader.setInput(iis);

            IIOMetadata meta = reader.getImageMetadata(0);
            IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree("javax_imageio_1.0");
            NodeList nodes = root.getElementsByTagName("HorizontalPixelSize");
            if (nodes.getLength() > 0) {
                IIOMetadataNode dpcWidth = (IIOMetadataNode) nodes.item(0);
                NamedNodeMap nnm = dpcWidth.getAttributes();
                Node item = nnm.item(0);
                int xDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
                System.out.println("xDPI: " + xDPI);
            } else {
                System.out.println("xDPI: -");
            }

            if (nodes.getLength() > 0) {
                nodes = root.getElementsByTagName("VerticalPixelSize");
                IIOMetadataNode dpcHeight = (IIOMetadataNode) nodes.item(0);
                NamedNodeMap nnm = dpcHeight.getAttributes();
                Node item = nnm.item(0);
                int yDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
                System.out.println("yDPI: " + yDPI);
                return yDPI;
            } else {
                System.out.println("yDPI: -");
                return 0;
            }
        } catch (Exception e) {
            logger.error("Error at getDPI : ", e);
            return 0;
        }
    }

    public BufferedImage getScaledImage3(BufferedImage image, int width, int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
                image,
                new BufferedImage(width, height, image.getType()));
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = new File(multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }

    public int[] byteArrayToIntArray(byte[] bytes) {
        int[] arr = new int[bytes.length / 4];

        for (int i = 0; i < bytes.length / 4; i = i + 4) {
            arr[i] = byteToInt(new byte[]{bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3]});
        }

        return arr;
    }

    public int byteToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }

        return result;
    }

}
