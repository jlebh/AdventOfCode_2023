package aoc2023;
import java.io.IOException;

//Day Template
//TODO: Reduce references to this class to reduce risk of mistakes when renaming
public class DayXX {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(DayXX.class);

        String answer1 = answerPart1(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 1: " + answerPart1(Meta.readPrompt(DayXX.class)));
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(DayXX.class);
        String answer2 = answerPart2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        System.out.println("Answer for Part 2: " + answerPart2(Meta.readPrompt(DayXX.class)));
    }

    /**
     * 
     * @param prompt
     * @return
     */
    public static String answerPart1(String[] prompt) {
    
        return "";

    }


    /**
    
     * @param prompt
     * @return
     */
    public static String answerPart2(String[] prompt) {
  
        return "";

    }

}
