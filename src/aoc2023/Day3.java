package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Day3 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day3.class);

        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day3.class)));
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day3.class);
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day3.class)));
    }

    public static String answerPart1(String[] prompt) {
        char[][] ccs = new char[prompt.length][];
        for (int i = 0; i < ccs.length; i++) {
            ccs[i] = prompt[i].toCharArray();
        }
        // result is just the sum of all numbers adjacent to non-'.'-symbol
        int sum = 0;
        for (int i = 0; i < ccs.length; i++) {
            for (int j = 0; j < ccs[i].length; j++) {
                // fast forward to next digit
                if (!Character.isDigit(ccs[i][j])) {
                    continue;
                }
                // start of a number was found
                int jStop = j;
                // find end of number
                while (jStop + 1 < ccs[i].length && Character.isDigit(ccs[i][jStop + 1])) {
                    jStop++;
                }
                int number = Integer.parseInt(prompt[i].substring(j, jStop + 1));

                // is a symbol adjacent?
                if (/* check for symbol on this line (before or after number) */(j > 1 && isSymbol(ccs[i][j - 1]))
                        || (jStop < ccs[i].length - 1 && isSymbol(ccs[i][jStop + 1])) ||
                        (/* check row above if exists */i > 0
                                && isSymbol(ccs[i - 1], j - 1, jStop + 1))/* check row below if exists */
                        || (i < (ccs.length - 1) && isSymbol(ccs[i + 1], j - 1, jStop + 1))) {
                    sum += number;
                } else {
                    // System.out.println(number+" not a part");
                }

                j = jStop; // skip loop to end of number
            }

        }
        return sum + "";

    }

    public static boolean isSymbol(char c) {
        // assuming that digits in other rows dont count as symbols
        return !Character.isDigit(c) && !(c == '.');
    }

    /**
     * Returns true if any of the chars within start and stop (inclusive) is a
     * symbol
     * 
     * @param c
     * @param start
     * @param stop
     * @return
     */
    public static boolean isSymbol(char[] c, int start, int stop) {
        // bounds fixing just in case
        start = start < 0 ? 0 : start;
        stop = stop >= c.length ? c.length - 1 : stop;
        // seek until found or range ended
        for (; start <= stop; start++) {
            if (isSymbol(c[start])) {
                return true;
            }
        }
        return false;
    }

    public static String answerPart2(String[] prompt) {
        char[][] ccs = new char[prompt.length][];
        for (int i = 0; i < ccs.length; i++) {
            ccs[i] = prompt[i].toCharArray();
        }
        HashMap<String, ArrayList<Integer>> map = new HashMap<>(); // store x,y in string form as key, and found numbers
                                                                   // in a list
        int sum = 0;
        for (int i = 0; i < ccs.length; i++) {
            for (int j = 0; j < ccs[i].length; j++) {
                // fast forward to next digit
                if (!Character.isDigit(ccs[i][j])) {
                    continue;
                }
                int jStop = j;
                // find end of number
                while (jStop + 1 < ccs[i].length && Character.isDigit(ccs[i][jStop + 1])) {
                    jStop++;
                }
                int number = Integer.parseInt(prompt[i].substring(j, jStop + 1));
                ArrayList<String> foundgears = new ArrayList<>();
                /* check for symbol on this line */
                if (j > 1)
                    foundgears.add(gearAtLocation(ccs, i, j - 1));
                if (jStop < ccs[i].length - 1)
                    foundgears.add(gearAtLocation(ccs, i, jStop + 1));
                /* check row above if exists */
                if (i > 0)
                    foundgears.addAll(gearsInRange(ccs, i - 1, j - 1, jStop + 1));
                /* check row below if exists */
                if (i < (ccs.length - 1))
                    foundgears.addAll(gearsInRange(ccs, i + 1, j - 1, jStop + 1));

                for (String gear : foundgears) {
                    if (gear == null)
                        continue;
                    // check if mapping already exists.
                    ArrayList<Integer> list = map.get(gear);
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(gear, list);
                    }
                    list.add(number);
                }
                j = jStop;
            }

        }
        for (String key : map.keySet()) {
            ArrayList<Integer> ints = map.get(key);
            System.out.println(key + ":" + ints.toString());
            // only gears with (at least) two adjacent numbers are of interest
            if (ints.size() == 2) {
                int num = 1;
                for (int i : ints) { //incase size could be >2
                    num *= i;
                }
                sum += num;
            }
            if (ints.size() > 2) { // didnt occure
                // System.out.println(ints.toString());
            }
        }
        return sum + "";

    }

    /**
     * Returns a string containing x,y if char is a '*', null otherwise
     * 
     * @param c
     * @param x
     * @param y
     * @return
     */
    public static String gearAtLocation(char[][] c, int x, int y) {
        if (c[x][y] == '*') {
            return x + "," + y;
        }
        return null;
    }

    /**
     * Returns a list of x,y coordinates of each '*'found within start and stop
     * (inclusive)
     * 
     * @param c
     * @param start
     * @param stop
     * @return
     */
    public static ArrayList<String> gearsInRange(char[][] c, int x, int start, int stop) {
        ArrayList<String> list = new ArrayList<>();
        // bounds fixing just in case
        start = start < 0 ? 0 : start;
        stop = stop >= c.length ? c.length - 1 : stop;
        // seek entire range
        for (; start <= stop; start++) {
            String s = gearAtLocation(c, x, start);
            if (s != null) {
                list.add(s);
            }
        }
        return list;
    }
}
