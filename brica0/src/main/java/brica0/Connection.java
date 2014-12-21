package brica0;

public class Connection {

	public Module from_module;
	public String from_port_id;

	// there is no to_module because to_module owns this connection.
	public String to_port_id;
	
	public Connection(Module from, String from_port_id, String to_port_id) {
		this.from_module = from;
		this.from_port_id = from_port_id;
		this.to_port_id = to_port_id;
	}

	
	public String toString() {
	    return String.format("Connection %s:%s -> port:%s", from_module, from_port_id, to_port_id);
	}
}
