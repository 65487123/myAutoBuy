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



    private static Rectangle captureRect;

    private static Point[] points= new Point[6];

    private static long  delay = 100;

    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        captureRect = new Rectangle(screenSize.width / 9, screenSize.height / 9, screenSize.width / 2, screenSize.height / 2);

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

            BufferedImage expectedImageD = ImageIO.read(new File("./f.png"));
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
                    Thread.sleep(800);
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
            //for (int l=0; l<100;l++){
            for (;;){
                robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
                mousePressAndRelease(robot);
                Thread.sleep(1);
                robot.mouseMove((int) points[1].getX(), (int) points[1].getY());
                mousePressAndRelease(robot);
                Thread.sleep(120);
                robot.mouseMove((int) points[2].getX(), (int) points[2].getY());
                mousePressAndRelease(robot);
                Thread.sleep(35);
                robot.mouseMove((int) points[3].getX(), (int) points[3].getY());
                BufferedImage screenCapture =robot.createScreenCapture(captureRect);
                if (match(screenCapture)) {
                    for (int i = 3; i < points.length; i++) {
                        robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                        mousePressAndRelease(robot);
                        if (i==3){
                            if (!waitUntilWindowAppear(robot)) {
                                //可能原因是点到了上个查询出来的数据,然后判断前被新数据覆盖了
                                //不过也不确定有没有这种情况
                                break;
                            }
                        }
                    }
                }else {
                    //System.out.println("没抓到");
                }
            }
        }
    }

    private static boolean waitUntilWindowAppear(Robot robot) throws InterruptedException {
        long time  = System.currentTimeMillis();
        while (!containBuyingWindow(robot)){
            if (System.currentTimeMillis() - time > 1000) {
                return false;
            }
        }
        return true;
    }



    private static  void mousePressAndRelease(Robot robot){
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(1);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static boolean  containBuyingWindow(Robot robot){
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
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
                /*|| matchC(capturedWidth, capturedHeight, capturedPixels)
                || matchD(capturedWidth, capturedHeight, capturedPixels)*/;

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