package org.chat.machine.server;

import org.util.properties.ReadPro;
import org.util.properties.SetPro;

public class ConfigOperation {
    public static String ReadConfig(String item) {
        return ReadPro.ReadPro(".\\config.properties", item);
    }

    public static void SetConfig(String item, String text) {
        SetPro.SetPro(".\\config.properties", item, text,  "CMS");
    }
}
