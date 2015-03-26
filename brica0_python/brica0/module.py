# -*- coding: utf-8 -*-

"""
module.py
=====

This module contains the abstract class `Module` and sample implementations
`NullModule`, `PipeModule`, and `ConstantModule`. The `CognitiveArchitecture`
is a special class designed to be the root of BriCA modules.

"""

__all__ = ["Module", "CognitiveArchitecture", "NullModule", "PipeModule", "ConstantModule"]

from abc import ABCMeta, abstractmethod
import copy
import numpy

# BrICA imports
from connection import *
from scheduler import *

class Module:
    """
    This class is an abstract class for creating modules. Subclasses must
    override the `fire(self)` method to specify its implementation. Some sample
    implementations `NullModule`, `PipeModule`, and `ConstantModule` are
    included for reference. The `CognitiveArchitecture` class is a special
    abstract sub-class of `Module`.
    """

    __metaclass__ = ABCMeta

    def __init__(self):

        """ Create a new Module Instance.

        Args:
          None.

        Returns:
          Module: A new Module instance.

        """

        """
        The last input / output time and its intervals.
        """
        self._last_input_time = 0.0
        self._last_output_time = 0.0
        self._interval = 1.0

        """
        The callbacks to call before input and after output.
        """
        self._input_callback = lambda caller : caller
        self._output_callback = lambda caller : caller

        """
        Input / output ports of this module.
        in-ports and out-ports are updated automatically.
        Users instead should work on state and results below.
        """    
        self._in_ports = {}
        self._out_ports = {}

        self._states = {}
        self._results = {}

        """
        Double output buffers.
        These buffers are use internally by this class to hide calculation
        process of this module (defined in the fire() method) from other
        modules
        """
        self._buffer0 = {}
        self._buffer1 = {}

        self._out_ports = self._buffer0
        self._results = self._buffer1

        self._connections = []
        self._sub_modules = {}

    def get_last_input_time(self):
        return self._last_input_time

    def get_last_output_time(self):
        return self._last_output_time

    def get_interval(self):
        return self._interval

    def set_interval(self, value):
        self._interval = value

    @abstractmethod
    def fire(self):
        """
        Users must override `fire(self)` method to define a new sub-class of
        Module. `fire(self)` method implements a function of the form

          results, states <- fire(in_ports, states)

        which states that this method should be a mutator of these two member
        variables (`results` and `states`), and the result of the computation
        should solely depend on the values stored in (`in_ports` and `states`).
        One important note here is that it shall mutate results member variable
        and not `out_ports`. Values stored in `results` will automatically be
        copied to `out_ports` by the scheduler which calls `update_output()`
        method of this Module, so these must be visible and accessible from
        other Modules. This procedure is especially necessary in concurrent
        execution of multiple Modules to avoid contention.

        Args:
          None.

        Returns:
          None.

        """

        return

    def make_in_port(self, id, length):
        """ Make an in-port of this Module.

        Args:
          id (str): a string ID.
          length (int): an initial length of the value vector.

        Returns:
          None.

        """
        self._in_ports[id] = numpy.zeros(length, dtype=numpy.short)

    def remove_in_port(self, id):
        """ Remove an in-port from this Module.

        Args:
          id (str): a string ID.

        Returns:
          None.

        """

        del self._in_ports[id]

    def get_in_port(self, id):
        """ Get values in an in-port from this Module.

        Args:
          id (str): a string ID.

        Returns:
          numpy.ndarray: a value vector for the in-port ID.

        """

        return self._in_ports[id]

    def make_out_port(self, id, length):
        """ Make an out-port of this Module.

        Args:
          id (str): a string ID.
          length (int): an initial length of the value vector.

        Returns:
          None.

        """
        self._buffer0[id] = (numpy.zeros(length, dtype=numpy.short))
        self._buffer1[id] = (numpy.zeros(length, dtype=numpy.short))

    def remove_out_port(self, id):
        """ Remove an out-port from this Module.

        Args:
          id (str): a string ID.

        Returns:
          None.

        """

        del self._buffer0[id]
        del self._buffer1[id]

    def get_out_port(self, id):
        """ Get values in an out-port from this Module.

        Args:
          id (str): a string ID.

        Returns:
          numpy.ndarray: a value vector for the out-port ID.

        """

        return self._out_ports[id]

    def set_state(self, id, v):
        """ Set a state vector for the given ID.

        Args:
          id (str): a string ID.
          v (numpy.ndarray): a numpy ndarray.

        Returns:
          None.

        """

        self._states[id] = v.copy()

    def get_state(self, id):
        """ Get a state vector for the given ID.

        Args:
          id (str): a string ID.

        Returns:
          numpy.ndarray: a value vector for the state ID.

        """

        return self._states[id]

    def clear_state(self, id):
        """ Clear a state vector for the given ID.

        Args:
          id (str): a string ID.

        Returns:
          None.

        """

        del self._states[id]

    def set_result(self, id, v):
        """ Set a result vector for the given ID.

        Args:
          id (str): a string ID.
          v (numpy.ndarray): a numpy ndarray.

        Returns:
          None.

        """

        self._results[id] = v.copy()

    def get_result(self, id):
        """ Get a result vector for the given ID.

        Args:
          id (str): a string ID.

        Returns:
          numpy.ndarray: a value vector for the result ID.

        """

        return self._results[id]

    def clear_result(self, id):
        """ Clear a result vector for the given ID.

        Args:
          id (str): a string ID.

        Returns:
          None.

        """

        del self._results[id]

    def make_connection(self, c):
        """ Add a connection to the connections list.

        Args:
          c (Connection): a connection object.

        Returns:
          None.

        """

        self._connections.append(c)

    def connect(self, module, from_id, to_id):
        """ Connect port of output ID to port of input ID in this module.

        Args:
          module (Module): a module to connect from.
          from_id (str): out-port id from other module.
          to_id (str): in-port id for this module.

        Returns:
          None.

        Raises:
          Error: if connection to `to_id` already exists.

        """

        for c in self._connections:
            if to_id == c.to_port_id:
                # TODO: throw exception of proper type.
                raise("Connection to this port already exists.")

        self.make_in_port(to_id, len(module.get_out_port(from_id)))
        self.make_connection(Connection(module, from_id, to_id))

    def add_sub_module(self, id, module):
        """ Add a sub-module to this module.

        Args:
          id (str): a string id.
          module (Module): module to add for `id`.

        Returns:
          None.

        """

        self._sub_modules[id] = module

    def get_sub_module(self, id):
        """ Get a sub-module for given id.

        Args:
          id (str): a string hierarchy id.

        Returns:
          Module: module for given `id`.

        """

        list = id.split(".")
        head = list.pop(0)
        child = self._sub_modules[head]

        if len(list) == 0:
            return child

        return child.get_sub_module(".".join(list))

    def get_all_sub_modules(self):
        """ Get a list of all sub-modules and its sub-modules.

        Args:
          None.

        Returns:
          Array<Module>: a list of all sub-modules.

        """

        # Shallow copy to hold module references
        list = self._sub_modules.values()
        for module in self._sub_modules.values():
            list.extend(module.get_all_sub_modules())

        return list

    def register_input_callback(self, callback):
        """ Register a function to callback before `input()`.

        Args:
          callback (function): a function to callback.

        Returns:
          None.
        
        """
        self._input_callback = callback

    def register_output_callback(self, callback):
        """ Register a function to callback after `output()`.

        Args:
          callback (function): a function to callback.

        Returns:
          None.
        
        """
        self._output_callback = callback

    def input(self, time):
        """ Obtain inputs from outputs of other Modules.

        This method collects the outputs of connected modules and sets the
        values to the in-ports. It is usually called by the scheduler.

        Args:
          time (float): the scheduler's current time.

        Returns:
          None.

        """

        self._input_callback(self)

        for c in self._connections:
            self._update_in_port(c)

        assert self._last_input_time <= time, "Input captured a time travel"
        self._last_input_time = time

    def _update_in_port(self, c):
        o = c.from_module.get_out_port(c.from_port_id)
        self._in_ports[c.to_port_id] = o.copy()

    def output(self, time):
        """ Expose results to `out_ports`

        This method exposes the computation results from `results` to
        `out_ports`. It is usually called by the scheduler.

        Args:
          time (float): the scheduler's current time.

        Returns:
          None.

        """

        tmp = self._out_ports
        self._out_ports = self._results
        self._results = tmp

        self._output_callback(self)
        assert self._last_output_time <= time, "Output captured a time travel"
        self._last_output_time = time

class CognitiveArchitecture(Module):
    """
    CognitiveArchitecture is the top level module for all cognitive
    architecture implementations. It contains a scheduler for stepping the
    internal modules.
    """

    def __init__(self, scheduler):
        """ Create a new CognitiveArchitecture Instance.

        Args:
          None.

        Returns:
          Module: A new NullModule instance.

        """

        super(CognitiveArchitecture, self).__init__()
        self._scheduler = scheduler

    def add_sub_module(self, id, module):
        """ Add a sub-module to this module.

        Args:
          id (str): a string id.
          module (Module): module to add for `id`.

        Returns:
          None.

        """

        super(CognitiveArchitecture, self).add_sub_module(id, module)
        self._update_scheduler()

    def step(self):
        """ Step scheduler once

        Args:
          None.

        Returns:
          float: the current time of the scheduler

        """
        return self._scheduler.step()

    def _update_scheduler(self):
        self._scheduler.update(self)

class NullModule(Module):
    """
    NullModule simply does nothing.
    """

    def __init__(self):
        """ Create a new NullModule Instance.

        Args:
          None.

        Returns:
          Module: A new NullModule instance.

        """

        super(NullModule, self).__init__()

    def fire(step):
        """ Do nothing. Just return.

        Args:
          None.

        Returns:
          None.

        """

        return

class PipeModule(Module):
    """
    PipeModule simply copies inputs to output ports of the same names.
    """

    def __init__(self):
        """ Create a new PipeModule Instance.

        Args:
          None.

        Returns:
          Module: A new PipeModule instance.

        """

        super(PipeModule, self).__init__()
        self._port_map = {}

    def map_port(self, in_id, out_id):
        """ Map from in-port to out port.

        Args:
          in_id (str): port id to map from.
          out_id (str): port id to map to.

        Returns:
          None.

        """

        self._port_map[in_id] = out_id

    def fire(self):
        """ Copy in-port contents to corresponding out-ports.

        Args:
          None.

        Returns:
          None.

        """

        for in_id in self._port_map.keys():
            out_id = self._port_map[in_id]
            v = self.get_in_port(in_id)
            if out_id in self._out_ports:
                self.set_result(out_id, v)

class ConstantModule(Module):
    """
    ConstantModule simply copies states to out ports.

    Users could use `set_state()` method to define the output of this Module.
    Values of in-ports are not used.
    """

    def __init__(self):
        """ Create a new ConstantModule Instance.

        Args:
          None.

        Returns:
          Module: A new ConstantModule instance.

        """

        super(ConstantModule, self).__init__()

    def fire(self):
        """ Copy state contents to results.

        Args:
          None.

        Returns:
          None.

        """

        for id in self._states.keys():
            self._results[id] = self._states[id].copy()
