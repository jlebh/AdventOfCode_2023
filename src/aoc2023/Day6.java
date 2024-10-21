package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Day6 {
    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day6.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day6.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day6.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day6.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    public static String answerPart1(String[] prompt) {
        ArrayList<Integer> listofTimeToBeat = new ArrayList<>();

        Scanner scanner = new Scanner(prompt[0].split(":")[1]);
        while (scanner.hasNextInt()) {
            listofTimeToBeat.add(scanner.nextInt());
        }
        scanner.close();
        scanner = new Scanner(prompt[1].split(":")[1]);
        // read distances, make
        int[] distancesToBeat = new int[listofTimeToBeat.size()];
        for (int i = 0; i < distancesToBeat.length; i++) {
            distancesToBeat[i] = scanner.nextInt();
        }
        scanner.close();

        int result = 1; //result is multiplied, so init with 1
        //loop over races
        for (int i = 0; i < distancesToBeat.length; i++) {
            int distanceToBeat = distancesToBeat[i];
            int time = listofTimeToBeat.get(i);
            int windupTime = 0;
            //find the shortest windup time that allows to win
            for (; windupTime * (time - windupTime) <= distanceToBeat; windupTime++)
                ;

            //System.out.println(windupTime + " " + ((time - 2 * (windupTime - 1)) - 1) + "");
            // result is the multiplied margin of errors. margin of error = number of ways
            // to beat each record
            result *= ((time - 2 * (windupTime - 1)) - 1);
        }

        return result + "";

    }

    public static String answerPart2(String[] prompt) {
        long time = Long.parseLong(prompt[0].split(":")[1].replace(" ", ""));
        long distanceToBeat = Long.parseLong(prompt[1].split(":")[1].replace(" ", ""));

        long windupTime = 0;
        //a direct mathematic approach is propably better
        for (; windupTime * (time - windupTime) <= distanceToBeat; windupTime++) {
        }

        System.out.println(windupTime + " " + ((time - 2 * (windupTime - 1)) - 1) + "");

        return ((time - 2 * (windupTime - 1)) - 1) + "";

    }

}
