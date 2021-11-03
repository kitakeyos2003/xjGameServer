// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

public class ZipIntMultShortHashMap {

    public static final int DEFAULT_LOAD_FACTOR = 75;
    public static final int SUB_ELEMENT_SIZE = 3;
    private final int loadFactor;
    private Element[] buckets;
    private final boolean isPowerOfTwo;
    private int size;

    public ZipIntMultShortHashMap(final int initialCapacity) {
        this(initialCapacity, 75);
    }

    public ZipIntMultShortHashMap(int initialCapacity, final int loadFactor) {
        int capacity;
        for (initialCapacity = initialCapacity * 100 / loadFactor, capacity = 1; initialCapacity > capacity; capacity <<= 1) {
        }
        this.isPowerOfTwo = (capacity == initialCapacity);
        this.buckets = new Element[initialCapacity];
        this.loadFactor = loadFactor;
    }

    public boolean put(final int key, final short value) {
        if (this.size * 100 / this.buckets.length > this.loadFactor) {
            this.increaseSize();
        }
        int index;
        if (this.isPowerOfTwo) {
            index = (key & Integer.MAX_VALUE & this.buckets.length - 1);
        } else {
            index = (key & Integer.MAX_VALUE) % this.buckets.length;
        }
        Element element = this.buckets[index];
        if (element == null) {
            element = new Element(key, new short[3]);
            element.values[0] = value;
            Element element2 = element;
            ++element2.size;
            this.buckets[index] = element;
            ++this.size;
            return true;
        }
        Element lastElement = element;
        while (element.key != key) {
            lastElement = element;
            element = element.next;
            if (element == null) {
                element = new Element(key, new short[3]);
                element.values[0] = value;
                Element element3 = element;
                ++element3.size;
                this.buckets[index] = element;
                ++this.size;
                lastElement.next = element;
                return true;
            }
        }
        Element element4 = element;
        ++element4.size;
        if (element.size == element.values.length) {
            short[] newValues = new short[element.values.length * 2];
            System.arraycopy(element.values, 0, newValues, 0, element.values.length);
            element.values = newValues;
        }
        element.values[element.size - 1] = value;
        return true;
    }

    public Element get(final int key) {
        int index;
        if (this.isPowerOfTwo) {
            index = (key & Integer.MAX_VALUE & this.buckets.length - 1);
        } else {
            index = (key & Integer.MAX_VALUE) % this.buckets.length;
        }
        Element element = this.buckets[index];
        if (element == null) {
            return null;
        }
        while (element.key != key) {
            element = element.next;
            if (element == null) {
                return null;
            }
        }
        return element;
    }

    public short remove(final int key) {
        int index;
        if (this.isPowerOfTwo) {
            index = (key & Integer.MAX_VALUE & this.buckets.length - 1);
        } else {
            index = (key & Integer.MAX_VALUE) % this.buckets.length;
        }
        Element element = this.buckets[index];
        if (element == null) {
            return -1;
        }
        Element lastElement = null;
        while (element.key != key) {
            lastElement = element;
            element = element.next;
            if (element == null) {
                return -1;
            }
        }
        if (lastElement == null) {
            this.buckets[index] = element.next;
        } else {
            lastElement.next = element.next;
        }
        --this.size;
        return 1;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public boolean containsKey(final int key) {
        return this.get(key) != null;
    }

    public void clear() {
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = null;
        }
        this.size = 0;
    }

    public int[] keys() {
        int[] keys = new int[this.size];
        int index = 0;
        for (int i = 0; i < this.buckets.length; ++i) {
            for (Element element = this.buckets[i]; element != null; element = element.next) {
                keys[index] = element.key;
                ++index;
            }
        }
        return keys;
    }

    private void increaseSize() {
        int newCapacity;
        if (this.isPowerOfTwo) {
            newCapacity = this.buckets.length << 1;
        } else {
            newCapacity = (this.buckets.length << 1) - 1;
        }
        Element[] newBuckets = new Element[newCapacity];
        for (int i = 0; i < this.buckets.length; ++i) {
            Element lastElement;
            for (Element element = this.buckets[i]; element != null; element = element.next, lastElement.next = null) {
                int index;
                if (this.isPowerOfTwo) {
                    index = (element.key & Integer.MAX_VALUE & newCapacity - 1);
                } else {
                    index = (element.key & Integer.MAX_VALUE) % newCapacity;
                }
                Element newElement = newBuckets[index];
                if (newElement == null) {
                    newBuckets[index] = element;
                } else {
                    while (newElement.next != null) {
                        newElement = newElement.next;
                    }
                    newElement.next = element;
                }
                lastElement = element;
            }
        }
        this.buckets = newBuckets;
    }

    public static final class Element {

        public final int key;
        public short[] values;
        public short size;
        public Element next;

        public Element(final int key, final short[] value) {
            this.key = key;
            this.values = value;
        }
    }
}
