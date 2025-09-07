
package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {


    private static final String VIDEO_PATH = "C:\\Users\\Personal Desk\\Desktop\\bad_apple.mp4";
    private static final String OUTPUT_DIR = "C:\\Users\\Personal Desk\\Desktop\\frames_png";

    private static final int CONSOLE_WIDTH = 120;
    private static final int CONSOLE_HEIGHT = 40;

    private static final char[] GRAYSCALE_CHARS = {'@', '#', 'S', '%', '?', '*', '+', ';', ':', ',', '.'};
    private static final int NUM_CHARS = GRAYSCALE_CHARS.length;
    private static final int FONT_SIZE = 10;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCV native library loaded");

        VideoCapture cap = new VideoCapture(VIDEO_PATH);

        if (!cap.isOpened()) {
            System.err.println("Could not open video file");
            return;
        }

        System.out.println("Starting frame export to images...");

        new File(OUTPUT_DIR).mkdirs();

        Mat frame = new Mat();
        Mat resizedFrame = new Mat();
        int frameCount = 0;


        int imageWidth = CONSOLE_WIDTH * (FONT_SIZE / 2); //approx character width
        int imageHeight = CONSOLE_HEIGHT * FONT_SIZE;

        while (cap.read(frame)) {
            Imgproc.resize(frame, resizedFrame, new Size(CONSOLE_WIDTH, CONSOLE_HEIGHT), 0, 0, Imgproc.INTER_AREA);
            Imgproc.cvtColor(resizedFrame, resizedFrame, Imgproc.COLOR_BGR2GRAY);

            // Create a new image for each frame
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, imageWidth, imageHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE));

            for (int row = 0; row < CONSOLE_HEIGHT; row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < CONSOLE_WIDTH; col++) {
                    double pixelValue = resizedFrame.get(row, col)[0];
                    int charIndex = (int) (pixelValue / 256.0 * NUM_CHARS);

                    if (charIndex >= NUM_CHARS) {
                        charIndex = NUM_CHARS - 1;
                    }
                    if (charIndex < 0) {
                        charIndex = 0;
                    }
                    line.append(GRAYSCALE_CHARS[charIndex]);
                }
                g.drawString(line.toString(), 0, (row + 1) * FONT_SIZE);
            }

            g.dispose();

            try {
                ImageIO.write(image, "png", new File(String.format(OUTPUT_DIR + "/frame_%05d.png", frameCount)));
            } catch (IOException e) {
                System.err.println("Error writing image file: " + e.getMessage());
                break;
            }
            frameCount++;
        }

        cap.release();
        System.out.println("Export finished. Total frames: " + frameCount);
    }
}