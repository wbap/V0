package wba.citta.gsa;

import java.util.EventObject;

public class SharedMemoryEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public SharedMemoryEvent(ISharedMemory sharedMemory) {
        super(sharedMemory);
    }
    
    @Override
    public ISharedMemory getSource() {
        return (ISharedMemory)source;
    }
}
