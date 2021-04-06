package br.edu.catolicasc;

public class App {

    private static final String COMPLETE_IMG = "/case_1.jpg";
    private static final String PIECE_TO_COMPARE = "/case_1_o.jpg";

    public static void main(String[] args) {
        ImageUtils.templateMatching(ImageUtils.convertToGrayscale(COMPLETE_IMG),
                ImageUtils.convertToGrayscale(PIECE_TO_COMPARE));
    }
}
