# -*- coding: utf-8 -*-

"""
scheduler.py
=====

This module contains the abstract class `Scheduler` and its implementations
`VirtualTimeSyncScheduler`

"""

__all__ = ["Scheduler", "VirtualTimeSyncScheduler"]

from abc import ABCMeta, abstractmethod
import copy
import numpy

# BrICA imports
from connection import Connection

class Scheduler:
    """
    This class is an abstract class for creating schedulers. Subclasses must
    override the `step()` method to specify its implementation.
    """

    __metaclass__ = ABCMeta

    def __init__(self):

        """ Create a new Scheduler instance.

        Args:
          None.

        Returns:
          Scheduler: A new Scheduler instance.

        """

        self._num_steps = 0
        self._current_time = 0.0

        self._modules = []


    def reset(self):
        """ Reset the scheduler.

        Args:
          None.

        Returns:
          None.
        
        """

        self._modules = []
        self._num_steps = 0
        self._current_time = 0

    def add_module(self, module):
        """ Add a module to the scheduler.

        Args:
          module (Module): a module to add.

        Returns:
          None.

        """

        self._modules.append(module)

    def update(self, ca):
        """ Update the scheduler for given cognitive architecture (ca)

        Args:
          ca (CognitiveArchitecture): a target to update.

        Returns:
          None.

        """

        self._modules = ca.get_all_sub_modules()

    @abstractmethod
    def step(self):
        """ Step over a single iteration

        Args:
          None.

        Returns:
          float: the current time of the scheduler

        """

        return

class VirtualTimeSyncScheduler(Scheduler):
    
    """
    VirtualTimeSyncScheduler is a Scheduler implementation for virutal time in
    a synced manner.
    """

    def __init__(self, interval=1.0):
        """ Create a new VirtualTimeSyncScheduler Instance.

        Args:
          interval (float): interval between each step

        Returns:
          VirtualTimeSyncScheduler: A new VirtualTimeSyncScheduler instance.

        """

        super(VirtualTimeSyncScheduler, self).__init__()
        self._interval = interval

    def step(self):
        """ Step by the internal interval.

        The methods `input()`, `fire()`, and `output()` are synchronously
        called and the time is incremented by the given interval for each
        step.

        Args:
          None.

        Returns:
          float: the current time of the scheduler

        """

        for module in self._modules:
            module.input(self._current_time)

        for module in self._modules:
            module.fire()

        self._current_time = self._current_time + self._interval

        for module in self._modules:
            module.output(self._current_time)

        self._num_steps = self._num_steps + 1

        return self._current_time

