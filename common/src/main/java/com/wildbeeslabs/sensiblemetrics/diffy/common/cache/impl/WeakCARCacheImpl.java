package com.wildbeeslabs.sensiblemetrics.diffy.common.cache.impl;

import com.wildbeeslabs.sensiblemetrics.diffy.common.cache.exception.ComputationErrorException;
import com.wildbeeslabs.sensiblemetrics.diffy.common.cache.interfaces.CacheKeyFilter;
import com.wildbeeslabs.sensiblemetrics.diffy.common.cache.interfaces.WeakCARCache;
import com.wildbeeslabs.sensiblemetrics.diffy.common.cache.interfaces.WeakHashClock;
import com.wildbeeslabs.sensiblemetrics.diffy.common.cache.interfaces.WeakHashLRU;
import org.apache.commons.lang3.concurrent.Computable;

import java.util.Map;

/**
 * Implements the CAR algorithm as found here:
 * <p>
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.105.6057
 *
 * @author jwells
 */
public class WeakCARCacheImpl<K, V> implements WeakCARCache<K, V> {
    private final Computable<K, V> computable;
    private final int maxSize;  // TODO, make this dynamic

    private final WeakHashClock<K, CarValue<V>> t1;
    private final WeakHashClock<K, CarValue<V>> t2;
    private final WeakHashLRU<K> b1;
    private final WeakHashLRU<K> b2;

    // The target size of t1, adaptive
    private int p = 0;

    public WeakCARCacheImpl(Computable<K, V> computable, int maxSize, boolean isWeak) {
        this.computable = computable;
        this.maxSize = maxSize;

        t1 = GeneralUtilities.getWeakHashClock(isWeak);
        t2 = GeneralUtilities.getWeakHashClock(isWeak);
        b1 = GeneralUtilities.getWeakHashLRU(isWeak);
        b2 = GeneralUtilities.getWeakHashLRU(isWeak);
    }

    private V getValueFromT(K key) {
        CarValue<V> cValue = t1.get(key);
        if (cValue != null) {
            // So fast
            cValue.referenceBit = true;
            return cValue.value;
        }

        cValue = t2.get(key);
        if (cValue != null) {
            // So fast
            cValue.referenceBit = true;
            return cValue.value;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#compute(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public V compute(K key) {
        V value = getValueFromT(key);
        if (value != null) return value;

        synchronized (this) {
            value = getValueFromT(key);
            if (value != null) return value;

            // Cache Miss.  First, get the value.  Any failures
            // will bubble up prior to us messing with any data structures
            try {
                value = computable.compute(key);
            } catch (ComputationErrorException cee) {
                // In this case the value should not be kept in the cache
                return (V) cee.getComputation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int cacheSize = getValueSize();
            if (cacheSize >= maxSize) {
                replace();

                boolean inB1 = b1.contains(key);
                boolean inB2 = b2.contains(key);
                if (!inB1 && !inB2) {
                    if ((t1.size() + b1.size()) >= maxSize) {
                        b1.remove();
                    } else if ((t1.size() + t2.size() + b1.size() + b2.size()) >= (2 * maxSize)) {
                        b2.remove();
                    }
                }
            }

            boolean inB1 = b1.contains(key);
            boolean inB2 = b2.contains(key);

            if (!inB1 && !inB2) {
                t1.put(key, new CarValue<V>(value));
            } else if (inB1) {
                int b1size = b1.size();
                if (b1size == 0) b1size = 1;  // Can happen in a weak situation, we fake the one

                int b2size = b2.size();

                int ratio = b2size / b1size;  // integer division
                if (ratio <= 0) ratio = 1;

                p = p + ratio;
                if (p > maxSize) p = maxSize;

                b1.remove(key);
                t2.put(key, new CarValue<V>(value));
            } else {
                // Must be in B2
                int b2size = b2.size();
                if (b2size == 0) b2size = 1;  // Can happen in a weak situation, we fake the one

                int b1size = b1.size();

                int ratio = b1size / b2size;
                if (ratio <= 0) ratio = 1;

                p = p - ratio;
                if (p < 0) p = 0;

                b2.remove(key);
                t2.put(key, new CarValue<V>(value));
            }
        }

        return value;
    }

    private void replace() {
        boolean found = false;
        while (!found) {
            int trySize = p;
            if (trySize < 1) trySize = 1;

            if (t1.size() >= trySize) {
                Map.Entry<K, CarValue<V>> entry = t1.next();

                if (entry.getValue().referenceBit == false) {
                    found = true;

                    t1.remove(entry.getKey());
                    b1.add(entry.getKey());
                } else {
                    CarValue<V> entryValue = entry.getValue();
                    entryValue.referenceBit = false;

                    t1.remove(entry.getKey());
                    t2.put(entry.getKey(), entryValue);
                }
            } else {
                Map.Entry<K, CarValue<V>> entry = t2.next();

                if (entry.getValue().referenceBit == false) {
                    found = true;

                    t2.remove(entry.getKey());
                    b2.add(entry.getKey());
                } else {
                    CarValue<V> entryValue = entry.getValue();
                    entryValue.referenceBit = false;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getKeySize()
     */
    @Override
    public synchronized int getKeySize() {
        return t1.size() + t2.size() + b1.size() + b2.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getValueSize()
     */
    @Override
    public synchronized int getValueSize() {
        return t1.size() + t2.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#clear()
     */
    @Override
    public synchronized void clear() {
        t1.clear();
        t2.clear();
        b1.clear();
        b2.clear();

        p = 0;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getMaxSize()
     */
    @Override
    public int getMaxSize() {
        return maxSize;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getComputable()
     */
    @Override
    public Computable<K, V> getComputable() {
        return computable;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#remove(java.lang.Object)
     */
    @Override
    public synchronized boolean remove(K key) {
        if (t1.remove(key) == null) {
            if (t2.remove(key) == null) {
                if (!b1.remove(key)) {
                    return b2.remove(key);
                }

                return true;
            }

            return true;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#releaseMatching(org.glassfish.hk2.utilities.cache.CacheKeyFilter)
     */
    @Override
    public synchronized void releaseMatching(CacheKeyFilter<K> filter) {
        if (filter == null) return;

        b2.releaseMatching(filter);
        b1.releaseMatching(filter);
        t1.releaseMatching(filter);
        t2.releaseMatching(filter);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#clearStaleReferences()
     */
    @Override
    public synchronized void clearStaleReferences() {
        t1.clearStaleReferences();
        t2.clearStaleReferences();
        b1.clearStaleReferences();
        b2.clearStaleReferences();
    }

    private static class CarValue<V> {
        private final V value;
        private volatile boolean referenceBit = false;

        private CarValue(V value) {
            this.value = value;
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getT1Size()
     */
    @Override
    public int getT1Size() {
        return t1.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getT2Size()
     */
    @Override
    public int getT2Size() {
        return t2.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getB1Size()
     */
    @Override
    public int getB1Size() {
        return b1.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getB2Size()
     */
    @Override
    public int getB2Size() {
        return b2.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#getP()
     */
    @Override
    public int getP() {
        return p;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.cache.WeakCARCache#dumpAllLists()
     */
    @Override
    public String dumpAllLists() {
        StringBuffer sb = new StringBuffer("p=" + p + "\nT1: " + t1.toString() + "\n");
        sb.append("T2: " + t2.toString() + "\n");
        sb.append("B1: " + b1.toString() + "\n");
        sb.append("B2: " + b2.toString() + "\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "WeakCARCacheImpl(t1size=" + t1.size() + ",t2Size=" + t2.size() +
            ",b1Size=" + b1.size() + ",b2Size=" + b2.size() + ",p=" + p + "," +
            System.identityHashCode(this) + ")";
    }


}
