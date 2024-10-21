package aoc2023;
import java.io.IOException;

public class Day1 {
    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day1.class);

        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day1.class)));
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day1.class);
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day1.class)));
    }

    public static String answerPart1(String[] prompt) {
        int sum = 0;
        for (String s : prompt) {
            char[] chars = s.toCharArray();
            char firstDigit = 0;
            char secondDigit = 0;
            int i;
            // seek until first digit found (1st digit for calibration value)
            for (i = 0; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    firstDigit = chars[i];
                    break;
                }
            }
            // reverse-seek until first digit found (2nd digit for calibration value)
            // assumption: there are at least two digits in each line. thus the seek-range
            // can be limited, although this does not really have any advantage here
            for (int j = chars.length - 1; j >= i; j--) {
                if (Character.isDigit(chars[j])) {
                    secondDigit = chars[j];
                    break;
                }
            }

            // convert ascii digits to numerics
            sum += (firstDigit - '0') * 10 + secondDigit - '0';
        }
        return "" + sum;
    }

    public static String answerPart2(String[] prompt) {
        int sum = 0;
        for (String s : prompt) {
            char[] chars = s.toCharArray();
            int i;
            int[] firstOccurence = new int[10];
            firstOccurence[0] = chars.length - 1;
            int[] lastOccurance = new int[10];
            lastOccurance[0] = -1;
            // seek until first digit found (1st digit for calibration value)
            for (i = 0; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    firstOccurence[0] = i;
                    break;
                }
            }
            // reverse-seek until first digit found (2nd digit for calibration value)
            // assumption: there are at least two digits in each line. thus the seek-range
            // can be limited, although this does not really have any advantage here
            for (int j = chars.length - 1; j >= firstOccurence[0]; j--) {
                if (Character.isDigit(chars[j])) {
                    lastOccurance[0] = j;
                    break;
                }
            }
            // now try to find the textual digits
            // note the abscence of zero/0: does not appear in (my) puzzle input
            firstOccurence[1] = s.indexOf("one");
            firstOccurence[2] = s.indexOf("two");
            firstOccurence[3] = s.indexOf("three");
            firstOccurence[4] = s.indexOf("four");
            firstOccurence[5] = s.indexOf("five");
            firstOccurence[6] = s.indexOf("six");
            firstOccurence[7] = s.indexOf("seven");
            firstOccurence[8] = s.indexOf("eight");
            firstOccurence[9] = s.indexOf("nine");

            int firstDigitInString = 0; //gets the index into firstOccurence pointing to first digit in the string. if it stays 0, the digit is found at firstOccurance(0), otherwise it is firstI
            for (int j = 0; j < firstOccurence.length; j++) {
                //is there a digit before the one currently stored in firstDigitInString?
                if (firstOccurence[firstDigitInString] == -1 || (firstOccurence[j] > -1 && firstOccurence[j] < firstOccurence[firstDigitInString])) {
                    firstDigitInString = j;
                }
            }

            lastOccurance[1] = s.lastIndexOf("one");
            lastOccurance[2] = s.lastIndexOf("two");
            lastOccurance[3] = s.lastIndexOf("three");
            lastOccurance[4] = s.lastIndexOf("four");
            lastOccurance[5] = s.lastIndexOf("five");
            lastOccurance[6] = s.lastIndexOf("six");
            lastOccurance[7] = s.lastIndexOf("seven");
            lastOccurance[8] = s.lastIndexOf("eight");
            lastOccurance[9] = s.lastIndexOf("nine");
            int lastDigitInString = 0; // index in lastOccurance where the highest value is stored
            for (int j = 1; j < lastOccurance.length; j++) {
                //find last index
                if (lastOccurance[lastDigitInString] < lastOccurance[j]) {
                    lastDigitInString = j;
                }
            }

             // convert to calibration value. for 1-9 index==value, for 0 look up char in string at stored index
            sum += ((firstDigitInString == 0 ? chars[firstOccurence[firstDigitInString]] - '0' : firstDigitInString) * 10
                    + (lastDigitInString == 0 ? chars[lastOccurance[lastDigitInString]] - '0' : lastDigitInString));
        }
        return "" + sum;
    }

}