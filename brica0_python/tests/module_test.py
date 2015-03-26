# -*- coding: utf-8 -*-

import sys, os

sys.path.append(os.path.dirname(os.path.abspath(__file__)) + "/..")

import unittest
import numpy
import brica0

def callback(test_case):
    return lambda module: lambda caller: test_case.assertIs(caller, module)

class TestNullModule(unittest.TestCase):
    def setUp(self):
        self.module = brica0.NullModule()

    def test_in_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_in_port("test", 5)
        self.assertTrue((self.module.get_in_port("test") == v).all())
        self.module.remove_in_port("test")

    def test_out_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_out_port("test", 5)
        self.assertTrue((self.module.get_out_port("test") == v).all())
        self.module.remove_out_port("test")

    def test_state(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_state("test", v)
        self.assertTrue((self.module.get_state("test") == v).all())

    def test_result(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_result("test", v)
        self.assertTrue((self.module.get_result("test") == v).all())

    def test_connect(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        null.make_out_port("null_out", 10)
        pipe.make_out_port("pipe_out", 10)
        constant.make_out_port("constant_out", 10)
        null_c = brica0.Connection(null, "null_out", "null_in")
        pipe_c = brica0.Connection(pipe, "pipe_out", "pipe_in")
        constant_c = brica0.Connection(constant, "constant_out", "constant_in")
        self.module.connect(null, "null_out", "null_in")
        self.module.connect(pipe, "pipe_out", "pipe_in")
        self.module.connect(constant, "constant_out", "constant_in")
        self_c = self.module._connections[0]
        self.assertEqual(null_c.to_string(), self_c.to_string())
        self_c = self.module._connections[1]
        self.assertEqual(pipe_c.to_string(), self_c.to_string())
        self_c = self.module._connections[2]
        self.assertEqual(constant_c.to_string(), self_c.to_string())

    def test_add_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        self.assertIs(null, self.module.get_sub_module("null"))
        self.module.add_sub_module("pipe", pipe)
        self.assertIs(pipe, self.module.get_sub_module("pipe"))
        self.module.add_sub_module("constant", constant)
        self.assertIs(constant, self.module.get_sub_module("constant"))

    def test_get_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        null.add_sub_module("pipe", pipe)
        pipe.add_sub_module("constant", constant)
        self.assertIs(self.module.get_sub_module("null"), null)
        self.assertIs(self.module.get_sub_module("null.pipe"), pipe)
        self.assertIs(self.module.get_sub_module("null.pipe.constant"), constant)
        self.assertIs(null.get_sub_module("pipe"), pipe)
        self.assertIs(null.get_sub_module("pipe.constant"), constant)
        self.assertIs(pipe.get_sub_module("constant"), constant)
        list = self.module.get_all_sub_modules()
        self.assertTrue(null in list)
        self.assertTrue(pipe in list)
        self.assertTrue(constant in list)

    def test_callback(self):
        cb = callback(self)(self.module)
        self.module.register_input_callback(cb)
        self.module.input(1.0)
        self.module.register_output_callback(cb)
        self.module.output(1.0)

class TestPipeModule(unittest.TestCase):
    def setUp(self):
        self.module = brica0.PipeModule()

    def test_in_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_in_port("test", 5)
        self.assertTrue((self.module.get_in_port("test") == v).all())
        self.module.remove_in_port("test")

    def test_out_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_out_port("test", 5)
        self.assertTrue((self.module.get_out_port("test") == v).all())
        self.module.remove_out_port("test")

    def test_state(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_state("test", v)
        self.assertTrue((self.module.get_state("test") == v).all())

    def test_result(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_result("test", v)
        self.assertTrue((self.module.get_result("test") == v).all())

    def test_connect(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        null.make_out_port("null_out", 10)
        pipe.make_out_port("pipe_out", 10)
        constant.make_out_port("constant_out", 10)
        null_c = brica0.Connection(null, "null_out", "null_in")
        pipe_c = brica0.Connection(pipe, "pipe_out", "pipe_in")
        constant_c = brica0.Connection(constant, "constant_out", "constant_in")
        self.module.connect(null, "null_out", "null_in")
        self.module.connect(pipe, "pipe_out", "pipe_in")
        self.module.connect(constant, "constant_out", "constant_in")
        self_c = self.module._connections[0]
        self.assertEqual(null_c.to_string(), self_c.to_string())
        self_c = self.module._connections[1]
        self.assertEqual(pipe_c.to_string(), self_c.to_string())
        self_c = self.module._connections[2]
        self.assertEqual(constant_c.to_string(), self_c.to_string())

    def test_add_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        self.assertIs(null, self.module.get_sub_module("null"))
        self.module.add_sub_module("pipe", pipe)
        self.assertIs(pipe, self.module.get_sub_module("pipe"))
        self.module.add_sub_module("constant", constant)
        self.assertIs(constant, self.module.get_sub_module("constant"))

    def test_get_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        null.add_sub_module("pipe", pipe)
        pipe.add_sub_module("constant", constant)
        self.assertIs(self.module.get_sub_module("null"), null)
        self.assertIs(self.module.get_sub_module("null.pipe"), pipe)
        self.assertIs(self.module.get_sub_module("null.pipe.constant"), constant)
        self.assertIs(null.get_sub_module("pipe"), pipe)
        self.assertIs(null.get_sub_module("pipe.constant"), constant)
        self.assertIs(pipe.get_sub_module("constant"), constant)
        list = self.module.get_all_sub_modules()
        self.assertTrue(null in list)
        self.assertTrue(pipe in list)
        self.assertTrue(constant in list)

    def test_callback(self):
        cb = callback(self)(self.module)
        self.module.register_input_callback(cb)
        self.module.input(1.0)
        self.module.register_output_callback(cb)
        self.module.output(1.0)

class TestConstantModule(unittest.TestCase):
    def setUp(self):
        self.module = brica0.ConstantModule()

    def test_in_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_in_port("test", 5)
        self.assertTrue((self.module.get_in_port("test") == v).all())
        self.module.remove_in_port("test")

    def test_out_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_out_port("test", 5)
        self.assertTrue((self.module.get_out_port("test") == v).all())
        self.module.remove_out_port("test")

    def test_state(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_state("test", v)
        self.assertTrue((self.module.get_state("test") == v).all())

    def test_result(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_result("test", v)
        self.assertTrue((self.module.get_result("test") == v).all())

    def test_connect(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        null.make_out_port("null_out", 10)
        pipe.make_out_port("pipe_out", 10)
        constant.make_out_port("constant_out", 10)
        null_c = brica0.Connection(null, "null_out", "null_in")
        pipe_c = brica0.Connection(pipe, "pipe_out", "pipe_in")
        constant_c = brica0.Connection(constant, "constant_out", "constant_in")
        self.module.connect(null, "null_out", "null_in")
        self.module.connect(pipe, "pipe_out", "pipe_in")
        self.module.connect(constant, "constant_out", "constant_in")
        self_c = self.module._connections[0]
        self.assertEqual(null_c.to_string(), self_c.to_string())
        self_c = self.module._connections[1]
        self.assertEqual(pipe_c.to_string(), self_c.to_string())
        self_c = self.module._connections[2]
        self.assertEqual(constant_c.to_string(), self_c.to_string())

    def test_add_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        self.assertIs(null, self.module.get_sub_module("null"))
        self.module.add_sub_module("pipe", pipe)
        self.assertIs(pipe, self.module.get_sub_module("pipe"))
        self.module.add_sub_module("constant", constant)
        self.assertIs(constant, self.module.get_sub_module("constant"))

    def test_get_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        null.add_sub_module("pipe", pipe)
        pipe.add_sub_module("constant", constant)
        self.assertIs(self.module.get_sub_module("null"), null)
        self.assertIs(self.module.get_sub_module("null.pipe"), pipe)
        self.assertIs(self.module.get_sub_module("null.pipe.constant"), constant)
        self.assertIs(null.get_sub_module("pipe"), pipe)
        self.assertIs(null.get_sub_module("pipe.constant"), constant)
        self.assertIs(pipe.get_sub_module("constant"), constant)
        list = self.module.get_all_sub_modules()
        self.assertTrue(null in list)
        self.assertTrue(pipe in list)
        self.assertTrue(constant in list)

    def test_callback(self):
        cb = callback(self)(self.module)
        self.module.register_input_callback(cb)
        self.module.input(1.0)
        self.module.register_output_callback(cb)
        self.module.output(1.0)

class TestCognitiveArchitecture(unittest.TestCase):
    def setUp(self):
        class TestCA(brica0.CognitiveArchitecture):
            def fire(self):
                return
            
        self.scheduler = brica0.VirtualTimeSyncScheduler()
        self.module = TestCA(self.scheduler)

    def test_in_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_in_port("test", 5)
        self.assertTrue((self.module.get_in_port("test") == v).all())
        self.module.remove_in_port("test")

    def test_out_port(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.make_out_port("test", 5)
        self.assertTrue((self.module.get_out_port("test") == v).all())
        self.module.remove_out_port("test")

    def test_state(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_state("test", v)
        self.assertTrue((self.module.get_state("test") == v).all())

    def test_result(self):
        v = numpy.zeros(5, dtype=numpy.short)
        self.module.set_result("test", v)
        self.assertTrue((self.module.get_result("test") == v).all())

    def test_connect(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        null.make_out_port("null_out", 10)
        pipe.make_out_port("pipe_out", 10)
        constant.make_out_port("constant_out", 10)
        null_c = brica0.Connection(null, "null_out", "null_in")
        pipe_c = brica0.Connection(pipe, "pipe_out", "pipe_in")
        constant_c = brica0.Connection(constant, "constant_out", "constant_in")
        self.module.connect(null, "null_out", "null_in")
        self.module.connect(pipe, "pipe_out", "pipe_in")
        self.module.connect(constant, "constant_out", "constant_in")
        self_c = self.module._connections[0]
        self.assertEqual(null_c.to_string(), self_c.to_string())
        self_c = self.module._connections[1]
        self.assertEqual(pipe_c.to_string(), self_c.to_string())
        self_c = self.module._connections[2]
        self.assertEqual(constant_c.to_string(), self_c.to_string())

    def test_add_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        self.assertIs(null, self.module.get_sub_module("null"))
        self.module.add_sub_module("pipe", pipe)
        self.assertIs(pipe, self.module.get_sub_module("pipe"))
        self.module.add_sub_module("constant", constant)
        self.assertIs(constant, self.module.get_sub_module("constant"))

    def test_get_sub_module(self):
        null = brica0.NullModule()
        pipe = brica0.PipeModule()
        constant = brica0.ConstantModule()
        self.module.add_sub_module("null", null)
        null.add_sub_module("pipe", pipe)
        pipe.add_sub_module("constant", constant)
        self.assertIs(self.module.get_sub_module("null"), null)
        self.assertIs(self.module.get_sub_module("null.pipe"), pipe)
        self.assertIs(self.module.get_sub_module("null.pipe.constant"), constant)
        self.assertIs(null.get_sub_module("pipe"), pipe)
        self.assertIs(null.get_sub_module("pipe.constant"), constant)
        self.assertIs(pipe.get_sub_module("constant"), constant)
        list = self.module.get_all_sub_modules()
        self.assertTrue(null in list)
        self.assertTrue(pipe in list)
        self.assertTrue(constant in list)

    def test_callback(self):
        cb = callback(self)(self.module)
        self.module.register_input_callback(cb)
        self.module.input(1.0)
        self.module.register_output_callback(cb)
        self.module.output(1.0)

if __name__ == '__main__':
    test_classes = [TestNullModule, TestPipeModule, TestConstantModule, TestCognitiveArchitecture]

    suites_list = []

    loader = unittest.TestLoader()

    for test_class in test_classes:
        suite = loader.loadTestsFromTestCase(test_class)
        suites_list.append(suite)

    all_suite = unittest.TestSuite(suites_list)

    runner = unittest.TextTestRunner(verbosity=2)
    results = runner.run(all_suite)
