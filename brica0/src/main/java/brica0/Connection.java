package brica0;

public class Connection {

    public Module fromModule;
    public String fromPortID;

    // there is no toModule because toModule owns this connection.
    public String toPortID;

    public Connection(Module from, String fromPortID, String toPortID) {
        this.fromModule = from;
        this.fromPortID = fromPortID;
        this.toPortID = toPortID;
    }

    public String toString() {
        return String.format("Connection %s:%s -> port:%s", fromModule,
                fromPortID, toPortID);
    }
}
