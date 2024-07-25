package com.metrolink.ami_api.config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogManager;

public class LoggerConfigProcesos {

    private static final Logger logger = Logger.getLogger("com.metrolink.ami_api.controllers.procesos");

    static {
        try {
            LogManager.getLogManager().reset();
            // Specify the file path for the log file
            String logFilePath = "C:\\Users\\Personal\\Documents\\AMI\\AMI_API\\logs\\procesos\\procesos.log";
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            SimpleFormatter formatter = new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$s] %3$s %n";

                @Override
                public synchronized String format(java.util.logging.LogRecord lr) {
                    return String.format(format,
                            new java.util.Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            };
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);

            // Optionally, also log to console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.ALL);
            for (Handler h : logger.getHandlers()) {
                h.setLevel(Level.ALL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
