import java.io.*;

public class test { 
    public static void main(String[] args) { 
        Avatar player = new Avatar();
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
                switch(s) {
                case "h":
                    direction = 3;
                    break;
                case "j":
                    direction = 7;
                    break;
                case "k":
                    direction = 1;
                    break;
                case "l":
                    direction = 5;
                    break;
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
