#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/..")

import numpy
import brica0

# Define an implementation of brica.CognitiveArchitecture.
# The `fire(self)` method must be implemented.
class TestCA(brica0.CognitiveArchitecture):
    def fire(self):
        return # Do nothing.

s = brica0.VirtualTimeSyncScheduler()
ca = TestCA(s)

# Here we will connect ConstantModule -> PipeModule -> NullModule
A = brica0.ConstantModule()
B = brica0.PipeModule()
C = brica0.NullModule()

zero = numpy.zeros(3, dtype=numpy.short)
v = numpy.array([1, 2, 3], dtype=numpy.short)

A.set_state("out1", v)

A.make_out_port("out1", 3)

B.make_out_port("out1", 3)
B.connect(A, "out1", "in1") # connection from A:out1 to B:in1
B.map_port("in1", "out1") # B:out1 is a simple reflection of B:in1.

C.connect(B, "out1", "in1") # connection from B:out1 to C:in1

ca.add_sub_module("A", A)
ca.add_sub_module("B", B)
ca.add_sub_module("C", C)

# initially everything is zero
print "Step 0:"
print "A (NullModule)     : out1  => ", A.get_out_port("out1")
print "B (PipeModule)     : in1   => ", B.get_in_port("in1")
print "B (PipeModule)     : out1  => ", B.get_out_port("out1")
print "C (ConstantModule) : in1   => ", C.get_in_port("in1")

# Step 1
ca.step()

print "Step 1:"
print "A (NullModule)     : out1  => ", A.get_out_port("out1")
print "B (PipeModule)     : in1   => ", B.get_in_port("in1")
print "B (PipeModule)     : out1  => ", B.get_out_port("out1")
print "C (ConstantModule) : in1   => ", C.get_in_port("in1")

# Step 2
ca.step()

print "Step 2:"
print "A (NullModule)     : out1  => ", A.get_out_port("out1")
print "B (PipeModule)     : in1   => ", B.get_in_port("in1")
print "B (PipeModule)     : out1  => ", B.get_out_port("out1")
print "C (ConstantModule) : in1   => ", C.get_in_port("in1")

# Step 3
ca.step()

print "Step 3:"
print "A (NullModule)     : out1  => ", A.get_out_port("out1")
print "B (PipeModule)     : in1   => ", B.get_in_port("in1")
print "B (PipeModule)     : out1  => ", B.get_out_port("out1")
print "C (ConstantModule) : in1   => ", C.get_in_port("in1")
