# -*- coding: utf-8 -*-

import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/..")

import unittest
import numpy
import brica0

class TestVirtualTimeSyncScheduler(unittest.TestCase):
    def setUp(self):
        class TestCA(brica0.CognitiveArchitecture):
            def fire(step):
                return

        s = brica0.VirtualTimeSyncScheduler(1.0)
        self.ca = TestCA(s)

    def test_constant_module(self):
        ca = self.ca

        cm = brica0.ConstantModule()
        cm.make_out_port("out1", 3)

        setout = numpy.ndarray([0, 1, 2], dtype=numpy.short)

        cm.set_state("out1", setout)
        ca.add_sub_module("M1", cm)

        t = ca.step()
        self.assertEqual(1.0,  t)

        mm = ca.get_sub_module("M1")
        self.assertIs(mm, cm)

        setout2 = numpy.ndarray([3, 4, 5], dtype=numpy.short)
        mm.set_state("out1", setout2)

        self.assertTrue((setout == mm.get_out_port("out1")).all)

        ca.step()
        ca.step()

        self.assertTrue((setout2 == mm.get_out_port("out1")).all)

    def test_const_pipe_null_module(self):
        ca = self.ca

        A = brica0.ConstantModule()
        B = brica0.PipeModule()
        C = brica0.NullModule()

        zero = numpy.zeros(3, dtype=numpy.short)
        v = numpy.array([1, 2, 3], dtype=numpy.short)

        A.set_state("out1", v)

        self.assertTrue((A.get_state("out1") == v).all())
        self.assertIsNot(A.get_state("out1"), v) # ensure that v is cloned.

        A.make_out_port("out1", 3)

        B.make_out_port("out1", 3)
        B.connect(A, "out1", "in1") # connection from A:out1 to B:in1
        B.map_port("in1", "out1") # B:out1 is a simple reflection of B:in1.

        C.connect(B, "out1", "in1") # connection from B:out1 to C:in1

        ca.add_sub_module("A", A)
        ca.add_sub_module("B", B)
        ca.add_sub_module("C", C)

        # initially everything is zero
        self.assertTrue((zero == A.get_out_port("out1")).all())
        self.assertTrue((zero == B.get_in_port("in1")).all())
        self.assertTrue((zero == B.get_out_port("out1")).all())
        self.assertTrue((zero == C.get_in_port("in1")).all())

        # Step 1
        ca.step()

        self.assertTrue((v == A.get_out_port("out1")).all())
        self.assertTrue((zero == B.get_in_port("in1")).all())
        self.assertTrue((zero == B.get_out_port("out1")).all())
        self.assertTrue((zero == C.get_in_port("in1")).all())

        # Step 2
        ca.step()

        self.assertTrue((v == A.get_out_port("out1")).all())
        self.assertTrue((v == B.get_in_port("in1")).all())
        self.assertTrue((v == B.get_out_port("out1")).all())
        self.assertTrue((zero == C.get_in_port("in1")).all())

        # Step 3
        ca.step()

        self.assertTrue((v == A.get_out_port("out1")).all())
        self.assertTrue((v == B.get_in_port("in1")).all())
        self.assertTrue((v == B.get_out_port("out1")).all())
        self.assertTrue((v == C.get_in_port("in1")).all())

if __name__ == '__main__':
    test_classes = [TestVirtualTimeSyncScheduler]

    suites_list = []

    loader = unittest.TestLoader()

    for test_class in test_classes:
        suite = loader.loadTestsFromTestCase(test_class)
        suites_list.append(suite)

    all_suite = unittest.TestSuite(suites_list)

    runner = unittest.TextTestRunner(verbosity=2)
    results = runner.run(all_suite)

