package wba.citta.gsa;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class State extends ArrayList<Integer> {
    private static final long serialVersionUID = 1L;

    public State(int cap) {
        super(cap);
        for (int i = 0; i < cap; i++) {
            super.add(null);
        }
    }

    public State(List<Integer> goal, boolean[] useNode) {
        super(goal.size());
        for (int i = 0; i < useNode.length; i++) {
            if (useNode[i])
                super.add(goal.get(i));
        }
    }

    public State(Collection<? extends Integer> c) {
        super(c);
    }

    public boolean add(Integer _) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends Integer> _) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Integer _) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> _) {
        throw new UnsupportedOperationException();
    }

    public void add(int i, Integer _) {
        throw new UnsupportedOperationException();
    }

    public void remove(int i, Integer _) {
        throw new UnsupportedOperationException();
    }
}
