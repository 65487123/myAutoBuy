package com.lzp.myAutoBuy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.CountDownLatch;


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

    private static int expectedWidthG1;
    private static int expectedHeightG1;
    private static int[] expectedPixelsG1;

    private static int expectedWidthQ;
    private static int expectedHeightQ;
    private static int[] expectedPixelsQ;
    private static Rectangle captureRect;
    private static Point[] points = new Point[6];

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

            BufferedImage expectedImageG1 = ImageIO.read(new File("./gold1.png"));
            expectedWidthG1 = expectedImageG1.getWidth();
            expectedHeightG1 = expectedImageG1.getHeight();
            expectedPixelsG1 = expectedImageG1.getRGB(0, 0, expectedWidthG1, expectedHeightG1, null, 0, expectedWidthG1);

            BufferedImage expectedImageQ = ImageIO.read(new File("./querybox.png"));
            expectedWidthQ = expectedImageQ.getWidth();
            expectedHeightQ = expectedImageQ.getHeight();
            expectedPixelsQ = expectedImageQ.getRGB(0, 0, expectedWidthQ, expectedHeightQ, null, 0, expectedWidthQ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        captureRect = generCaptureRect(Toolkit.getDefaultToolkit().getScreenSize());
        optionPane = new JOptionPane("现在可以把购买窗口关闭了,接下来要定位六个所需坐标", JOptionPane.INFORMATION_MESSAGE);
        dialog = optionPane.createDialog("提示");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }


    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            for(int i = 0; i < 6; ++i) {
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
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();

        //for (int l=0; l<5000;l++){
        BufferedImage screenCapture;


        while (true) {
                for (int l = 0; l <12000 ; l++) {
                    try {
                        long now = System.currentTimeMillis();
                        robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
                        mousePressAndRelease(robot);
                        //waitUntilBoxAppear(robot);
                        waitUntilQueryBoxComeAndGone(robot);
                        robot.mouseMove((int) points[1].getX(), (int) points[1].getY());
                        mousePressAndRelease(robot);
                        screenCapture = waitUntilGlod1Appear(robot);
                        robot.mouseMove((int) points[2].getX(), (int) points[2].getY());
                        if (match(screenCapture)) {
                            for (int i = 2; i < points.length - 1; i++) {
                                robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                                mousePressAndRelease(robot);
                                if (i == 2 && !waitUntilWindowAppear(robot)) {
                                    break;
                                }
                            }
                        }
                        robot.mouseMove((int) points[5].getX(), (int) points[5].getY());
                        mousePressAndRelease(robot);
                        System.out.println(System.currentTimeMillis() - now);
                    } catch (Exception ignored) {
                    }
                }
                Thread.sleep(1000);
                logOutAndLogin(robot);
            }
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

                        return new Rectangle(x1, y1, screenSize.width / 12, screenSize.height / 12);
                    }
                }
            }
        } catch (Exception var12) {
        }

        return null;
    }

    private static boolean waitUntilQueryBoxComeAndGone(Robot robot) throws InterruptedException {
        long now = System.currentTimeMillis();

        while(!containQueryBox(robot) && System.currentTimeMillis() - now <= 200L) {
        }

        while(containQueryBox(robot)) {
        }
        return true;
    }

    private static BufferedImage waitUntilGlod1Appear(Robot robot) throws InterruptedException {
        BufferedImage bufferedImage;
        long time = System.currentTimeMillis();
        Thread.sleep(10L);

        do {
            if ((bufferedImage = containGold1AndGetImg(robot)) != null) {
                return bufferedImage;
            }
        } while (System.currentTimeMillis() - time <= 300L);
        return null;
    }


    private static boolean waitUntilWindowAppear(Robot robot) {
        long time = System.currentTimeMillis();

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

    private static boolean containBuyingWindow(Robot robot) {
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);

        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsW[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightW; ++y) {
                        for(int x = 0; x < expectedWidthW; ++x) {
                            int expectedPixel = expectedPixelsW[y * expectedWidthW + x];
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

    private static void logOutAndLogin(Robot robot) throws InterruptedException {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        Thread.sleep(500);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
        Thread.sleep(1000);
        robot.mouseMove((int) points[3].getX() + captureRect.width /8, (int) points[3].getY() + captureRect.height);
        mousePressAndRelease(robot);
        Thread.sleep(6000);
        robot.mouseMove(captureRect.width /8, captureRect.height/2);
        mousePressAndRelease(robot);
        mousePressAndRelease(robot);
        Thread.sleep(5000);
        robot.mouseMove((int) points[2].getX()+captureRect.width /2, (int) points[2].getY());
        mousePressAndRelease(robot);
        Thread.sleep(20000);


        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(3000);
        robot.mouseMove((int) points[3].getX() - captureRect.width / 4, (int) points[3].getY() + captureRect.height * 3 / 4);
        mousePressAndRelease(robot);
        Thread.sleep(20000);


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
        robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
        mousePressAndRelease(robot);
        Thread.sleep(1000);
        robot.mouseMove((int) captureRect.getX()+captureRect.width*7/2, (int)captureRect.getY()+captureRect.height);
        mousePressAndRelease(robot);
        Thread.sleep(1000);
        robot.mouseMove((int) captureRect.getX()-captureRect.width, (int)captureRect.getY()+captureRect.height*4);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove((int) captureRect.getX()-captureRect.width, (int)captureRect.getY());
        Thread.sleep(1000);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove((int) captureRect.getX()-captureRect.width, (int)captureRect.getY()+captureRect.height);
        mousePressAndRelease(robot);
        Thread.sleep(1000);
        robot.mouseMove((int) captureRect.getX(), (int)captureRect.getY()+captureRect.height*2);
        mousePressAndRelease(robot);
        robot.mouseMove((int) points[5].getX(), (int) points[5].getY());
        mousePressAndRelease(robot);


    }

    private static boolean containQueryBox(Robot robot) {
        BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(captureRect.x + captureRect.width / 2, captureRect.y + captureRect.height * 3 / 2, captureRect.width, captureRect.height));
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);

        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsQ[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightQ; ++y) {
                        for(int x = 0; x < expectedWidthQ; ++x) {
                            int expectedPixel = expectedPixelsQ[y * expectedWidthQ + x];
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


    private static BufferedImage containGold1AndGetImg(Robot robot) {
        robot.mouseMove((int) points[2].getX(), (int) points[2].getY());
        BufferedImage screenCapture = robot.createScreenCapture(captureRect);
        int capturedWidth = screenCapture.getWidth();
        int capturedHeight = screenCapture.getHeight();
        int[] capturedPixels = screenCapture.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        try {
            for (int y1 = 0; y1 < capturedHeight; ++y1) {
                label42:
                for (int x1 = 0; x1 < capturedWidth; ++x1) {
                    int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                    if (expectedPixelsG1[0] == capturedPixel1) {
                        for (int y = 0; y < expectedHeightG1; ++y) {
                            for (int x = 0; x < expectedWidthG1; ++x) {
                                int expectedPixel = expectedPixelsG1[y * expectedWidthG1 + x];
                                int capturedPixel = capturedPixels[(y + y1) * capturedWidth + x + x1];
                                if (expectedPixel != capturedPixel) {
                                    continue label42;
                                }
                            }
                        }

                        return screenCapture;
                    }
                }
            }
        }catch (Exception e){
            try {
                System.out.println(e.getMessage());
                ImageIO.write(screenCapture,"png",new File("test.png"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

    private static boolean match(BufferedImage capturedImage) {
        try {
            int capturedWidth = capturedImage.getWidth();
            int capturedHeight = capturedImage.getHeight();
            int[] capturedPixels = capturedImage.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
            return matchA(capturedWidth, capturedHeight, capturedPixels) || matchB(capturedWidth, capturedHeight, capturedPixels)
                    || matchC(capturedWidth, capturedHeight, capturedPixels);
        } catch (Exception var4) {

            return false;
        }
    }

    private static boolean matchA(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsA[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightA; ++y) {
                        for(int x = 0; x < expectedWidthA; ++x) {
                            int expectedPixel = expectedPixelsA[y * expectedWidthA + x];
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

    private static boolean matchB(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsB[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightB; ++y) {
                        for(int x = 0; x < expectedWidthB; ++x) {
                            int expectedPixel = expectedPixelsB[y * expectedWidthB + x];
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

    private static boolean matchC(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsC[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightC; ++y) {
                        for(int x = 0; x < expectedWidthC; ++x) {
                            int expectedPixel = expectedPixelsC[y * expectedWidthC + x];
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

    private static boolean matchD(int capturedWidth, int capturedHeight, int[] capturedPixels) {
        for(int y1 = 0; y1 < capturedHeight; ++y1) {
            label42:
            for(int x1 = 0; x1 < capturedWidth; ++x1) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixelsD[0] == capturedPixel1) {
                    for(int y = 0; y < expectedHeightD; ++y) {
                        for(int x = 0; x < expectedWidthD; ++x) {
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
