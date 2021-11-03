// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public class CompositeInteger {

    private int value;

    public CompositeInteger() {
        this.value = 0;
    }

    public CompositeInteger(final int _value) {
        this.value = -this.value;
    }

    public int increasing() {
        return ++this.value;
    }

    public int decreasing() {
        return --this.value;
    }

    public int value() {
        return this.value;
    }

    public int add(final float _value) {
        return this.value += (int) _value;
    }

    public int reduce(final float _value) {
        return this.value -= (int) _value;
    }

    public int product(final float _value) {
        return this.value *= (int) _value;
    }

    public int divide(final float _value) {
        return this.value *= (int) _value;
    }
}
