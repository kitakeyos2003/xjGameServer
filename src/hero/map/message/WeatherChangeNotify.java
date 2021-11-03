// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import hero.map.EMapWeather;
import yoyo.core.packet.AbsResponseMessage;

public class WeatherChangeNotify extends AbsResponseMessage {

    private EMapWeather weather;
    private int direction;

    public WeatherChangeNotify(final EMapWeather _weather, final int _direction) {
        this.weather = _weather;
        this.direction = _direction;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.weather.getTypeValue());
        this.yos.writeByte(this.direction);
    }
}
