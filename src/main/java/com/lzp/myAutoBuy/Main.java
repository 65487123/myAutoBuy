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


    private static int expectedWidthG;
    private static int expectedHeightG ;
    private static int[] expectedPixelsG;

    private static int expectedWidthBo;
    private static int expectedHeightBo ;
    private static int[] expectedPixelsBo;

    private static int expectedWidthG1;
    private static int expectedHeightG1 ;
    private static int[] expectedPixelsG1;
    private static Rectangle captureRect;

    private static Point[] points= new Point[6];

    private static long  delay = 100;

    static {
        JOptionPane optionPane = new JOptionPane("请确保程序启动前购买窗口已打开！", JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog("提示");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
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

            BufferedImage expectedImageG = ImageIO.read(new File("./gold.png"));
            expectedWidthG = expectedImageG.getWidth();
            expectedHeightG = expectedImageG.getHeight();
            expectedPixelsG = expectedImageG.getRGB(0, 0, expectedWidthG, expectedHeightG, null, 0, expectedWidthG);

            BufferedImage expectedImageG1 = ImageIO.read(new File("./gold1.png"));
            expectedWidthG1 = expectedImageG1.getWidth();
            expectedHeightG1 = expectedImageG1.getHeight();
            expectedPixelsG1 = expectedImageG1.getRGB(0, 0, expectedWidthG1, expectedHeightG1, null, 0, expectedWidthG1);

            BufferedImage expectedImageBo = ImageIO.read(new File("./box.png"));
            expectedWidthBo = expectedImageBo.getWidth();
            expectedHeightBo = expectedImageBo.getHeight();
            expectedPixelsBo = expectedImageBo.getRGB(0, 0, expectedWidthBo, expectedHeightBo, null, 0, expectedWidthBo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        captureRect =generCaptureRect(Toolkit.getDefaultToolkit().getScreenSize());
        optionPane = new JOptionPane("现在可以把购买窗口关闭了,接下来要定位六个所需坐标", JOptionPane.INFORMATION_MESSAGE);
        dialog = optionPane.createDialog("提示");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private static Rectangle generCaptureRect(Dimension screenSize){
        try {
            BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(0,0,screenSize.width/2,
                    screenSize.height/2));
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
                        return new Rectangle(x1, y1, screenSize.width / 12, screenSize.height / 12);
                    }
                }
            }

        }catch (Exception ignored){
        }
        return null;
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
            //for (int l=0; l<5000;l++){
            for (;;){
                robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
                mousePressAndRelease(robot);
                waitUntilBoxAppear(robot);
                robot.mouseMove((int) points[1].getX(), (int) points[1].getY());
                mousePressAndRelease(robot);
                waitUntilGlodAppear(robot);
                robot.mouseMove((int) points[2].getX(), (int) points[2].getY());
                mousePressAndRelease(robot);
                waitUntilGlod1Appear(robot);
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

    private static boolean waitUntilGlodAppear(Robot robot) throws InterruptedException {
        long time  = System.currentTimeMillis();
        Thread.sleep(30);
        while (!containGold(robot)){
            if (System.currentTimeMillis() - time > 300) {
                return false;
            }
        }
        return true;
    }

    private static boolean waitUntilGlod1Appear(Robot robot) throws InterruptedException {
        long time  = System.currentTimeMillis();
        Thread.sleep(10);
        while (!containGold1(robot)){
            if (System.currentTimeMillis() - time > 300) {
                return false;
            }
        }
        return true;
    }

    private static boolean waitUntilBoxAppear(Robot robot) throws InterruptedException {
        long time  = System.currentTimeMillis();
        Thread.sleep(50);
        while (!containBox(robot)){
            if (System.currentTimeMillis() - time > 300) {
                return false;
            }
        }
        return true;
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
    private static boolean  containBox(Robot robot){
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsBo[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightBo; y++) {
                        for (int x = 0; x < expectedWidthBo; x++) {
                            int expectedPixel = expectedPixelsBo[y * expectedWidthBo + x];
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

    private static boolean  containGold1(Robot robot){
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsG1[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightG1; y++) {
                        for (int x = 0; x < expectedWidthG1; x++) {
                            int expectedPixel = expectedPixelsG1[y * expectedWidthG1 + x];
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

    private static boolean  containGold(Robot robot){
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsG[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightG; y++) {
                        for (int x = 0; x < expectedWidthG; x++) {
                            int expectedPixel = expectedPixelsG[y * expectedWidthG + x];
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
        try {
            int capturedWidth = capturedImage.getWidth();
            int capturedHeight = capturedImage.getHeight();
            int[] capturedPixels = capturedImage.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
            return matchA(capturedWidth, capturedHeight, capturedPixels)
                    || matchB(capturedWidth, capturedHeight, capturedPixels)
                /*|| matchC(capturedWidth, capturedHeight, capturedPixels)
                || matchD(capturedWidth, capturedHeight, capturedPixels)*/;
        }catch (Exception e){
            //空指针异常,一般只有在客户端卡顿的时候会出现
            return false;
        }
    }


    private static boolean matchA(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsA[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightA; y++) {
                        for (int x = 0; x < expectedWidthA; x++) {
                            int expectedPixel = expectedPixelsA[y * expectedWidthA + x];
                            int capturedPixel = capturedPixels[(y + y1) * capturedWidth + (x + x1)];

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

    private static boolean matchB(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsB[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightB; y++) {
                        for (int x = 0; x < expectedWidthB; x++) {
                            int expectedPixel = expectedPixelsB[y * expectedWidthB + x];
                            int capturedPixel = capturedPixels[(y + y1) * capturedWidth + (x + x1)];

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
                            int capturedPixel = capturedPixels[(y + y1) * capturedWidth + (x + x1)];

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

    private static boolean matchD(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsD[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightD; y++) {
                        for (int x = 0; x < expectedWidthD; x++) {
                            int expectedPixel = expectedPixelsD[y * expectedWidthD + x];
                            int capturedPixel = capturedPixels[(y + y1) * capturedWidth + (x + x1)];

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