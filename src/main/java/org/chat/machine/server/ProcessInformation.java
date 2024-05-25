package org.chat.machine.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.util.file.DeleteFile;
import org.util.file.NewFile;
import org.util.properties.ReadPro;
import org.util.properties.SetPro;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.chat.machine.server.Main.key;

public class ProcessInformation {
    private static final Logger log = LogManager.getLogger(ProcessInformation.class);
    private static String ClientIP;
    
    public static String main(String message, Socket socket, String ClientIP) {
        ProcessInformation.ClientIP = ClientIP;

        switch (message.split(":")[0]) {
            default -> {
                log.warn(ls("无效内容"));
                return("ic");
            }

            case "verify" -> {
                if (message.split(":")[1].equals(key)) {
                    log.info(ls("验证通过"));
                    return("true");
                } else {
                    log.info(ls("验证不通过"));
                    return("false");
                }
            }

            case "login" -> {
                File file = new File(".\\user");
                File[] fs = file.listFiles();
                boolean correct = false;

                for (File f : fs) {
                    if (!f.isDirectory()) {
                        if (message.split(":")[1].split(",")[0].equals(ReadPro.ReadPro(".\\user\\" + f.getName(), "name"))) {
                            if (message.split(":")[1].split(",")[1].equals(ReadPro.ReadPro(".\\user\\" + f.getName(), "password"))) {
                                correct = true;
                            }
                        }
                    }
                }

                if (correct) {
                    log.info(ls("登录成功"));
                    return("true");
                } else {
                    log.info(ls("登录失败"));
                    return("false");
                }
            }

            case "register" -> {
                String ID = String.valueOf(UUID.randomUUID());
                String file = ".\\user\\" + ID + ".user";
                NewFile.NewFile(file);
                SetPro.SetPro(file, "name", message.split(":")[1].split(",")[0], "CMS");
                SetPro.SetPro(file, "password", message.split(":")[1].split(",")[1], "CMS");
                log.info(ls("注册成功(UUID:" + ID + ")"));
                return("true");
            }

            case "logoutuser" -> {
                File file = new File(".\\user");
                File[] fs = file.listFiles();

                for (File f : fs) {
                    if (!f.isDirectory()) {
                        if (message.split(":")[1].split(",")[0].equals(ReadPro.ReadPro(".\\user\\" + f.getName(), "name"))) {
                            DeleteFile.DeleteFile(".\\user\\" + f.getName());
                            log.info(ls("注销成功"));
                            return("true");
                        }
                    }
                }
            }

            case "getuuid" -> {
                File file = new File(".\\user");
                File[] fs = file.listFiles();

                for (File f : fs) {
                    if (!f.isDirectory()) {
                        if (message.split(":")[1].equals(ReadPro.ReadPro(".\\user\\" + f.getName(), "name"))) {
                            String uuid = f.getName().split("\\.")[0];
                            return(uuid);
                        }
                    }
                }
            }

            case "getroom" -> {
                File file = new File(".\\room");
                File[] fs = file.listFiles();
                StringBuilder room = new StringBuilder();

                for (File f : fs) {
                    room.append(ReadPro.ReadPro(".\\room\\" + f.getName(), "name")).append(",");
                }

                return room.substring(0, room.length() - 1);
            }

            case "addroom" -> {
                String file;
                String name = message.split(":")[1];

                while (true) {
                    file = ".\\room\\" + UUID.randomUUID() + ".room";

                    if (!Files.exists(Path.of(file))) {
                        NewFile.NewFile(file);
                        SetPro.SetPro(file, "name", name, "CMS");
                        SetPro.SetPro(file, "data", "", "CMS");
                        break;
                    }
                }

                log.info("创建成功");
                return "true";
            }

            case "getroomdata" -> {
                File file = new File(".\\room");
                File[] fs = file.listFiles();

                for (File f : fs) {
                    if (!f.isDirectory()) {
                        if (message.split(":")[1].split(",")[0].equals(ReadPro.ReadPro(".\\room\\" + f.getName(), "name"))) {
                            return(ReadPro.ReadPro(".\\room\\" + f.getName(), "data"));
                        }
                    }
                }
            }

            case "addroomdata" -> {
                File file = new File(".\\room");
                File[] fs = file.listFiles();

                for (File f : fs) {
                    if (!f.isDirectory()) {
                        if (message.split(":")[1].split(",")[0].equals(ReadPro.ReadPro(".\\room\\" + f.getName(), "name"))) {
                            SetPro.SetPro(
                                    ".\\room\\" + f.getName(),
                                    "data",
                                    ReadPro.ReadPro(".\\room\\" + f.getName(), "data") + message.split(":")[1].split(",")[1] + ",",
                                    "CMS"
                            );

                            log.info(ls("添加成功"));
                            return("true");
                        }
                    }
                }
            }

            case "sendmail" -> {
                log.info(ls("[发送邮件] 向 {} 发送邮件...", message.split(":")[1].split(",")[0]));

                try {
                    SendMail.send(
                            message.split(":")[1].split(",")[1],
                            message.split(":")[1].split(",")[2],
                            message.split(":")[1].split(",")[0]
                    );
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

                log.info(ls("[发送邮件] 完成"));
                return "true";
            }

            case "close" -> {
                log.info(ls("关闭连接"));

                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return "closedone";
            }
        }

        return "ic";
    }

    private static String ls(String text) {
        return "[" + ClientIP + "] " + text;
    }

    private static String ls(String text, String replace) {
        return "[" + ClientIP + "] " + text.replace("{}", replace);
    }
}
