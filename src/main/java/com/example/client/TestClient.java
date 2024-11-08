package com.example.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestClient {

    public static void main(String[] args) throws InterruptedException {
        String serverAddress = "127.0.0.1";
        int serverport = 10035;
        int threadCount = 2;
        int telegramSendCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        IntStream.range(0, threadCount).forEach(taskId -> {
            executorService.execute(() -> {
                try {
                    SocketChannel socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(serverAddress, serverport));

                    for (int i = 0; i < telegramSendCount; i++) {
                        while (!socketChannel.finishConnect()) {
                            Thread.sleep(100);
                        }

                        Thread.sleep(500);
                        String telegram = i % 2 == 0
                                ? "Telegram" +String.format("%010d%010d", taskId, i) + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        End"
                                : "Telegram" +String.format("%010d%010d", taskId, i) + "                                                                                                                                                                                                                                                                                                                                                                                     End";
//                        String telegram = "Telegram" +String.format("%010d%010d", taskId, i) + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                End";

//                        String telegram = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
//                        String telegram = taskId % 2 == 0
//                                ? "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
//                                : "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
                        ByteBuffer writeBuffer = ByteBuffer.wrap(telegram.getBytes());
                        System.out.println(Thread.currentThread().getName() + " : Request(" + telegram.length() + ") :" + telegram);
                        socketChannel.write(writeBuffer);

                        ByteBuffer readBuffer = ByteBuffer.allocate(512);
                        int bytesRead = socketChannel.read(readBuffer);
                        if (bytesRead > 0) {
                            readBuffer.flip();
                            byte[] responseData = new byte[bytesRead];
                            readBuffer.get(responseData);
                            String response = new String(responseData);
                            System.out.println(Thread.currentThread().getName() + " : Response : " + response);
                        } else {
                            System.out.println(Thread.currentThread().getName() + " : No Response Received");
                        }
                    }

                    socketChannel.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }

            });
        });

        latch.await();
        executorService.shutdown();
    }
}
