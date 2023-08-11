package com.lzp.myAutoBuy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class Main {

    private static int expectedWidthA ;
    private static int expectedHeightA ;
    private static int[] expectedPixelsA;

    private static int expectedWidthB ;
    private static int expectedHeightB ;
    private static int[] expectedPixelsB;

    private static int expectedWidthC ;
    private static int expectedHeightC ;
    private static int[] expectedPixelsC;

    private static int expectedWidthD ;
    private static int expectedHeightD ;
    private static int[] expectedPixelsD;

    private static int expectedWidthW;
    private static int expectedHeightW ;
    private static int[] expectedPixelsW;


    private static Point[] points= new Point[6];

    private static volatile boolean shutdown;

    static {
        try {
            BufferedImage expectedImageA = ImageIO.read(new File("./a.png"));
            expectedWidthA = expectedImageA.getWidth();
            expectedHeightA = expectedImageA.getHeight();
            expectedPixelsA = expectedImageA.getRGB(0, 0, expectedWidthA, expectedHeightA, null, 0, expectedWidthA);

            BufferedImage expectedImageB = ImageIO.read(new File("./b.png"));
            expectedWidthB = expectedImageB.getWidth();
            expectedHeightB = expectedImageB.getHeight();
            expectedPixelsB = expectedImageB.getRGB(0, 0, expectedWidthB, expectedHeightB, null, 0, expectedWidthB);

            BufferedImage expectedImageC = ImageIO.read(new File("./c.png"));
            expectedWidthC = expectedImageC.getWidth();
            expectedHeightC = expectedImageC.getHeight();
            expectedPixelsC = expectedImageC.getRGB(0, 0, expectedWidthC, expectedHeightC, null, 0, expectedWidthC);

            BufferedImage expectedImageD = ImageIO.read(new File("./d.png"));
            expectedWidthD = expectedImageD.getWidth();
            expectedHeightD = expectedImageD.getHeight();
            expectedPixelsD = expectedImageD.getRGB(0, 0, expectedWidthD, expectedHeightD, null, 0, expectedWidthD);
            BufferedImage expectedImageW = ImageIO.read(new File("./window.png"));
            expectedWidthW = expectedImageW.getWidth();
            expectedHeightW = expectedImageW.getHeight();
            expectedPixelsW = expectedImageW.getRGB(0, 0, expectedWidthW, expectedHeightW, null, 0, expectedWidthW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();

        new Thread(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                points[i] = MouseInfo.getPointerInfo().getLocation();
                JOptionPane optionPane = new JOptionPane("第"+(i+1)+"个位置已记录！", JOptionPane.INFORMATION_MESSAGE);
                final JDialog dialog = optionPane.createDialog("提示");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
            synchronized (Main.class){
                Main.class.notify();
            }
        }).start();

        synchronized (Main.class) {
            Main.class.wait();
            for (; ;){
                robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
                mousePressAndRelease(robot);

                robot.mouseMove((int) points[1].getX(), (int) points[1].getY());
                mousePressAndRelease(robot);
                Thread.sleep(100);
                BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                BufferedImage windowImage = screenShot.getSubimage(0, 0, screenShot.getWidth() / 2, screenShot.getHeight() / 2);
                if (match(windowImage)) {
                    for (int i = 2; i < points.length; i++) {
                        if (i == 3) {
                            robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                            screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                            windowImage = screenShot.getSubimage(0, 0, screenShot.getWidth() / 2, screenShot.getHeight() / 2);
                            if (match(windowImage)) {
                                mousePressAndRelease(robot);
                            }
                            if (!waitUntilWindowAppear(robot)) {
                                break;
                            }
                        } else {
                            robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                            mousePressAndRelease(robot);
                        }
                    }
                }
            }
        }
    }

    private static boolean waitUntilWindowAppear(Robot robot) throws InterruptedException {
        long time  = System.currentTimeMillis();
        while (!containBuyingWindow(robot)){
            Thread.sleep(10);
            if (System.currentTimeMillis() - time > 1000) {
                return false;
            }
        }
        return true;
    }

    private static  void mousePressAndRelease(Robot robot){
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(20);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static boolean  containBuyingWindow(Robot robot){
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        BufferedImage windowImage = screenShot.getSubimage(0, 0, screenShot.getWidth() / 2, screenShot.getHeight() / 2);
        int capturedWidth = windowImage.getWidth();
        int capturedHeight = windowImage.getHeight();
        int[] capturedPixels = windowImage.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsW[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightW; y++) {
                        for (int x = 0; x < expectedWidthW; x++) {
                            int expectedPixel = expectedPixelsW[y * expectedWidthW + x];
                            int capturedPixel = capturedPixels[(y+y1) * capturedWidth + (x+x1)];

                            if (expectedPixel != capturedPixel) {
                                continue a;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean match(BufferedImage capturedImage) {

        int capturedWidth = capturedImage.getWidth();
        int capturedHeight = capturedImage.getHeight();
        int[] capturedPixels = capturedImage.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        return matchA(capturedWidth, capturedHeight, capturedPixels)
                || matchB(capturedWidth, capturedHeight, capturedPixels)
                || matchC(capturedWidth, capturedHeight, capturedPixels)
                || matchD(capturedWidth, capturedHeight, capturedPixels);

    }


    private static boolean matchA(int capturedWidth,int capturedHeight,int[] capturedPixels){
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsA[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightA; y++) {
                        for (int x = 0; x < expectedWidthA; x++) {
                            int expectedPixel = expectedPixelsA[y * expectedWidthA + x];
                            int capturedPixel = capturedPixels[(y+y1) * capturedWidth + (x+x1)];

                            if (expectedPixel != capturedPixel) {
                                continue a;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchB(int capturedWidth,int capturedHeight,int[] capturedPixels){
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsB[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightB; y++) {
                        for (int x = 0; x < expectedWidthB; x++) {
                            int expectedPixel = expectedPixelsB[y * expectedWidthB + x];
                            int capturedPixel = capturedPixels[(y+y1) * capturedWidth + (x+x1)];

                            if (expectedPixel != capturedPixel) {
                                continue a;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchC(int capturedWidth,int capturedHeight,int[] capturedPixels){
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsC[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightC; y++) {
                        for (int x = 0; x < expectedWidthC; x++) {
                            int expectedPixel = expectedPixelsC[y * expectedWidthC + x];
                            int capturedPixel = capturedPixels[(y+y1) * capturedWidth + (x+x1)];

                            if (expectedPixel != capturedPixel) {
                                continue a;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchD(int capturedWidth,int capturedHeight,int[] capturedPixels){
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsD[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightD; y++) {
                        for (int x = 0; x < expectedWidthD; x++) {
                            int expectedPixel = expectedPixelsD[y * expectedWidthD + x];
                            int capturedPixel = capturedPixels[(y+y1) * capturedWidth + (x+x1)];

                            if (expectedPixel != capturedPixel) {
                                continue a;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


}