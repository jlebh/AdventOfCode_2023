package aoc2023;
import java.io.IOException;

public class Day2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day2.class);

        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day2.class)));
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day2.class);
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day2.class)));
    }

    public static String answerPart1(String[] prompt) {
        int sum = 0;
        //Jumppoint for when a game is not possible. (method for the test code with simple bool return might have been wiser?)
        GameLoop: for (int i = 0; i < prompt.length; i++) {
            // throw away "Game x:" prefix, then seperate different draws

            String[] draws = prompt[i].split(": ")[1].split("; ");

            //valid games have max 12 reds, 13 greens, 14 blues
            int redMax = 12;
            int greenMax = 13;
            int blueMax = 14;
            for (String draw : draws) {
                String[] cubes = draw.split(", ");
                for (String cube : cubes) {
                    //split into color and number
                    String[] cubeData = cube.split(" ");
                    //if the draw exceeds the limits for the color, the entire game is failed
                    if ((cubeData[1].equals("blue") && Integer.parseInt(cubeData[0]) > blueMax)) {
                        continue GameLoop;
                    } else if ((cubeData[1].equals("green") && Integer.parseInt(cubeData[0]) > greenMax)) {
                        continue GameLoop;
                    } else if ((cubeData[1].equals("red")) && Integer.parseInt(cubeData[0]) > redMax) {
                        continue GameLoop;
                    }

                }
            }
            //all draws (=entire game) successful
            sum += i + 1;
        }
        return "" + sum;
    }

    public static String answerPart2(String[] prompt) {
        int sum = 0;
        for (int i = 0; i < prompt.length; i++) {
            // throw away "Game x:" prefix, then seperate different draws

            String[] draws = prompt[i].split(": ")[1].split("; ");

            //highest occurance of colors in a single draw this game
            int redMax = 0;
            int greenMax = 0;
            int blueMax = 0;
            for (String draw : draws) {
                String[] cubes = draw.split(", ");
                for (String cube : cubes) {
                    //split into color and number
                    String[] cubeData = cube.split(" ");
                    //keep highest draws for each color
                    if ((cubeData[1].equals("blue"))) {
                        blueMax = Math.max(blueMax, Integer.parseInt(cubeData[0]));
                    } else if ((cubeData[1].equals("green"))) {
                        greenMax = Math.max(greenMax, Integer.parseInt(cubeData[0]));
                    } else if ((cubeData[1].equals("red"))) {
                        redMax = Math.max(redMax, Integer.parseInt(cubeData[0]));
                    }

                }
            }
            //power of set is multiplication of the required cubes for each color
            sum += redMax * greenMax * blueMax;
        }
        return "" + sum;
    }

}