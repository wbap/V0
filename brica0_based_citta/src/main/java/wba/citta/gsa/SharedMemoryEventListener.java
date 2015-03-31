package wba.citta.gsa;

import java.util.EventListener;

public interface SharedMemoryEventListener extends EventListener {
    public void sharedMemoryChanged(SharedMemoryEvent sharedMemory);
}
