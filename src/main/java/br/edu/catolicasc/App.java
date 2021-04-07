package br.edu.catolicasc;

import java.awt.*;
import java.awt.image.*;
import java.io.File;

import javax.swing.*;

public class App {

    private static final String COMPLETE_IMG = "/case_1.jpg";
    private static final String PIECE_TO_COMPARE = "/case_1_o.jpg";

    public static void main(String[] args) {
        File template = ImageUtils.convertToGrayscale(COMPLETE_IMG);
        File searchImg = ImageUtils.convertToGrayscale(PIECE_TO_COMPARE);
        Point initialPoint = ImageUtils.templateMatching(template, searchImg);
        ImageUtils.drawLine(template, initialPoint, ImageUtils.getSize(searchImg));
    }
}
