package wba.citta.gsa;

import java.util.ArrayList;
import java.util.Collection;

public class Goal extends ArrayList<Integer> {
    private static final long serialVersionUID = 1L;

    public Goal() {
        super();
    }

    public Goal(int cap) {
        super(cap);
        for (int i = 0; i < cap; i++) {
            add(0);
        }
    }

    public Goal(Integer... a) {
        super(a.length);
        for (int i = 0; i < a.length; i++)
            add(a[i]);
    }

    public Goal(Collection<? extends Integer> c) {
        super(c);
    }

    public Integer[] toArray() {
        Integer[] retval = new Integer[size()];
        return super.toArray(retval);
    }
}
