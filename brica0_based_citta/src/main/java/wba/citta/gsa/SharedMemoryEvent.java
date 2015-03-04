package wba.citta.gsa;

import java.util.EventObject;

public class SharedMemoryEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public SharedMemoryEvent(SharedMemory sharedMemory) {
        super(sharedMemory);
    }
    
    @Override
    public SharedMemory getSource() {
        return (SharedMemory)source;
    }
}
