package org.chat.machine.server.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class input extends Thread {
    protected static final Logger log = LogManager.getLogger(input.class);

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        log.info("输入启用");

        while (true) {
            command.main(scanner.nextLine());
        }
    }
}
