package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
//Part 2 missing
public class Day10 {

    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day10.class);
        Meta.timerStart();
        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches. " + Meta.timerElapsed());
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        Meta.timerStart();
        System.out
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day10.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day10.class);
        Meta.timerStart();
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches. " + Meta.timerElapsed());
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        Meta.timerStart();
        System.out
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day10.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    public static String answerPart1(String[] prompt) {
        char[][] promptC = new char[prompt.length][];
        for (int i = 0; i < promptC.length; i++) {
            promptC[i] = prompt[i].toCharArray();
        }
        // There appear to be only exactly two connections to the
        int[] pos1Current, pos1Previous, pos2Current, pos2Previous;

        int xStart = 0, yStart = 0;
        // find S/start
        for (int y = 0; y < prompt.length; y++) {
            for (int x = 0; x < prompt[0].length(); x++) {
                if (promptC[y][x] == 'S') {
                    yStart = y;
                    xStart = x;
                    promptC[y][x] = 'X';
                }
            }
        }
        pos1Previous = new int[] { xStart, yStart };
        pos2Previous = new int[] { xStart, yStart };

        // find the two connected segments
        ArrayList<int[]> connected = new ArrayList<>();
        if (isConnectedToStart(promptC, xStart + 1, yStart, xStart, yStart)) {
            connected.add(new int[] { xStart + 1, yStart });
        }
        if (isConnectedToStart(promptC, xStart - 1, yStart, xStart, yStart)) {
            connected.add(new int[] { xStart - 1, yStart });
        }
        if (isConnectedToStart(promptC, xStart, yStart + 1, xStart, yStart)) {
            connected.add(new int[] { xStart, yStart + 1 });
        }
        if (isConnectedToStart(promptC, xStart, yStart - 1, xStart, yStart)) {
            connected.add(new int[] { xStart, yStart - 1 });
        }
        pos1Current = connected.get(0);
        pos2Current = connected.get(1);

        int distanceFromS = 1;
        // traverse pipe. if either segment was unexplored, it counts towards distance
        // (so it works for even and uneven total pipe length)
        boolean exploredNewSegment;
        do {
            exploredNewSegment = nextPipeSegmentUnexplored(promptC, pos1Current, pos1Previous);
            exploredNewSegment = nextPipeSegmentUnexplored(promptC, pos2Current, pos2Previous);

            if (exploredNewSegment) {
                distanceFromS++;
            }

        } while (exploredNewSegment);
        return distanceFromS + "";

    }

    /**
     * Returns true if next pipe segment has not been visited (marked with X)
     * xyPrevious receives the coordinates of xyCurrent, and xyCurrent receives
     * those of the new adjacent segment
     * 
     * @param prompt
     * @param xyCurrent
     * @param xyPrevious
     * @return
     */
    private static boolean nextPipeSegmentUnexplored(char[][] prompt, int[] xyCurrent, int[] xyPrevious) {
        if (prompt[xyCurrent[1]][xyCurrent[0]] == 'X') {
            return false;
        }
        Character c = getCharAt(prompt, xyCurrent[0], xyCurrent[1]);

        // look at which direction the pipe continues. In each case look at the shape of
        // the pipe segment and the direction of the previous segment
//i cant really say im happy with how these conditions worked out, but it appears to be working well enough
        // Go North
        if ((xyCurrent[1] < xyPrevious[1] && c == '|') || ((xyCurrent[1] == xyPrevious[1])
                && ((xyCurrent[0] < xyPrevious[0] && c == 'L') || (xyCurrent[0] > xyPrevious[0] && c == 'J')))) {
            prompt[xyCurrent[1]][xyCurrent[0]] = 'X';
            xyPrevious[0] = xyCurrent[0];
            xyPrevious[1] = xyCurrent[1];
            xyCurrent[1]--;

        } else
        // Go South
        if ((xyCurrent[1] > xyPrevious[1] && c == '|') || ((xyCurrent[1] == xyPrevious[1])
                && ((xyCurrent[0] < xyPrevious[0] && c == 'F') || (xyCurrent[0] > xyPrevious[0] && c == '7')))) {
            prompt[xyCurrent[1]][xyCurrent[0]] = 'X';
            xyPrevious[0] = xyCurrent[0];
            xyPrevious[1] = xyCurrent[1];
            xyCurrent[1]++;
        } else
        // Go West
        if ((xyCurrent[0] < xyPrevious[0] && c == '-') || ((xyCurrent[0] == xyPrevious[0])
                && ((xyCurrent[1] < xyPrevious[1] && c == '7') || (xyCurrent[1] > xyPrevious[1] && c == 'J')))) {
            prompt[xyCurrent[1]][xyCurrent[0]] = 'X';
            xyPrevious[0] = xyCurrent[0];
            xyPrevious[1] = xyCurrent[1];
            xyCurrent[0]--;
        } else
        // Go East
        if ((xyCurrent[0] > xyPrevious[0] && c == '-') || ((xyCurrent[0] == xyPrevious[0])
                && ((xyCurrent[1] < xyPrevious[1] && c == 'F') || (xyCurrent[1] > xyPrevious[1] && c == 'L')))) {
            prompt[xyCurrent[1]][xyCurrent[0]] = 'X';
            xyPrevious[0] = xyCurrent[0];
            xyPrevious[1] = xyCurrent[1];
            xyCurrent[0]++;
        } 

        return true;
    }

    private static boolean isConnectedToStart(char[][] prompt, int x, int y, int xStart, int yStart) {
        Character c = getCharAt(prompt, x, y);
        if (c == null) {
            return false;
        }
        if (x < xStart) {
            if (c == '-' || c == 'L' || c == 'F') {
                return true;
            }
        } else if (x > xStart) {
            if (c == '-' || c == 'J' || c == '7') {
                return true;
            }
        } else if (y < yStart) {
            if (c == '|' || c == '7' || c == 'F') {
                return true;
            }

        } else if (y > yStart) {
            if (c == '|' || c == 'J' || c == 'L') {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns char at specified location. Returns null on out of bounds
     * 
     * @param prompt
     * @param x
     * @param y
     * @return
     */
    private static Character getCharAt(char[][] prompt, int x, int y) {
        if (y < 0 || x < 0 || y >= prompt.length || x >= prompt[0].length) {
            return null;
        }
        return prompt[y][x];
    }

    /**
     * 
     * @param prompt
     * @return
     */
    public static String answerPart2(String[] prompt) {

        return "";

    }

}
