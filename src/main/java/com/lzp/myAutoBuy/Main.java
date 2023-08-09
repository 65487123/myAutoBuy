package com.lzp.myAutoBuy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class Main {

    private static int expectedWidth ;
    private static int expectedHeight ;
    private static int[] expectedPixels;

    private static Point[] points= new Point[6];

    private static volatile boolean shutdown;

    static {
        try {
            BufferedImage expectedImage = ImageIO.read(new File("./a.png"));
            expectedWidth = expectedImage.getWidth();
            expectedHeight = expectedImage.getHeight();
            expectedPixels = expectedImage.getRGB(0, 0, expectedWidth, expectedHeight, null, 0, expectedWidth);
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
            new Thread(KeyListenerImpl::new).start();
        }).start();

        synchronized (Main.class) {
            Main.class.wait();
            long deadline = System.currentTimeMillis()+3600000;
            while (System.currentTimeMillis() < deadline) {

                robot.mouseMove((int) points[0].getX(), (int) points[0].getY());
                mousePressAndRelease(robot);

                robot.mouseMove((int) points[1].getX(), (int) points[1].getY());
                mousePressAndRelease(robot);
                Thread.sleep(100);
                BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                BufferedImage windowImage = screenShot.getSubimage(0, 0, screenShot.getWidth() / 3, screenShot.getHeight() / 3);
                if (consist(screenShot)) {
                    for (int i = 2; i < points.length; i++) {
                        Thread.sleep(50);
                        robot.mouseMove((int) points[i].getX(), (int) points[i].getY());
                        mousePressAndRelease(robot);
                    }
                }
            }
        }
    }

    private static  void mousePressAndRelease(Robot robot){
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    private static boolean consist(BufferedImage capturedImage) {

        int capturedWidth = capturedImage.getWidth();
        int capturedHeight = capturedImage.getHeight();
        int[] capturedPixels = capturedImage.getRGB(0, 0, capturedWidth, capturedHeight, null, 0, capturedWidth);
        for (int y1 = 0; y1 < capturedHeight; y1++) {
            a:
            for (int x1 = 0; x1 < capturedWidth; x1++) {
                int capturedPixel1 = capturedPixels[y1 * capturedWidth + x1];
                if (expectedPixels[0] == capturedPixel1) {
                    for (int y = 0; y < expectedHeight; y++) {
                        for (int x = 0; x < expectedWidth; x++) {
                            int expectedPixel = expectedPixels[y * expectedWidth + x];
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

    static class KeyListenerImpl extends JFrame implements KeyListener {

        public KeyListenerImpl() {
            addKeyListener(this); // 注册键盘监听器
            setFocusable(true); // 确保窗口获取焦点
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(0, 0);
            setVisible(true);
            // 获取屏幕的大小
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            Dimension screenSize = gd.getDefaultConfiguration().getBounds().getSize();
            // 设置窗口位置为右下角
            int x = screenSize.width - getWidth();
            int y = screenSize.height - getHeight();
            setLocation(x, y);
            this.setAlwaysOnTop(true);
        }


        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                Main.shutdown = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println("Key Released: " + e.getKeyChar());
        }

        public static void main(String[] args) {
            new KeyListenerImpl();
        }
    }




}