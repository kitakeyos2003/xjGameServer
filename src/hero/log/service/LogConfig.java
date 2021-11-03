// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

import java.util.List;
import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.FileAppender;
import java.io.File;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;
import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import org.apache.log4j.Logger;
import java.util.HashMap;
import yoyo.service.base.AbsConfig;

public class LogConfig extends AbsConfig {

    public static HashMap<String, Logger> myLogger;

    static {
        LogConfig.myLogger = new HashMap<String, Logger>();
    }

    @Override
    public void init(final Element _root) throws Exception {
        List nodeList = _root.element("logservice").selectNodes("log");
        for (int i = 0; i < nodeList.size(); ++i) {
            Element element = (Element) nodeList.get(i);
            String name = element.attributeValue("name");
            String level = element.elementTextTrim("close");
            String path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("filePath");
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
                File logFile = new File(path);
                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                }
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
            LogConfig.myLogger.put(name, logger);
        }
    }

    public Logger getLogger(final String name) {
        return LogConfig.myLogger.get(name);
    }
}
