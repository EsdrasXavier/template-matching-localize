package br.edu.catolicasc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static File convertToGrayscale(String imageName) {
        InputStream inputStream = ImageUtils.class.getResourceAsStream(imageName);
        File result = null;

        if (inputStream == null) {
            System.out.println("Could not find img '".concat(imageName).concat("'"));
            return result;
        }

        try {
            System.out.println("Converting image: ".concat(imageName));
            BufferedImage image = ImageIO.read(inputStream);
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb & 0xFF);

                    // Normalize and gamma correct
                    double rr = Math.pow(r / 255.0, 2.2);
                    double gg = Math.pow(g / 255.0, 2.2);
                    double bb = Math.pow(b / 255.0, 2.2);

                    // Calculate luminance
                    double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

                    // Gamma compand and rescale to byte range
                    int grayLevel = (int) (255.0 * Math.pow(lum, 1.0 / 2.2));
                    int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                    image.setRGB(x, y, gray);
                }
            }

            final String fileName = FilenameUtils.removeExtension(imageName);
            final String fileExtension = FilenameUtils.getExtension(imageName);
            String name = "./".concat(fileName.concat("_grayscale.").concat(fileExtension));
            result = new File(name);
            ImageIO.write(image, "png", result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done!");
        return result;
    }

    public static void templateMatching(File templateImg, File searchImg) {
        double minSAD = Integer.MAX_VALUE;
        try {
            BufferedImage templateBuffer = ImageIO.read(templateImg);
            BufferedImage searchBuffer = ImageIO.read(searchImg);

            int templateWidth = templateBuffer.getWidth();
            int templateHeight = templateBuffer.getHeight();

            int searchWidth = searchBuffer.getWidth();
            int searchHeight = searchBuffer.getHeight();

            System.out.println("Rows: " + Math.abs(searchHeight - templateHeight) + " - Cols: "
                    + Math.abs(searchWidth - templateWidth));

            for (int x = 0; x <= Math.abs(searchHeight - templateHeight); ++x) {
                System.out.println("Current row: " + x);
                for (int y = 0; y <= Math.abs(searchWidth - templateWidth); ++y) {
                    double sad = 0.0;

                    for (int j = 0; j < templateHeight; j++)
                        for (int i = 0; i < templateWidth; i++) {
                            if (x + j < searchWidth && y + i < searchHeight) {
                                int pixelSearch = searchBuffer.getRGB(x + j, y + i);
                                int pixelTemplate = templateBuffer.getRGB(i, j);

                                sad += Math.abs(pixelSearch - pixelTemplate);
                            }
                        }

                    if (minSAD > sad) {
                        minSAD = sad;
                        System.out.println("Best Row: " + y);
                        System.out.println("Best Col: " + x);
                        System.out.println("Best SAD: " + sad);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}