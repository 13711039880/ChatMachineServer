package org.chat.machine.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class LoadPlugin {
    public static MethodAndClass GetMAC(String plugin, String method) {
        try {
            Class c = LoadJar(".\\plugin\\" + plugin + ".jar");
            Method m = c.getMethod(method);
            return new MethodAndClass(m, c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class LoadJar(String file) throws IOException, ClassNotFoundException {
        ClassLoader cl = GetClassLoader(file);
        Properties properties = GetProperties(cl, "plugin.properties");
        String mainClass = properties.getProperty("mainclass");
        return LoadClass(cl, mainClass);
    }

    private static ClassLoader GetClassLoader(String plugin) throws MalformedURLException {
        return new URLClassLoader(new URL[]{new File(plugin).toURI().toURL()}, null);
    }

    private static Properties GetProperties(ClassLoader classLoader, String propertiesName) throws IOException {
        InputStream propertiesStream = classLoader.getResourceAsStream(propertiesName);
        Properties properties = new Properties();
        properties.load(propertiesStream);
        return properties;
    }

    private static Class LoadClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass(className);
        return clazz;
    }

    public static class MethodAndClass {
        private Method m;
        private Class c;

        private MethodAndClass(Method m, Class c) {
            this.m = m;
            this.c = c;
        }

        public Method getM() {
            return m;
        }

        public Class getC() {
            return c;
        }
    }
}
