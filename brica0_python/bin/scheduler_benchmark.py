#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/..")

import numpy
import brica0

from benchmarker import Benchmarker

class TestCA(brica0.CognitiveArchitecture):
    def fire(self):
        return

def setup1(scheduler):
    ca = TestCA(scheduler)
    m = brica0.NullModule()
    ca.add_sub_module("M1", m)
    return ca

def setup2(scheduler, data_length):
    ca = TestCA(scheduler)
    M1 = brica0.ConstantModule()
    M2 = brica0.NullModule()

    v = numpy.zeros(data_length, dtype=numpy.short)

    M1.set_state("out1", v)

    M1.make_out_port("out1", 3)

    M2.connect(M1, "out1", "in1")

    ca.add_sub_module("M1", M1)
    ca.add_sub_module("M2", M2)

    return ca

if __name__ == '__main__':
    scheduler = brica0.VirtualTimeSyncScheduler(1.0)

    with Benchmarker(1000 * 1000, width=20, cycle=5) as bench:
        @bench("Setup1")
        def _(bm):
            ca = setup1(scheduler)
            for i in bm:
                ca.step()

        @bench("Setup2")
        def _(bm):
            ca = setup2(scheduler, 1)
            for i in bm:
                ca.step()

        @bench("Setup2 (1kB)")
        def _(bm):
            ca = setup2(scheduler, 500)
            for i in bm:
                ca.step()

        @bench("Setup2 (10kB)")
        def _(bm):
            ca = setup2(scheduler, 5000)
            for i in bm:
                ca.step()
