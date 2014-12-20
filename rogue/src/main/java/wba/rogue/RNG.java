package wba.rogue;

import java.util.Random;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class RNG {
    private Random rng;
    private boolean secure;

    RNG() {
        try {
            rng = SecureRandom.getInstance("SHA1PRNG");
        } catch(NoSuchAlgorithmException nsae) {
            System.err.println("Caught NoSuchAlgorithmException: using native Java random");
            long seed = System.currentTimeMillis();
            rng = new Random(seed);
        }
    }

    boolean nextBoolean() {
        return rng.nextBoolean();
    }

    void nextBytes(byte[] bytes) {
        rng.nextBytes(bytes);
    }

    double nextDouble() {
        return rng.nextDouble();
    }

    float nextFloat() {
        return rng.nextFloat();
    }

    double nextGaussian() {
        return rng.nextGaussian();
    }

    int nextInt() {
        return rng.nextInt();
    }

    int nextInt(int n) {
        return rng.nextInt(n);
    }

    long nextLong() {
        return rng.nextLong();
    }

    void setSeed(long seed) {
        if(!secure) {
            rng.setSeed(seed);
        }
    }
}
