package wba.citta.gsa;

public interface IListenableSharedMemory extends ISharedMemory {
    public abstract void addChangeListener(SharedMemoryEventListener listener);

    public abstract void removeChangeListener(SharedMemoryEventListener listener);
}
