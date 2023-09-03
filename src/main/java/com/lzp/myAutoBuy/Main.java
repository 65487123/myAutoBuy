package com.lzp.myAutoBuy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    private static int expectedWidthA;
    private static int expectedHeightA;
    private static int[] expectedPixelsA;

    private static int expectedWidthB;
    private static int expectedHeightB;
    private static int[] expectedPixelsB;

    private static int expectedWidthC;
    private static int expectedHeightC;
    private static int[] expectedPixelsC;

    private static int expectedWidthD;
    private static int expectedHeightD;
    private static int[] expectedPixelsD;

    private static int expectedWidthW;
    private static int expectedHeightW;
    private static int[] expectedPixelsW;


    private static int expectedWidthQ;
    private static int expectedHeightQ;
    private static int[] expectedPixelsQ;
    private static Rectangle captureRect;


    private static int[] captureRectArray;

    private static Method method;
    private static Point[] points = new Point[5];

    private static AtomicInteger timeoutCount = new AtomicInteger();
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

            BufferedImage expectedImageD = ImageIO.read(new File("./d.png"));
            expectedWidthD = expectedImageD.getWidth();
            expectedHeightD = expectedImageD.getHeight();
            expectedPixelsD = expectedImageD.getRGB(0, 0, expectedWidthD, expectedHeightD, null, 0, expectedWidthD);

            BufferedImage expectedImageW = ImageIO.read
                    (new File("./window.png"));
            expectedWidthW = expectedImageW.getWidth();
            expectedHeightW = expectedImageW.getHeight();
            expectedPixelsW = expectedImageW.getRGB(0, 0, expectedWidthW, expectedHeightW, null, 0, expectedWidthW);


            BufferedImage expectedImageQ = ImageIO.read(new File("./querybox.png"));
            expectedWidthQ = expectedImageQ.getWidth();
            expectedHeightQ = expectedImageQ.getHeight();
            expectedPixelsQ = expectedImageQ.getRGB(0, 0, expectedWidthQ, expectedHeightQ, null, 0, expectedWidthQ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        captureRect = generCaptureRect(Toolkit.getDefaultToolkit().getScreenSize());

        captureRectArray = new int[captureRect.width * captureRect.height];

        optionPane = new JOptionPane("现在可以把购买窗口关闭了,接下来要定位5个所需坐标,如已定位过,无需再次定位", JOptionPane.INFORMATION_MESSAGE);
        dialog = optionPane.createDialog("提示");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }


    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        RobotPeer robotPeer = getRobotPeer(robot);
        method = robotPeer.getClass().getDeclaredMethod("getRGBPixels",int.class,int.class,int.class,int.class,int[].class);
        method.setAccessible(true);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("points.obj"));
                points = (Point[]) objectInputStream.readObject();
            }catch (Exception e){
                for(int i = 0; i < 5; ++i) {
                    try {
                        Thread.sleep(800L);
                    } catch (InterruptedException ignored) {
                    }

                    points[i] = MouseInfo.getPointerInfo().getLocation();
                    JOptionPane optionPane = new JOptionPane("第" + (i + 1) + "个位置已记录！", JOptionPane.INFORMATION_MESSAGE);
                    JDialog dialog = optionPane.createDialog("提示");
                    dialog.setAlwaysOnTop(true);
                    dialog.setVisible(true);
                }
                try {
                    new ObjectOutputStream(Files.newOutputStream(Paths.get("points.obj"))).writeObject(points);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();

        //for (int l=0; l<5000;l++){


        while (true) {
                for (int l = 0; l <12000 ; l++) {
                    try {
                        long now = System.currentTimeMillis();
                        openMarket(robot);
                        waitUntilQueryBoxComeAndGone(robotPeer);
                        if (match()) {
                            for (int i = 0; i < points.length-1 ; i++) {
                                robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                                mousePressAndRelease(robot);
                                if (i == 1 && !waitUntilWindowAppear(robotPeer)) {
                                    break;
                                }
                            }
                            Thread.sleep(300);
                        }
                        robot.mouseMove((int) points[4].getX(), (int) points[4].getY());
                        mousePressAndRelease(robot);
                        System.out.println(System.currentTimeMillis() - now);
                    } catch (Exception ignored) {
                    }
                }
                Thread.sleep(1000);
                logOutAndLogin(robot);
            }
    }

    private static void openMarket(Robot robot){
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_A);
    }

    private static RobotPeer getRobotPeer(Robot robot) throws NoSuchFieldException, IllegalAccessException {
        Field peerField = robot.getClass().getDeclaredField("peer");
        peerField.setAccessible(true);
        return (RobotPeer) peerField.get(robot);
    }

    private static Rectangle generCaptureRect(Dimension screenSize) {
        try {
            BufferedImage screenCapture = (new Robot()).createScreenCapture(new Rectangle(0, 0, screenSize.width / 2, screenSize.height / 2));
            int capturedWidth = screenCapture.getWidth();
            int capturedHeight = screenCapture.getHeight();
            int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);

            for(int y1 = 0; y1 < capturedHeight; ++y1) {
                label48:
                for(int x1 = 0; x1 < capturedWidth; ++x1) {
                    int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                    if (expectedPixelsW[0] == capturedPixel1) {
                        for(int y = 0; y < expectedHeightW; ++y) {
                            for(int x = 0; x < expectedWidthW; ++x) {
                                int expectedPixel = expectedPixelsW[y * expectedWidthW + x];
                                int capturedPixel = capturedPixels[(y + y1) * capturedWidth + x + x1];
                                if (expectedPixel != capturedPixel) {
                                    continue label48;
                                }
                            }
                        }

                        return new Rectangle(x1, y1, (screenSize.width / 22)*3/2, (screenSize.height / 13)*9/4);
                    }
                }
            }
        } catch (Exception var12) {
        }

        throw new RuntimeException("generCaptureRect failed");
    }

    private static void waitUntilQueryBoxComeAndGone(RobotPeer robot) throws InvocationTargetException, IllegalAccessException, AWTException, IOException, InterruptedException {
        long now = System.currentTimeMillis();

        while(!containQueryBox(robot)) {
            if (System.currentTimeMillis() - now > 75L){
                ImageIO.write(new Robot().createScreenCapture(captureRect),"png",new File("test.png"));
                System.out.println("wait Box timeout");
                robot.keyRelease(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_ALT);
                if (timeoutCount.incrementAndGet()>50){
                    resetPoint(robot);
                }
                throw new RuntimeException();
            }
        }
        System.out.println("time"+(System.currentTimeMillis() - now));

        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_ALT);
        timeoutCount.set(0);
        while(containQueryBox(robot)) {
            if (System.currentTimeMillis() - now>5000){
                resetPoint(robot);
                throw new RuntimeException();
            }
        }
    }

    private static void resetPoint(RobotPeer robotPeer) throws InterruptedException {
        robotPeer.mouseMove((int) points[4].getX() -  captureRect.width*5/4, (int) points[4].getY());
        robotPeer.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1);
        robotPeer.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(100);
        robotPeer.keyRelease(KeyEvent.VK_ESCAPE);
        robotPeer.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(100);
        robotPeer.keyRelease(KeyEvent.VK_ESCAPE);



        robotPeer.mouseMove((int) points[4].getX() -  captureRect.width*5/4, (int) points[4].getY());
        robotPeer.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1);
        robotPeer.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robotPeer.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1);
        robotPeer.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }


    private static boolean waitUntilWindowAppear(RobotPeer robot) throws InterruptedException {
        long time = System.currentTimeMillis();
        Thread.sleep(1);
        do {
            if (containBuyingWindow(robot)) {
                return true;
            }
        } while(System.currentTimeMillis() - time <= 1000L);

        return false;
    }

    private static void mousePressAndRelease(Robot robot) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(1);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static boolean containBuyingWindow(RobotPeer robot) {

        int[] capturedPixels = robot.getRGBPixels(captureRect);
        int height = captureRect.height/4;
        int width = captureRect.width/3;
        for(int y1 = 0; y1 < height; ++y1) {
            if (height-y1<=expectedHeightW){
                break;
            }
            label42:
            for (int x1 = 0; x1 < width; ++x1) {
                if (width - x1 <= expectedWidthW) {
                    break;
                }
                int capturedPixel1 = capturedPixels[y1 * captureRect.width + x1];
                if (expectedPixelsW[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightW; ++y) {
                        for(int x = 0; x < expectedWidthW; ++x) {
                            int expectedPixel = expectedPixelsW[y * expectedWidthW + x];
                            int capturedPixel = capturedPixels[(y + y1) * captureRect.width + x + x1];
                            if (expectedPixel != capturedPixel) {
                                continue label42;
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private static void logOutAndLogin(Robot robot) throws InterruptedException {
        escape(robot);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        Thread.sleep(500);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
        Thread.sleep(1000);
        robot.mouseMove((int) points[2].getX() +captureRect.width/2 , (int) points[2].getY() + captureRect.height/2);

        mousePressAndRelease(robot);
        Thread.sleep(6000);
        robot.mouseMove(captureRect.width/5 , captureRect.height/6);
        mousePressAndRelease(robot);

        mousePressAndRelease(robot);
        Thread.sleep(5000);
        robot.mouseMove((int) points[1].getX()+(captureRect.width /2)*4/9, (int) points[1].getY());
        mousePressAndRelease(robot);
        Thread.sleep(20000);


        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(3000);
        robot.mouseMove((int) points[2].getX() - captureRect.width , (int) points[2].getY() + captureRect.height  / 3);
        mousePressAndRelease(robot);
        Thread.sleep(20000);


        escape(robot);
        robot.mouseMove((int) points[4].getX() -  captureRect.width*5/4, (int) points[4].getY());
        mousePressAndRelease(robot);
        Thread.sleep(100);

        openMarket(robot);
        Thread.sleep(1000);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_A);
        robot.mouseMove((int) captureRect.getX()+captureRect.width*4+captureRect.width/6, (int)captureRect.getY()+captureRect.height/2);
        mousePressAndRelease(robot);
        Thread.sleep(1000);
        robot.mouseMove((int) captureRect.getX()-2*captureRect.width, (int)captureRect.getY()+captureRect.height*2);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove((int) captureRect.getX()-2*captureRect.width, (int)captureRect.getY());
        Thread.sleep(1000);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove((int) captureRect.getX()-2*captureRect.width, (int)captureRect.getY()+captureRect.height/2);
        mousePressAndRelease(robot);
        Thread.sleep(1000);
        robot.mouseMove((int) captureRect.getX(),
                (int)captureRect.getY()+captureRect.height);
        mousePressAndRelease(robot);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(200);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

    }

    private static void escape(Robot robot) throws InterruptedException {
        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(20);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(20);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(20);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(20);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }

    private static boolean containQueryBox(RobotPeer robot) throws InvocationTargetException, IllegalAccessException {
        method.invoke(robot,captureRect.x,captureRect.y,captureRect.width,captureRect.height,captureRectArray);
        for(int y1 =  captureRect.height/4*3; y1 < captureRect.height; ++y1) {
            if ((captureRect.height - y1) <= expectedHeightQ){
                break ;
            }
            label42:
            for (int x1 = captureRect.width/2; x1 < captureRect.width; ++x1) {
                if ((captureRect.width - x1) <= expectedWidthQ){
                    break ;
                }
                int capturedPixel1 = captureRectArray[y1 * captureRect.width + x1];
                if (expectedPixelsQ[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeightQ; ++y) {
                        for (int x = 0; x < expectedWidthQ; ++x) {
                            if (expectedPixelsQ[y * expectedWidthQ + x] != captureRectArray[(y + y1) * captureRect.width + x + x1]) {
                                continue label42;
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }


    private static boolean match() {
        try {
            return matchA(captureRect.width, captureRect.height, captureRectArray) || matchB(captureRect.width, captureRect.height, captureRectArray)
                    /*|| matchC(captureRect.width, captureRect.height, captureRectArray)*/;
        } catch (Exception e) {


            System.out.println(e.getMessage());
            return false;
        }
    }

    private static boolean matchA(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = capturedHeight/10; y1 < capturedHeight; ++y1) {
            if ((capturedHeight - y1) <= expectedHeightA){
                break ;
            }
            label42:
            for (int x1 = capturedWidth/10; x1 < capturedWidth; ++x1) {
                if ((capturedWidth - x1) <= expectedWidthA){
                    break ;
                }
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsA[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightA; ++y) {
                        for (int x = 0; x < expectedWidthA; ++x) {
                            if (expectedPixelsA[y * expectedWidthA + x] != capturedPixels[(y + y1) * capturedWidth + x + x1]) {
                                continue label42;
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
        for(int y1 = capturedHeight/10; y1 < capturedHeight; ++y1) {
            if ((capturedHeight - y1) <= expectedHeightB){
                break ;
            }
            label42:
            for(int x1 = capturedWidth/10; x1 < capturedWidth; ++x1) {
                if ((capturedWidth - x1) <= expectedWidthB){
                    break ;
                }
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsB[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightB; ++y) {
                        for (int x = 0; x < expectedWidthB; ++x) {
                            if (expectedPixelsB[y * expectedWidthB + x] != capturedPixels[(y + y1) * capturedWidth + x + x1]) {
                                continue label42;
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private static boolean matchC(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = capturedHeight/10; y1 < capturedHeight; ++y1) {
            if ((capturedHeight - y1) <= expectedHeightC){
                break ;
            }
            label42:
            for (int x1 = capturedWidth/10; x1 < capturedWidth; ++x1) {
                if ((capturedWidth - x1) <= expectedWidthC){
                    break ;
                }
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsC[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightC; ++y) {
                        for (int x = 0; x < expectedWidthC; ++x) {
                            if (expectedPixelsC[y * expectedWidthC + x] != capturedPixels[(y + y1) * capturedWidth + x + x1]) {
                                continue label42;
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
        for(int y1 = capturedHeight/10; y1 < capturedHeight; ++y1) {
            if ((capturedHeight - y1) <= expectedHeightD){
                break ;
            }
            label42:
            for (int x1 = capturedWidth/10; x1 < capturedWidth; ++x1) {
                if ((capturedWidth - x1) <= expectedWidthD){
                    break ;
                }
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsD[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightD; ++y) {
                        for (int x = 0; x < expectedWidthD; ++x) {
                            int expectedPixel = expectedPixelsD[y * expectedWidthD + x];
                            int capturedPixel = capturedPixels[(y + y1) * capturedWidth + x + x1];
                            if (expectedPixel != capturedPixel) {
                                continue label42;
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
