// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import hero.map.message.WeatherChangeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import javolution.util.FastList;
import hero.map.Map;
import java.util.TimerTask;
import hero.map.EMapWeather;
import java.util.Random;
import java.util.Timer;

public class WeatherManager {

    private WeatherControler rainWeatherControler;
    private WeatherControler snowWeatherControler;
    private WeatherControler cloudyWeatherControler;
    private WeatherControler petalWeatherControler;
    private WeatherControler bubbleWeatherControler;
    private Timer timer;
    private static WeatherManager instance;
    private static final Random WEATHER_CHANGE_RANDOM_BUILDER;
    private static final int WEATHER_KEEP_TIME_MIN = 300000;
    private static final int WEATHER_KEEP_TIME_MAX = 480000;
    private static final int CHANGE_INTERVAL_MIN = 600000;
    private static final int CHANGE_INTERVAL_MAX = 1800000;
    private static final int CONTROLER_START_RELAY = 480000;
    private static final int CONTROLER_START_REVISE = 180000;
    private static final int CHANGE_CHECK_INTERVAL = 30000;
    private static final int[] NONE_WEATHER_DESC;

    static {
        WEATHER_CHANGE_RANDOM_BUILDER = new Random();
        NONE_WEATHER_DESC = new int[2];
    }

    private WeatherManager() {
        this.rainWeatherControler = new WeatherControler(EMapWeather.RAIN);
        this.snowWeatherControler = new WeatherControler(EMapWeather.SNOW);
        this.cloudyWeatherControler = new WeatherControler(EMapWeather.CLOUDY);
        this.petalWeatherControler = new WeatherControler(EMapWeather.PETAL);
        this.bubbleWeatherControler = new WeatherControler(EMapWeather.BUBBLE);
        (this.timer = new Timer()).schedule(this.rainWeatherControler, WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000) + 180000, 30000L);
        this.timer.schedule(this.snowWeatherControler, WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000) + 180000, 30000L);
        this.timer.schedule(this.cloudyWeatherControler, WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000) + 180000, 30000L);
        this.timer.schedule(this.petalWeatherControler, WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000) + 180000, 30000L);
        this.timer.schedule(this.bubbleWeatherControler, WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000) + 180000, 30000L);
    }

    public static WeatherManager getInstance() {
        if (WeatherManager.instance == null) {
            WeatherManager.instance = new WeatherManager();
        }
        return WeatherManager.instance;
    }

    public void add(final Map _map) {
        switch (_map.getWeather()) {
            case RAIN: {
                this.rainWeatherControler.add(_map);
                break;
            }
            case SNOW: {
                this.snowWeatherControler.add(_map);
                break;
            }
            case CLOUDY: {
                this.cloudyWeatherControler.add(_map);
                break;
            }
            case BUBBLE: {
                this.bubbleWeatherControler.add(_map);
                break;
            }
            case PETAL: {
                this.petalWeatherControler.add(_map);
                break;
            }
        }
    }

    public void remove(final Map _map) {
        switch (_map.getWeather()) {
            case RAIN: {
                this.rainWeatherControler.remove(_map);
                break;
            }
            case SNOW: {
                this.snowWeatherControler.remove(_map);
                break;
            }
            case CLOUDY: {
                this.cloudyWeatherControler.remove(_map);
                break;
            }
            case BUBBLE: {
                this.bubbleWeatherControler.remove(_map);
                break;
            }
            case PETAL: {
                this.petalWeatherControler.remove(_map);
                break;
            }
        }
    }

    public int[] getWeather(final EMapWeather _weather) {
        switch (_weather) {
            case NONE: {
                return WeatherManager.NONE_WEATHER_DESC;
            }
            case RAIN: {
                return this.rainWeatherControler.getWeatherDesc();
            }
            case SNOW: {
                return this.snowWeatherControler.getWeatherDesc();
            }
            case CLOUDY: {
                return this.cloudyWeatherControler.getWeatherDesc();
            }
            case BUBBLE: {
                return this.bubbleWeatherControler.getWeatherDesc();
            }
            case PETAL: {
                return this.petalWeatherControler.getWeatherDesc();
            }
            default: {
                return WeatherManager.NONE_WEATHER_DESC;
            }
        }
    }

    private static int getWeatherRandomKeepTime() {
        int keepTime;
        for (keepTime = WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000); keepTime < 300000; keepTime = WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(480000)) {
        }
        return keepTime;
    }

    private static long getNextChangeTime() {
        int keepTime;
        for (keepTime = WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(1800000); keepTime < 600000; keepTime = WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(1800000)) {
        }
        return keepTime + System.currentTimeMillis();
    }

    private class WeatherControler extends TimerTask {

        private FastList<Map> mapList;
        private EMapWeather weather;
        private long startTime;
        private long endTime;
        private boolean ing;
        private int[] weatherDesc;

        public WeatherControler(final EMapWeather _weather) {
            this.weather = _weather;
            this.weatherDesc = new int[2];
            this.mapList = (FastList<Map>) new FastList();
        }

        public void add(final Map _map) {
            this.mapList.add(_map);
        }

        public int[] getWeatherDesc() {
            return this.weatherDesc;
        }

        public void remove(final Map _map) {
            this.mapList.remove(_map);
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if (!this.ing) {
                if (now >= this.startTime && (now < this.endTime || 0L == this.endTime)) {
                    this.endTime = getWeatherRandomKeepTime() + now;
                    this.weatherDesc[0] = this.weather.getTypeValue();
                    this.weatherDesc[1] = WeatherManager.WEATHER_CHANGE_RANDOM_BUILDER.nextInt(2) + 1;
                    synchronized (this.mapList) {
                        for (final Map map : this.mapList) {
                            MapSynchronousInfoBroadcast.getInstance().put(map, new WeatherChangeNotify(this.weather, this.weatherDesc[1]), false, 0);
                        }
                    }
                    // monitorexit(this.mapList)
                    this.ing = true;
                }
            } else if (now >= this.endTime) {
                this.startTime = getNextChangeTime();
                this.endTime = 0L;
                synchronized (this.mapList) {
                    for (final Map map : this.mapList) {
                        MapSynchronousInfoBroadcast.getInstance().put(map, new WeatherChangeNotify(EMapWeather.NONE, 0), false, 0);
                    }
                }
                // monitorexit(this.mapList)
                this.weatherDesc[0] = 0;
                this.weatherDesc[1] = 0;
                this.ing = false;
            }
        }
    }
}
