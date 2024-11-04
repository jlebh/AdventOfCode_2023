package aoc2023;

import java.io.IOException;

public class Day11 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day11.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day11.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day11.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day11.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    public static String answerPart1(String[] prompt) {
        // empty rows/columns expand to 2 empty r/c
        return calculate(prompt, 2) + "";
    }

    public static String answerPart2(String[] prompt) {
        // empty rows/columns expand to 1000000 empty r/c
        return calculate(prompt, 1000000) + "";

    }

    static private long calculate(String[] prompt, int columnSpacing) {
        long sum = 0; // answer is sum of distances between galaxies
        int[] galaxiesEachRow = new int[prompt.length];
        int[] galaxiesEachColumn = new int[prompt.length]; // the input is square
        // count per-row and per-column galaxies to get which are empty and have to be
        // expanded
        for (int i = 0; i < galaxiesEachRow.length; i++) {
            char[] line = prompt[i].toCharArray();
            for (int j = 0; j < galaxiesEachColumn.length; j++) {
                galaxiesEachRow[i] += (1 - (line[j] / '.'));
                galaxiesEachColumn[j] += (1 - (line[j] / '.'));
            }
        }

        // offset is calculated by sum of empty galaxy row/column respectively
        int[] offsetsRow = new int[prompt.length];
        int[] offsetsColumn = new int[prompt.length];
        int galaxiesCount = 0;
        // offset this and all following rows|columns each time by 1 for each empty
        // count
        for (int i = 0; i < galaxiesEachRow.length; i++) {
            galaxiesCount += galaxiesEachRow[i];
            if (galaxiesEachRow[i] == 0) {
                for (int j = i; j < offsetsRow.length; j++) {
                    offsetsRow[j]++;
                }
            }
        }
        for (int i = 0; i < galaxiesEachColumn.length; i++) {
            if (galaxiesEachColumn[i] == 0) {
                for (int j = i; j < offsetsColumn.length; j++) {
                    offsetsColumn[j]++;
                }
            }
        }

        int[] galaxyCoordinates = new int[galaxiesCount]; // store all galaxies so we dont have to traverse the entire
                                                          // prompt array multiple times
        int galaxyI = 0;
        // for storing the x,y coordinate in the same int
        final int multiplier = prompt.length * prompt.length; // this time prompt.length is enough because shift isnt
                                                              // added in
        // galaxyCoordinates
        for (int i = 0; i < galaxiesEachRow.length; i++) {
            char[] line = prompt[i].toCharArray();
            for (int j = 0; j < galaxiesEachColumn.length; j++) {
                if (line[j] == '#') {
                    galaxyCoordinates[galaxyI] = i * multiplier + j;
                    galaxyI++;
                }
            }
        }
        @SuppressWarnings("unused")
        int combos = 0;
        final long DISTANCEMULTIPLIER = columnSpacing - 1; // need to substract one because we already get a +1 from the
        // row/column itself
        for (int i = 0; i < galaxyCoordinates.length; i++) {
            int galaxyA_x = galaxyCoordinates[i] / multiplier;
            int galaxyA_y = galaxyCoordinates[i] % multiplier;
            // System.out.println(
            // galaxyA_x + "," + galaxyA_y + " is at " + (DISTANCEMULTIPLIER *
            // offsetsRow[galaxyA_x] + galaxyA_x)
            // + "," + (DISTANCEMULTIPLIER * offsetsRow[galaxyA_y] + galaxyA_y));
            for (int j = i + 1; j < galaxyCoordinates.length; j++) {
                int galaxyB_x = galaxyCoordinates[j] / multiplier;
                int galaxyB_y = galaxyCoordinates[j] % multiplier;
                // the new galaxy coordinates are calculated here for the first time
                sum += Math
                        .abs((DISTANCEMULTIPLIER * offsetsRow[galaxyA_x] + galaxyA_x)
                                - (DISTANCEMULTIPLIER * offsetsRow[galaxyB_x] + galaxyB_x))
                        + Math.abs((DISTANCEMULTIPLIER * offsetsColumn[galaxyA_y] + galaxyA_y)
                                - (DISTANCEMULTIPLIER * offsetsColumn[galaxyB_y] + galaxyB_y));

            }
        }

        return sum;
    }

}
