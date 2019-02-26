package ch.dajay42.collections;

/**Sparse int vector / int-int map with non-negative keys
 * Based on https://github.com/mikvor/hashmapTest
 */
public class NonNegativeIntMapInt{
	
	private static final int INT_PHI = 0x9E3779B9;
	
	public static int phiMix(final int x){
		final int h = x * INT_PHI;
		return h ^ (h >> 16);
	}
	
	private static final int FREE_KEY = -1;

    public static final int NO_VALUE = 0;

    /** Keys and values */
    private int[] m_data;

    /** Fill factor, must be between (0 and 1) */
    private final float m_fillFactor;
    /** We will resize a map once it reaches this size */
    private int m_threshold;
    /** Current map size */
    private int m_size;

    /** Mask to calculate the original position */
    private int m_mask;
    private int m_mask2;

    public NonNegativeIntMapInt(final int size, final float fillFactor){
        if (fillFactor <= 0 || fillFactor >= 1)
            throw new IllegalArgumentException("FillFactor must be in (0, 1)");
        if (size <= 0)
            throw new IllegalArgumentException("Size must be positive!");
        final int capacity = arraySize(size, fillFactor);
        m_mask = capacity - 1;
        m_mask2 = capacity*2 - 1;
        m_fillFactor = fillFactor;

        m_data = new int[capacity * 2];
        m_threshold = (int) (capacity * fillFactor);
    }

    private int arraySize(final int size, final float fillFactor){
    	final int n = (int) Math.ceil(size / fillFactor);
    	int s = 1;
    	while(s < n)
    		s <<= 1;
		return s;
	}

	public int get(final int key){
        if (key < 0){
            throw new IndexOutOfBoundsException("Key must be non-negative!");
        }
        
        int ptr = (phiMix(key) & m_mask) << 1;

        if (key == FREE_KEY)
            return NO_VALUE;

        int k = m_data[ptr];

        if (k == FREE_KEY)
            return NO_VALUE;  //end of chain already
        if (k == key) //we check FREE prior to this call
            return m_data[ptr + 1];

        while (true){
            ptr = (ptr + 2) & m_mask2; //that's next index
            k = m_data[ptr];
            if (k == FREE_KEY)
                return NO_VALUE;
            if (k == key)
                return m_data[ptr + 1];
        }
    }

    public int put(final int key, final int value){
        if (key < 0){
            throw new IndexOutOfBoundsException("Key must be non-negative!");
        }

        int ptr = (phiMix(key) & m_mask) << 1;
        int k = m_data[ptr];
        if (k == FREE_KEY){//end of chain already
            m_data[ptr] = key;
            m_data[ptr + 1] = value;
            if (m_size >= m_threshold)
                rehash(m_data.length * 2); //size is set inside
            else
                ++m_size;
            return NO_VALUE;
        }
        else if (k == key){//we check FREE prior to this call
            final int ret = m_data[ptr + 1];
            m_data[ptr + 1] = value;
            return ret;
        }

        while (true){
            ptr = (ptr + 2) & m_mask2; //that's next index calculation
            k = m_data[ptr];
            if (k == FREE_KEY){
                m_data[ptr] = key;
                m_data[ptr + 1] = value;
                if (m_size >= m_threshold)
                    rehash(m_data.length * 2); //size is set inside
                else
                    ++m_size;
                return NO_VALUE;
            }
            else if (k == key){
                final int ret = m_data[ptr + 1];
                m_data[ptr + 1] = value;
                return ret;
            }
        }
    }

    public int remove(final int key){
        if (key < 0){
            throw new IndexOutOfBoundsException("Key must be non-negative!");
        }
        
        int ptr = (phiMix(key) & m_mask) << 1;
        int k = m_data[ptr];
        if (k == key){//we check FREE prior to this call
            final int res = m_data[ptr + 1];
            shiftKeys(ptr);
            --m_size;
            return res;
        }
        else if (k == FREE_KEY)
            return NO_VALUE;  //end of chain already
        while (true){
            ptr = (ptr + 2) & m_mask2; //that's next index calculation
            k = m_data[ptr];
            if (k == key){
                final int res = m_data[ptr + 1];
                shiftKeys(ptr);
                --m_size;
                return res;
            }
            else if (k == FREE_KEY)
                return NO_VALUE;
        }
    }

    private int shiftKeys(int pos){
        // Shift entries with the same hash.
        int last, slot;
        int k;
        final int[] data = this.m_data;
        while (true){
            pos = ((last = pos) + 2) & m_mask2;
            while (true){
                if ((k = data[pos]) == FREE_KEY){
                    data[last] = FREE_KEY;
                    return last;
                }
                slot = (phiMix(k) & m_mask) << 1; //calculate the starting slot for the current key
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos)
                	break;
                pos = (pos + 2) & m_mask2; //go to the next entry
            }
            data[last] = k;
            data[last + 1] = data[pos + 1];
        }
    }


    public int size(){
        return m_size;
    }

    private void rehash(final int newCapacity){
        m_threshold = (int) (newCapacity/2 * m_fillFactor);
        m_mask = newCapacity/2 - 1;
        m_mask2 = newCapacity - 1;

        final int oldCapacity = m_data.length;
        final int[] oldData = m_data;

        m_data = new int[newCapacity];
        m_size = 0;

        for (int i = 0; i < oldCapacity; i += 2) {
            final int oldKey = oldData[i];
            if(oldKey != FREE_KEY)
                put(oldKey, oldData[i + 1]);
        }
    }
}
