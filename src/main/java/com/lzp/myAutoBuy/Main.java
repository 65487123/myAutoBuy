package com.lzp.myAutoBuy;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;








import javax.swing.*;
import java.awt.*;

import java.awt.event.InputEvent;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
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

    private static int expectedWidthE;
    private static int expectedHeightE;
    private static int[] expectedPixelsE;
    private static int expectedWidthF;
    private static int expectedHeightF;
    private static int[] expectedPixelsF;


    private static int expectedWidthH;
    private static int expectedHeightH;
    private static int[] expectedPixelsH;

    private static int expectedWidthI;
    private static int expectedHeightI;
    private static int[] expectedPixelsI;


    private static int expectedWidthW;
    private static int expectedHeightW;
    private static int[] expectedPixelsW;


    private static int expectedWidthGo;
    private static int expectedHeightGo;
    private static int[] expectedPixelsGo;

    private static int expectedWidthQ;
    private static int expectedHeightQ;
    private static int[] expectedPixelsQ;
    private static Rectangle captureRect;


    private static int[] captureRectArray;

    private static Method method;
    private static Point[] points = new Point[5];


    /**
     * 0 代表星石
     * 1 代表高级变身卡
     * 2 代表低级变身卡
     * 3 代表召唤灵积分券
     *
    * */
    private static int type = 0;
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

            BufferedImage expectedImageC = ImageIO.read(new File("./!b1.png"));
            expectedWidthC = expectedImageC.getWidth();
            expectedHeightC = expectedImageC.getHeight();
            expectedPixelsC = expectedImageC.getRGB(0, 0, expectedWidthC, expectedHeightC, null, 0, expectedWidthC);

            BufferedImage expectedImageD = ImageIO.read(new File("./!b2.png"));
            expectedWidthD = expectedImageD.getWidth();
            expectedHeightD = expectedImageD.getHeight();
            expectedPixelsD = expectedImageD.getRGB(0, 0, expectedWidthD, expectedHeightD, null, 0, expectedWidthD);

            BufferedImage expectedImageE = ImageIO.read(new File("./!b3.png"));
            expectedWidthE = expectedImageE.getWidth();
            expectedHeightE = expectedImageE.getHeight();
            expectedPixelsE = expectedImageE.getRGB(0, 0, expectedWidthE, expectedHeightE, null, 0, expectedWidthE);

            BufferedImage expectedImageF = ImageIO.read(new File("./!b4.png"));
            expectedWidthF = expectedImageF.getWidth();
            expectedHeightF = expectedImageF.getHeight();
            expectedPixelsF = expectedImageF.getRGB(0, 0, expectedWidthF, expectedHeightF, null, 0, expectedWidthF);

            BufferedImage expectedImageH = ImageIO.read(new File("./!b5.png"));
            expectedWidthH = expectedImageH.getWidth();
            expectedHeightH = expectedImageH.getHeight();
            expectedPixelsH = expectedImageH.getRGB(0, 0, expectedWidthH, expectedHeightH, null, 0, expectedWidthH);


            BufferedImage expectedImageI = ImageIO.read(new File("./!b6.png"));
            expectedWidthI = expectedImageI.getWidth();
            expectedHeightI = expectedImageI.getHeight();
            expectedPixelsI = expectedImageI.getRGB(0, 0, expectedWidthI, expectedHeightI, null, 0, expectedWidthI);


            BufferedImage expectedImageGo = ImageIO.read(new File("./gold.png"));
            expectedWidthGo = expectedImageGo.getWidth();
            expectedHeightGo = expectedImageGo.getHeight();
            expectedPixelsGo = expectedImageGo.getRGB(0, 0, expectedWidthGo, expectedHeightGo, null, 0, expectedWidthGo);

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
            for (int l = 0; l < 15000; l++) {
                long now = System.currentTimeMillis();
                try {
                    openMarket(robot);
                    boolean proper = true;

                    waitUntilQueryBoxComeAndGone(robotPeer);
                    //ImageIO.write(robot.createScreenCapture(captureRect),"png",new File("D:\\project\\picture\\"+l+".png"));
                    for (int i = 0; i < points.length - 1; i++) {
                        robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                        mousePressAndRelease(robot);
                        if (i == 1) {
                            //放到这里判断是为了性能
                            if (!match()) {

                                proper = false;
                                break;
                            }
                        }
                    }
                    Thread.sleep(19);
                    if (proper) {


                        robot.mouseMove((int) points[4].getX(), (int) points[4].getY());
                        mousePressAndRelease(robot);
                        System.out.println("all" + (System.currentTimeMillis() - now));
                        Thread.sleep(4000);
                    }
                } catch (Exception ignored) {
                    System.out.println(ignored);
                }
                mousePressAndRelease2(robot);

                robot.mouseMove((int) points[4].getX(), (int) points[4].getY());
                mousePressAndRelease(robot);
                System.out.println(System.currentTimeMillis() - now);
            }
            Thread.sleep(1000);
            logOutAndLogin(robot);
        }
    }

    private static void openMarket(Robot robot) {
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
        Thread.sleep(45);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_ALT);
        method.invoke(robot,captureRect.x,captureRect.y,captureRect.width,captureRect.height,captureRectArray);
        long now = System.currentTimeMillis();
        while (!marketOpened(captureRect.width, captureRect.height)) {
            method.invoke(robot,captureRect.x,captureRect.y,captureRect.width,captureRect.height,captureRectArray);
            if (System.currentTimeMillis() - now > 200) {
                System.out.println("open market timeout");
                if (timeoutCount.incrementAndGet() > 15) {
                    resetPoint(robot);
                    timeoutCount.set(0);
                }
                throw new RuntimeException();
            }
        }
        timeoutCount.set(0);
        now = System.currentTimeMillis();
        while (containQueryBox()) {
            method.invoke(robot, captureRect.x, captureRect.y, captureRect.width, captureRect.height, captureRectArray);
            if (System.currentTimeMillis() - now > 5000) {
                System.out.println("wait Box Gone timeout");
                throw new RuntimeException();
            }
        }
    }



    private static void resetPoint(RobotPeer robotPeer
    ) throws InterruptedException {
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




    private static void mousePressAndRelease(Robot robot) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(1);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    private static void mousePressAndRelease2(Robot robot) {
        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
        robot.delay(1);
        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
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
        Thread.sleep(5000);
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
        openSalesDetails(robot);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        Thread.sleep(200);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

    }

    private static void openSalesDetails(Robot robot) {
        if (type == 0) {
            robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY() + captureRect.height * 2);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY());
            robot.delay(1000);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY() + captureRect.height / 2);
            mousePressAndRelease(robot);
            robot.delay(1000);
            robot.mouseMove((int) captureRect.getX(),
                    (int) captureRect.getY() + captureRect.height);
            mousePressAndRelease(robot);
        } else if (type == 3) {
            robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY() + captureRect.height * 2 + captureRect.height / 10);             mousePressAndRelease(robot);             robot.delay(1000);             mousePressAndRelease(robot);             robot.delay(1000);             robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY() + captureRect.height * 2 - captureRect.height / 10);             mousePressAndRelease(robot);             robot.delay(1000);
            robot.mouseMove((int) captureRect.getX(),
                    (int) captureRect.getY() + captureRect.height);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            robot.mouseMove((int) captureRect.getX(),
                    (int) captureRect.getY());
            robot.delay(1000);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove((int) captureRect.getX()+ captureRect.width*2,
                    (int) captureRect.getY() + captureRect.height+captureRect.height/2);
            robot.delay(200);
            mousePressAndRelease(robot);
        } else {
            robot.mouseMove((int) captureRect.getX() - 2 * captureRect.width, (int) captureRect.getY() + captureRect.height * 2 + captureRect.height / 10);
            mousePressAndRelease(robot);
            robot.delay(1000);
            mousePressAndRelease(robot);
            robot.delay(1000);
            mousePressAndRelease(robot);
            robot.delay(1000);
            if (type == 1) {
                robot.mouseMove((int) captureRect.getX() + captureRect.width * 2,
                        (int) captureRect.getY() + captureRect.height / 2);
            } else {
                robot.mouseMove((int) captureRect.getX(),
                        (int) captureRect.getY() + captureRect.height);
            }
            mousePressAndRelease(robot);
        }
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

    private static boolean containQueryBox() throws InvocationTargetException, IllegalAccessException {
        int zy = captureRect.height - expectedHeightQ;
        int zx = captureRect.width - expectedWidthQ;
        for (int y1 = captureRect.height / 4 * 3; y1 < zy; ++y1) {
            label42:
            for (int x1 = captureRect.width / 2; x1 < zx; ++x1) {
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
            return matchA(captureRect.width, captureRect.height) ||(matchB(captureRect.width, captureRect.height)
                    &&!matchC(captureRect.width, captureRect.height)&&!matchD(captureRect.width, captureRect.height)
                    &&!matchE(captureRect.width, captureRect.height)&&!matchF(captureRect.width, captureRect.height)
                    &&!matchH(captureRect.width, captureRect.height)&&!matchI(captureRect.width, captureRect.height));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;

        }
    }

    private static boolean marketOpened(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height - expectedHeightGo;
        int zx = captureRect.width - expectedWidthGo;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for (int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsGo[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightGo; ++y) {
                        for (int x = 0; x < expectedWidthGo; ++x) {
                            if (expectedPixelsGo[y * expectedWidthGo + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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


    private static boolean matchA(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthA;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for (int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsA[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightA; ++y) {
                        for (int x = 0; x < expectedWidthA; ++x) {
                            if (expectedPixelsA[y * expectedWidthA + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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

    private static boolean matchC(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthC;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsC[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightC; ++y) {
                        for (int x = 0; x < expectedWidthC; ++x) {
                            if (expectedPixelsC[y * expectedWidthC + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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

    private static boolean matchD(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthD;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsD[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightD; ++y) {
                        for (int x = 0; x < expectedWidthD; ++x) {
                            if (expectedPixelsD[y * expectedWidthD + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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

    private static boolean matchE(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthE;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsE[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightE; ++y) {
                        for (int x = 0; x < expectedWidthE; ++x) {
                            if (expectedPixelsE[y * expectedWidthE + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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


    private static boolean matchF(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthF;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsF[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightF; ++y) {
                        for (int x = 0; x < expectedWidthF; ++x) {
                            if (expectedPixelsF[y * expectedWidthF + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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

    private static boolean matchH(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthH;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsH[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightH; ++y) {
                        for (int x = 0; x < expectedWidthH; ++x) {
                            if (expectedPixelsH[y * expectedWidthH + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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


    private static boolean matchI(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthI;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsI[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightI; ++y) {
                        for (int x = 0; x < expectedWidthI; ++x) {
                            if (expectedPixelsI[y * expectedWidthI + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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
    private static boolean matchB(int capturedWidth, int capturedHeight) {
        int zy = captureRect.height/2;
        int zx = captureRect.width - expectedWidthB;
        for(int y1 = capturedHeight/10; y1 < zy; ++y1) {
            label42:
            for(int x1 = capturedWidth/10; x1 < zx; ++x1) {
                int capturedPixel1 = captureRectArray[y1 * capturedWidth + x1];
                if (expectedPixelsB[0] == capturedPixel1 ) {
                    for (int y = 0; y < expectedHeightB; ++y) {
                        for (int x = 0; x < expectedWidthB; ++x) {
                            if (expectedPixelsB[y * expectedWidthB + x] != captureRectArray[(y + y1) * capturedWidth + x + x1]) {
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
