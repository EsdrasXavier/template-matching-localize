package br.edu.catolicasc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

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

            File directory = new File("./grayscale");
            if (!directory.exists()) {
                directory.mkdir();
            }

            String name = "./grayscale/".concat(fileName.concat("_grayscale.").concat(fileExtension));
            result = new File(name);
            ImageIO.write(image, "png", result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done!");
        return result;
    }

    public static void templateMatching(File templateImg, File searchImg) {
        double minSAD = Double.MAX_VALUE;
        int finalX = 0;
        int finalY = 0;
        try {
            BufferedImage templateBuffer = ImageIO.read(templateImg);
            BufferedImage searchBuffer = ImageIO.read(searchImg);

            int templateWidth = templateBuffer.getWidth();
            int templateHeight = templateBuffer.getHeight();

            int searchWidth = searchBuffer.getWidth();
            int searchHeight = searchBuffer.getHeight();

            System.out.println("Rows: " + Math.abs(searchHeight - templateHeight) + " - Cols: "
                    + Math.abs(searchWidth - templateWidth));

            for (int x = 0; x < templateWidth - searchWidth; x += 5) {
                System.out.println("Current row: " + x);
                for (int y = 0; y < templateHeight - searchHeight; y += 5) {
                    double sad = 0.0;

                    for (int searchX = 0; searchX < searchWidth; searchX += 5) {
                        for (int searchY = 0; searchY < searchHeight; searchY += 5) {
                            int pixelSearch = searchBuffer.getRGB(searchX, searchY);
                            int pixelTemplate = templateBuffer.getRGB(x + searchX, y + searchY);

                            sad += Math.abs(pixelSearch - pixelTemplate);
                        }
                    }
                    if (minSAD > sad) {
                        minSAD = sad;
                        finalX = x;
                        finalY = y;
                        System.out.println("Best Row: " + x);
                        System.out.println("Best Col: " + y);
                        System.out.println("Best SAD: " + sad);
                    }
                }
            }

            System.out.println("Done.");
            Graphics2D g2d = templateBuffer.createGraphics();
            g2d.setColor(Color.GREEN);
            BasicStroke bs = new BasicStroke(2);
            g2d.setStroke(bs);

            g2d.drawLine(finalX, finalY, finalX + searchWidth, finalY);
            g2d.drawLine(finalX, finalY + searchHeight, finalX + searchWidth, finalY + searchHeight);

            g2d.drawLine(finalX, finalY, finalX, finalY + searchHeight);
            g2d.drawLine(finalX + searchWidth, finalY, finalX + searchWidth, finalY + searchHeight);

            ImageIcon ii = new ImageIcon(templateBuffer);
            JOptionPane.showMessageDialog(null, ii);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}