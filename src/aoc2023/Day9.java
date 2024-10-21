package aoc2023;

import java.io.IOException;
import java.util.ArrayList;

public class Day9 {
    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day9.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day9.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day9.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day9.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    private static int[] lineToIntArray(String line) {
        String[] sNumbers = line.split(" ");
        int[] numbers = new int[sNumbers.length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(sNumbers[i]);
        }
        return numbers;
    }

    public static String answerPart1(String[] prompt) {
        int sum = 0;
        for (String thisLine : prompt) {
            int[] numbers = lineToIntArray(thisLine);

            ArrayList<Integer> finalNumbers = new ArrayList<>(); // only for textual output

            boolean atLeastOneNotZero; // if any of the numbers this loop is not zero, we have to do another pass
            int end = numbers.length - 1;
            do {
                atLeastOneNotZero = false;
                // build deltas
                for (int i = 0; i < end; i++) {
                    numbers[i] = numbers[i + 1] - numbers[i];
                    atLeastOneNotZero |= numbers[i] != 0;
                }
                // per-line result can be built directly from the sum of the numbers at the end
                // of each imaginary line. no need to store it somewhere else
                sum += numbers[end];
                finalNumbers.add(numbers[end]);
                end--;
            } while (atLeastOneNotZero);
            // System.out.println("Finals:" + finalNumbers.toString());

        }

        return sum + "";

    }

    public static String answerPart2(String[] prompt) {
        // very similar to part1, but this time actually use the arraylist for something
        // useful (storing all the first elements. but MAYBE even that can be avoided?)

        int sum = 0;

        for (String thisLine : prompt) {
            int[] numbers = lineToIntArray(thisLine);

            ArrayList<Integer> finalNumbers = new ArrayList<>(); // only for textual output

            boolean atLeastOneNotZero; // if any of the numbers this loop is not zero, we have to do another pass
            int end = numbers.length - 1;
            do {
                finalNumbers.add(numbers[0]);
                atLeastOneNotZero = false;
                for (int i = 0; i < end; i++) {
                    numbers[i] = numbers[i + 1] - numbers[i];
                    atLeastOneNotZero |= numbers[i] != 0;
                }
                end--;
            } while (atLeastOneNotZero);
            // calculate the new left-most history value in val
            int val = finalNumbers.get(finalNumbers.size() - 1);
            for (int i = finalNumbers.size() - 2; i >= 0; i--) {
                //val is built from alternating between adding/substracting the finalNumbers
                val = (finalNumbers.get(i) - val);
            }
            sum += val;
            // System.out.println("Val:" + val);
            // System.out.println("Finals:" + finalNumbers.toString());
        }

        return sum + "";

    }

}
