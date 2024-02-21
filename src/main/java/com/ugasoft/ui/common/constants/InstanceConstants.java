package com.ugasoft.ui.common.constants;

import com.ugasoft.ui.common.core.Log;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class InstanceConstants {

    public static final String PATH = getPath();
    public static final String ENV = getEnvironment();
    public static final Properties properties;
    public static final String BASIC_USER_LOGIN;
    public static final String BASIC_USER_PASSWORD;
    public static final String DB_MYSQL_HOST;
    public static final String DB_MYSQL_CONN;
    public static final String DB_ORACLE_HOST;
    public static final String DB_ORACLE_CONN;
    public static final String DB_CASSANDRA_HOST;
    public static final String DB_CASSANDRA_PORT;
    public static final String VERSION_URL;

    public InstanceConstants() {
    }

    protected static String getEnvironment() {
        String env = "prod";
        String mavenEnv = System.getProperty("menv");
        if (!StringUtil.isNullOrEmpty("staging".trim())) {
            env = mavenEnv;
        }

        Log.info("Running test in environment: " + env + "\n");
        return env;
    }

    protected static String getPath() {
        String mpath = System.getProperty("mpath");
        return StringUtil.isNullOrEmpty(mpath) ? "/src/main/resources/PropertyFiles/" : mpath;
    }

    protected static String getProperty(String key) {
        return properties.getProperty(key);
    }

    protected static Properties getPropertiesForEnv() {
        Properties properties = null;

        try {
            properties = new Properties();
            FileReader reader = new FileReader(System.getProperty("user.dir") + String.format(PATH + "%s.properties", InstanceConstants.ENV));
            properties.load(reader);
            File custom = new File(System.getProperty("user.dir") + PATH + "custom.properties");
            String val;
            if (custom.exists()) {
                Properties customPros = new Properties();
                reader = new FileReader(System.getProperty("user.dir") + PATH + "custom.properties");
                customPros.load(reader);
                Enumeration<?> enums = customPros.propertyNames();

                while (enums.hasMoreElements()) {
                    val = (String) enums.nextElement();
                    String value = customPros.getProperty(val);
                    properties.setProperty(val, value);
                }
            }
            Enumeration<?> enums = properties.propertyNames();

            while (enums.hasMoreElements()) {
                String key = (String) enums.nextElement();
                val = System.getProperty("P_" + key, "_UNDEF_");
                if (!val.equals("_UNDEF_")) {
                    properties.setProperty(key, val);
                }
            }
        } catch (IOException ignored) {
        }

        return properties;
    }

    static {
        properties = getPropertiesForEnv();
        BASIC_USER_LOGIN = getProperty("basic.user.login");
        BASIC_USER_PASSWORD = getProperty("basic.user.password");
        DB_MYSQL_HOST = getProperty("db.mysql.host");
        DB_MYSQL_CONN = getProperty("db.mysql.connection");
        DB_ORACLE_HOST = getProperty("db.oracle.host");
        DB_ORACLE_CONN = getProperty("db.oracle.connection");
        DB_CASSANDRA_HOST = getProperty("db.cassandra.host");
        DB_CASSANDRA_PORT = getProperty("db.cassandra.port");
        VERSION_URL = getProperty("version.url");
    }

    protected static String getPropertyFromRoot(String fileName, String key) {
        Properties properties = new Properties();
        String value = null;
        try {
            FileReader reader = new FileReader(new File(System.getProperty("user.dir"), fileName));
            properties.load(reader);
            value = properties.getProperty(key);
        } catch (IOException e) {
            Log.error("Unable to load properties file from project root");
        }
        return value;
    }
}
