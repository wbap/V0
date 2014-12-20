package wba.rogue;

import java.io.*;

public class test { 
    final static int NUMCOLS = 80;
    final static int NUMLINES = 24;
    final static int MAXROOMS = 9;

    public static void main(String[] args) {
        final RNG rng = new RNG();
        Rogue rogue = new Rogue(new Coord(NUMCOLS, NUMLINES), MAXROOMS, rng);
        Avatar player = new Avatar(rogue);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s = "4";
        do {
            try {
                s = in.readLine();
            } catch(IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                int[] state;
                int direction = 4;

                if(s == "h") {
                	direction = 3;
                }

                if(s == "j") {
                	direction = 7;
                }
                
                if(s == "k") {
                	direction = 1;
                }
                
                if(s == "l") {
                	direction = 5;
                }
                
                state = player.move(direction);

                int i;

                for(i = 0; i < 80 * 24; ++i) {
                    System.err.print((char)state[i]);
                    if((i + 1) % 80 == 0) {
                        System.err.println("");
                    }
                }
                System.err.println("");
                System.err.println("Hunger:\t" + state[i++]);
                System.err.println("Gold:\t" + state[i++]);
                System.err.println("Key:\t" + state[i]);

                System.err.println(state.length);
            }
        } while(s != null);
    }
}
