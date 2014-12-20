package wba.brica;

import static org.junit.Assert.*;

import org.junit.Test;

import platform.Vector;

public class VectorTest {

	@Test
	public void test1() {
		short v = 0;
		float res = Vector.normalize(v);
		assertEquals("normalize(0) != 0.0", 0.0f, res, 1e-18f);
	}

	@Test
	public void test2() {
		short v = Short.MAX_VALUE;
		float res = Vector.normalize(v);
		assertEquals("normalize(MAX_VALUE) != 0.0", 1.0f, res, 1e-18f);
	}

	@Test
	public void test3() {
		short v = Short.MAX_VALUE / 2;
		float res = Vector.normalize(v);
		assertEquals("normalize(MAX_VALUE/2)", (float)(Short.MAX_VALUE / 2) / (float)Short.MAX_VALUE, res, 1e-6f);
	}

	@Test
	public void test4() {
		short[] v = {0, Short.MAX_VALUE, Short.MAX_VALUE / 2};
		float[] res = Vector.normalize(v);
		assertEquals("normalize(array): input and output array length mismatch", v.length, res.length);
		assertTrue("normalize(array) wrong result", res[0] == 0.0f && res[1] == 1.0f && res[2] - 0.5f < 1e-7f);
	}

	
}
