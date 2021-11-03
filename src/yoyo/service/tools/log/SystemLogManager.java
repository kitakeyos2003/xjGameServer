// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.log;

import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;
import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import org.apache.log4j.Logger;
import java.util.HashMap;
import yoyo.service.base.AbsConfig;

public class SystemLogManager extends AbsConfig {

    public static HashMap<String, Logger> loggerMap;

    static {
        SystemLogManager.loggerMap = new HashMap<String, Logger>();
    }

    public Logger getLoggerByName(final String name) {
        return SystemLogManager.loggerMap.get(name);
    }

    @Override
    public void init(final Element elememt) throws Exception {
        List logList = elememt.selectNodes("log");
        for (int i = 0; i < logList.size(); ++i) {
            Element element = (Element) logList.get(i);
            String name = element.attributeValue("name");
            String level = element.elementTextTrim("close");
            String path = element.elementTextTrim("filePath");
            path = String.valueOf(YOYOSystem.HOME) + path;
            String timepattern = element.elementTextTrim("timePattern");
            String format = element.elementTextTrim("format");
            Logger logger = Logger.getLogger(name.trim());
            if (level.equalsIgnoreCase("no")) {
                logger.setLevel(Level.DEBUG);
            } else if (level.equalsIgnoreCase("yes")) {
                logger.setLevel(Level.OFF);
            } else {
                logger.setLevel(Level.OFF);
            }
            logger.setAdditivity(false);
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(format);
            try {
                FileAppender fa = null;
                if (timepattern.equals("")) {
                    fa = new FileAppender((Layout) layout, path, false);
                } else {
                    fa = (FileAppender) new DailyRollingFileAppender((Layout) layout, path, timepattern);
                }
                logger.addAppender((Appender) fa);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            SystemLogManager.loggerMap.put(name, logger);
        }
        String lv = elememt.valueOf("//logservice/rootLogger/close");
        String format2 = elememt.valueOf("//logservice/rootLogger/format");
        PatternLayout layout2 = new PatternLayout();
        layout2.setConversionPattern(format2.trim());
        BasicConfigurator.configure((Appender) new ConsoleAppender((Layout) layout2));
        Logger rootLogger = Logger.getRootLogger();
        if (lv.trim().equalsIgnoreCase("no")) {
            rootLogger.setLevel(Level.DEBUG);
        } else if (lv.equalsIgnoreCase("yes")) {
            rootLogger.setLevel(Level.OFF);
        } else {
            rootLogger.setLevel(Level.OFF);
        }
        SystemLogManager.loggerMap.put("root", rootLogger);
        System.out.println("SysInfoConfig init(),OK!");
    }
}
