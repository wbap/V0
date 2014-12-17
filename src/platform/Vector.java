package platform;

public class Vector {
	
	static int MAX_VALUE = Short.MAX_VALUE;
	
	static float normalize(short v)
	{
		return (float)v / MAX_VALUE;
	}	
	
	static float[] normalize(short[] v)
	{
		float[] result = new float[v.length];
		for (int i = 0; i < v.length; i++)
		{
			result[i] = (float)v[i] / MAX_VALUE;
		}
		
		return result;
	}
	
						
}
