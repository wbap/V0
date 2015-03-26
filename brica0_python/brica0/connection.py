# -*- coding: utf-8 -*-

"""
connection.py
=====


"""

__all__ = ["Connection"]

class Connection:

    def __init__(self, module, from_port_id, to_port_id):

        """ Create a Connection instance.

        Args:
          module (Module): module containing the `from_port_id`
          from_port_id (str): out-port id of `module`
          to_port_id (str): in-port id of target module

        Returns:
          Connection: a new Connection instance.

        """

        self.from_module = module
        self.from_port_id = from_port_id
        self.to_port_id = to_port_id
        return

    def to_string(self):
        """ Format connection as string

        Args:
          None.

        Return:
          str: Connection formatted as string

        """
        return "Connection %s:%s -> port:%s" % (self.from_module, self.from_port_id, self.to_port_id)
