package aoc2023;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day4 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day4.class);

        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day4.class)));
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day4.class);
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day4.class)));
    }

    public static String answerPart1(String[] prompt) {

        int sum = 0;
        for (String line : prompt) {
            // remove "Card x:" prefix
            line = line.split(": ")[1];
            // split into winning numbers and numbers you have
            String[] numbers = line.split(" \\| ");
            // split numbers
            Set<String> winningNumbers = new HashSet<>(Arrays.asList(numbers[0].split(" ")));
            winningNumbers.remove("");// purge possible empty string from leading single-digit value

            // split and purge for my numbers
            Set<String> myNumbers = new HashSet<>(Arrays.asList(numbers[1].split(" ")));
            myNumbers.remove(""); // purge possible empty string from leading single-digit value

            // keep only winning numbers
            myNumbers.retainAll(winningNumbers);
            int correctNumbers = myNumbers.size();
            if (correctNumbers == 0)
                continue;
            // card score is 1 for first match, double for each additional match
            sum += Math.pow(2, correctNumbers - 1);
        }

        return sum + "";
    }

    public static String answerPart2(String[] prompt) {

        int sum = 0;
        // get the amount of winning numbers as this determines how far we (might) have
        // to look ahead for bonus scratchers
        int[] bonusScratchers = new int[prompt[0].split(": ")[1].split(" \\| ")[0].substring(1).replace("  ", " ")
                .split(" ").length];
        int currentBonusIndex = 0;
        for (String line : prompt) {
            // remove "Card x:" prefix
            line = line.split(": ")[1];
            // split into winning numbers and numbers you have
            String[] numbers = line.split(" \\| ");
            // split numbers
            Set<String> winningNumbers = new HashSet<>(Arrays.asList(numbers[0].split(" ")));
            winningNumbers.remove("");// purge possible empty string from leading single-digit value

            // split and purge for my numbers
            Set<String> myNumbers = new HashSet<>(Arrays.asList(numbers[1].split(" ")));
            myNumbers.remove(""); // purge possible empty string from leading single-digit value
            // keep only winning numbers
            myNumbers.retainAll(winningNumbers);

            int correctNumbers = myNumbers.size();
            int howOftenThisScratcher = 1 + bonusScratchers[currentBonusIndex]; //we start with one and may get bonus
            //sum is now just how many scratchcards (including bonus) were used
            sum += howOftenThisScratcher;
            bonusScratchers[currentBonusIndex] = 0; //exhaust the bonus from this one

            //add all bonus scratchers (round-robin)
            for (; correctNumbers > 0; correctNumbers--) {
                bonusScratchers[(currentBonusIndex + correctNumbers) % bonusScratchers.length] += howOftenThisScratcher;
            }

            currentBonusIndex = (currentBonusIndex + 1) % bonusScratchers.length;
        }

        return sum + "";
    }

}