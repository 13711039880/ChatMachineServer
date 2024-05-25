package org.chat.machine.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chat.machine.server.command.input;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);
    public static String[] parameter;
    public static ServerSocket sc;
    public static int port = Integer.parseInt(ConfigOperation.ReadConfig("port"));
    public static String key = ConfigOperation.ReadConfig("key");

    public static void main(String[] parameter) {
        Main.parameter = parameter;

        log.info("port = " + port);
        log.info("key = " + key);

        try {
            sc = new ServerSocket(port);
            SendMail.main();
            log.info("服务器已在 " + port + " 上启动");

            input input = new input();
            input.start();

            try (Stream<Path> walk = Files.walk(Path.of(".\\plugin"))) {
                walk.filter(Files::isRegularFile).forEach(new Consumer<Path>() {
                    @Override
                    public void accept(Path file) {
                        LoadPlugin.MethodAndClass mac = LoadPlugin.GetMAC(file.toString(), "onLoad");

                        try {
                            mac.getM().invoke(mac.getC().newInstance());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            while (true) {
                Socket socket = sc.accept();
                Thread thread = new Thread(new ClientHandler(socket, socket.getInetAddress().getHostAddress()));
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientHandler implements Runnable {
        protected static final Logger log = LogManager.getLogger(ClientHandler.class);
        public String ClientIP;
        private Socket socket;

        public ClientHandler(Socket socket, String ClientIP) {
            this.socket = socket;
            this.ClientIP = ClientIP;
        }

        private String ls(String text) {
            return "[" + ClientIP + "] " + text;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = reader.readLine();
                    log.info(ls("内容: " + message));

                    if (message != null) {
                        if (!message.split(":")[0].equals("cfile")) {
                            if (message.split(":")[0].equals("verify")) {
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                String ReturnString = ProcessInformation.main(message, socket, ClientIP);
                                log.info(ls("返回: " + ReturnString));
                                writer.write(ReturnString);
                                writer.newLine();

                                if (!ReturnString.equals("closedone")) {
                                    writer.flush();
                                } else {
                                    break;
                                }
                            } else {
                                if (message.split(":").length == 3) {
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    String ReturnString = ProcessInformation.main(message, socket, ClientIP);
                                    log.info(ls("返回: " + ReturnString));
                                    writer.write(ReturnString);
                                    writer.newLine();

                                    if (!ReturnString.equals("closedone")) {
                                        writer.flush();
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (message.split(":")[2].equals(key)) {
                                        if (!message.split(":")[0].equals("verify")) {
                                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                            String ReturnString = ProcessInformation.main(message, socket, ClientIP);
                                            log.info(ls("返回: " + ReturnString));
                                            writer.write(ReturnString);
                                            writer.newLine();

                                            if (!ReturnString.equals("closedone")) {
                                                writer.flush();
                                            } else {
                                                break;
                                            }
                                        } else {
                                            log.info(ls("key不正确"));
                                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                            log.info(ls("返回: " + null));
                                            writer.write((String) null);
                                            writer.newLine();
                                            writer.flush();
                                        }
                                    }
                                }
                            }
                        } else {
                            OutputStream os = socket.getOutputStream();
                            log.info(ls("补全文件: " + message.split(":")[1]));
                            File file = new File(".\\cfile\\" + message.split(":")[1]);
                            FileInputStream fis = new FileInputStream(file);
                            byte[] bytes = new byte[1024];
                            int length;

                            while ((length = fis.read(bytes)) != -1) {
                                os.write(bytes, 0, length);
                            }

                            os.close();
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
