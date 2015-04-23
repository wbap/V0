package wba.citta.brica0;

import java.util.List;

import wba.citta.gsa.Goal;

public abstract class BricaPerspective extends BasicBricaPerspective {
    final String latchPort;
    final String latchAvailPort;

    public BricaPerspective(int size,
            List<String> perNodeStackPorts,
            List<String> perNodeStackPushAvailPorts,
            List<String> perNodeStackTopPorts,
            List<String> perNodeStackRemoveAllOpPorts,
            List<String> perNodeStackRemoveOpPorts,
            List<String> perNodeStackTopDesignationStatePorts,
            String latchPort, String latchAvailPort) {
        super(size, perNodeStackPorts,
                perNodeStackPushAvailPorts, perNodeStackTopPorts,
                perNodeStackRemoveAllOpPorts, perNodeStackRemoveOpPorts,
                perNodeStackTopDesignationStatePorts);
        this.makeOutPort(latchPort, size);
        this.makeOutPort(latchAvailPort, size);
        this.latchPort = latchPort;
        this.latchAvailPort = latchAvailPort;
    }

    @Override
    public Goal getState() {
        final Goal retval = new Goal(size);
        final short[] state = getInPort(latchPort);
        assert state.length == retval.size();
        for (int i = 0; i < size; i++) {
            retval.set(i, (int)state[i]);
        }
        return retval;
    }

    @Override
    public void setState(Goal state) {
        throw new UnsupportedOperationException();
    }

    public abstract void doFire();
}
