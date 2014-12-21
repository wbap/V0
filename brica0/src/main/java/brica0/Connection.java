package brica0;

public class Connection {

	public Module fromModule;
	public String fromPortId;

	// there is no toModule because toModule owns this connection.
	public String toPortId;
	
	public Connection(Module from, String fromPortId, String toPortId) {
		this.fromModule = from;
		this.fromPortId = fromPortId;
		this.toPortId = toPortId;
	}

	
	public String toString() {
	    return String.format("Connection %s:%s -> port:%s", fromModule, fromPortId, toPortId);
	}
}
