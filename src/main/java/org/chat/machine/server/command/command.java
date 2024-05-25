package org.chat.machine.server.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chat.machine.server.ConfigOperation;
import org.chat.machine.server.Main;

import java.io.IOException;

import static org.chat.machine.server.Main.*;

public class command {
    protected static final Logger log = LogManager.getLogger(command.class);

    public static void main(String command) {
        switch (command.split(" ")[0]) {
            default -> log.info("未知命令, 用'help'查看帮助");
            case "" -> {
            }

            case "stop" -> {
                log.info("关闭服务器...");

                try {
                    sc.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.exit(0);
            }

            case "restart" -> {
                log.info("重新启动服务器...");

                try {
                    sc.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Main.main(parameter);
            }

            case "regetset" -> {
                port = Integer.parseInt(ConfigOperation.ReadConfig("port"));
                key = ConfigOperation.ReadConfig("key");
                log.info("重新获取设置完成");
            }

            case "set" -> {
                ConfigOperation.SetConfig(
                        command.split(" ")[1].split("=")[0],
                        command.split(" ")[1].split("=")[1]
                );

                log.info("更改成功");
            }

            case "help" -> {
                log.info("==========help==========");
                log.info("施工中...");
                log.info("========================");
            }
        }
    }
}
