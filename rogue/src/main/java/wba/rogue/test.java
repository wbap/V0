package wba.rogue;

import java.io.*;

import wba.citta.gsa.Goal;

public class test { 
    final static int NUMCOLS = 80;
    final static int NUMLINES = 24;
    final static int MAXROOMS = 9;

    public static void main(String[] args) {
        final RNG rng = new RNG(1);
        Rogue rogue = new Rogue(new Coord(NUMCOLS, NUMLINES), MAXROOMS, rng, true);
        Avatar player = new Avatar(rogue);

        Goal goal = player.getVisibleGoal();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s = "4";
        do {
            try {
                s = in.readLine();
            } catch(IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                char[] charArray = s.toCharArray();

                Goal state;
                int direction = 4;

                if(charArray.length > 0) {
                    char c = charArray[0];

                    if(c == 'h') {
                        direction = 3;
                    }

                    if(c == 'j') {
                        direction = 7;
                    }
                
                    if(c == 'k') {
                        direction = 1;
                    }
                
                    if(c == 'l') {
                        direction = 5;
                    }
                }

                state = player.move(direction);

                printState(goal);
                printState(state);

                if(player.checkGoal()) {
                    System.err.println("You win!");
                    player.restart();
                }
            }
        } while(s != null);
    }

    public static void printState(Goal state) {
        int i;
        for(i = 0; i < 80 * 24; ++i) {
            System.err.print((char)(int)state.get(i));
            if((i + 1) % 80 == 0) {
                System.err.println("");
            }
        }
        System.err.println("");
    }
}
