package wba.rogue;

import java.util.Random;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class RNG {
    private Random rng;
    private boolean secure;
    private long seed;

    public RNG() {
        try {
            rng = SecureRandom.getInstance("SHA1PRNG");
        } catch(NoSuchAlgorithmException nsae) {
            System.err.println("Caught NoSuchAlgorithmException: using native Java random");
            long seed = System.currentTimeMillis();
            this.seed = seed;
            rng = new Random(seed);
        }
    }

    public RNG(long seed) {
        this.seed = seed;
        rng = new Random(seed);
    }

    public boolean nextBoolean() {
        return rng.nextBoolean();
    }

    public void nextBytes(byte[] bytes) {
        rng.nextBytes(bytes);
    }

    public double nextDouble() {
        return rng.nextDouble();
    }

    public float nextFloat() {
        return rng.nextFloat();
    }

    public double nextGaussian() {
        return rng.nextGaussian();
    }

    public int nextInt() {
        return rng.nextInt();
    }

    public int nextInt(int n) {
        return rng.nextInt(n);
    }

    public long nextLong() {
        return rng.nextLong();
    }

    public void setSeed(long seed) {
        if(!secure) {
            rng.setSeed(seed);
        }
    }

    public void newSeed(long seed) {
        if(!secure) {
            this.seed = seed;
            rng.setSeed(seed);
        }
    }

    public void reset() {
        if(!secure) {
            rng.setSeed(seed);
        }
    }
}
