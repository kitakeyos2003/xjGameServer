// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.text.DateFormat;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import java.io.ObjectInputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.io.File;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.FileAppender;
import java.util.Timer;
import org.apache.log4j.Logger;
import java.util.TimerTask;

public class LogWriter extends TimerTask {

    private static Logger log;
    private static LogWriter instance;
    private static Timer timer;
    private static Logger logger;
    private static FileAppender appender;
    private static PatternLayout layout;
    private static String logPattern;
    private static String logCurrentFileName;
    private static String logCurrentPath;
    private static String logBackupPath;
    private static String logBackupExtendsname;
    private static boolean hasStart;

    static {
        LogWriter.log = Logger.getLogger((Class) LogWriter.class);
        LogWriter.instance = null;
        LogWriter.timer = null;
        LogWriter.logger = null;
        LogWriter.appender = null;
        LogWriter.layout = null;
        LogWriter.logPattern = "";
        LogWriter.logCurrentFileName = "log.txt";
        LogWriter.logCurrentPath = "." + File.separator + "server" + File.separator + "currentlog" + File.separator;
        LogWriter.logBackupPath = "." + File.separator + "server" + File.separator + "backuplog" + File.separator;
        LogWriter.logBackupExtendsname = ".log";
        LogWriter.hasStart = false;
    }

    public static LogWriter getInstance() {
        if (LogWriter.instance == null) {
            LogWriter.instance = new LogWriter();
        }
        return LogWriter.instance;
    }

    public static void init() {
        LogWriter.logPattern = "%d [%t] %-5p %c - %m%n";
        init(LogWriter.logCurrentPath, LogWriter.logCurrentFileName);
    }

    public static void init(final String _path, final String _fileName) {
        init(_path, _fileName, "%d [%t] %-5p %c - %m%n");
    }

    public static void init(final String _path, final String _fileName, final String _log4jPattern) {
        if (LogWriter.hasStart) {
            return;
        }
        LogWriter.hasStart = true;
        if (_log4jPattern == null) {
            LogWriter.logPattern = "%m%n";
        } else {
            LogWriter.logPattern = _log4jPattern;
        }
        LogWriter.logCurrentPath = _path;
        LogWriter.logCurrentFileName = _fileName;
        startLog();
        LogWriter.timer = new Timer();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(11, 24);
        LogWriter.timer.scheduleAtFixedRate(getInstance(), calendar.getTime(), 86400000L);
    }

    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public final void writeObject(final ObjectOutputStream out) throws NotSerializableException {
        throw new NotSerializableException("This object cannot be serialized");
    }

    public final void readObject(final ObjectInputStream in) throws NotSerializableException {
        throw new NotSerializableException("This object cannot be deserialized");
    }

    private static void startLog() {
        try {
            LogWriter.logger = Logger.getLogger("UEN");
            LogWriter.layout = new PatternLayout(LogWriter.logPattern);
            File currentLogFolder = new File(LogWriter.logCurrentPath);
            if (!currentLogFolder.exists()) {
                currentLogFolder.mkdir();
            }
            try {
                LogWriter.appender = new FileAppender((Layout) LogWriter.layout, String.valueOf(LogWriter.logCurrentPath) + LogWriter.logCurrentFileName, true);
            } catch (Exception ex2) {
            }
            LogWriter.logger.setAdditivity(false);
            LogWriter.logger.addAppender((Appender) LogWriter.appender);
            LogWriter.logger.setLevel(Level.ALL);
        } catch (Exception ex) {
            LogWriter.log.error((Object) "Can not find logger!");
        }
    }

    @Override
    public void run() {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            String backupFileName = this.formatLogName();
            backupFileName = String.valueOf(backupFileName) + LogWriter.logBackupExtendsname;
            File backupLogFolder = new File(LogWriter.logBackupPath);
            if (!backupLogFolder.exists()) {
                backupLogFolder.mkdir();
            }
            File backupLog = new File(String.valueOf(LogWriter.logBackupPath) + backupFileName);
            synchronized (LogWriter.logger) {
                LogWriter.logger.removeAppender((Appender) LogWriter.appender);
                LogWriter.appender.close();
                File logfile = new File(String.valueOf(LogWriter.logCurrentPath) + LogWriter.logCurrentFileName);
                logfile.renameTo(backupLog);
                try {
                    LogWriter.appender = new FileAppender((Layout) LogWriter.layout, String.valueOf(LogWriter.logCurrentPath) + LogWriter.logCurrentFileName, false);
                } catch (Exception ex2) {
                }
                LogWriter.logger.addAppender((Appender) LogWriter.appender);
                LogWriter.logger.addAppender((Appender) new ConsoleAppender((Layout) new SimpleLayout()));
            }
            // monitorexit(LogWriter.logger)
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String formatLogName() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        DateFormat df = DateFormat.getDateTimeInstance();
        String backupFileName = df.format(gc.getTime());
        int index = backupFileName.indexOf(" ");
        String fileName = backupFileName.substring(0, index);
        String temp = "";
        temp = backupFileName.substring(index + 1, backupFileName.indexOf(":"));
        backupFileName = backupFileName.substring(backupFileName.indexOf(":") + 1);
        fileName = String.valueOf(fileName) + "-" + temp;
        temp = backupFileName.substring(0, backupFileName.indexOf(":"));
        fileName = String.valueOf(fileName) + "-" + temp;
        backupFileName = (temp = backupFileName.substring(backupFileName.indexOf(":") + 1));
        fileName = String.valueOf(fileName) + "-" + temp;
        return fileName;
    }

    public static void uninit() {
        LogWriter.logger = null;
    }

    public static void println(final int _event) {
        info(String.valueOf(_event));
    }

    public static void println(final String _event) {
        try {
            _event.replace('\'', '_');
            _event.replace('\"', '_');
            info(_event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void println(final byte[] _event) {
        info(new String(_event));
    }

    public static void println(final char[] _event) {
        info(new String(_event));
    }

    public static void println(final Exception _event) {
        info(String.valueOf(_event.toString()) + _event.getMessage());
    }

    public static void error(final Object obj, final Throwable e) {
        LogWriter.logger.error(obj, e);
    }

    public static void info(final String _msg) {
        LogWriter.logger.info((Object) _msg);
    }
}
