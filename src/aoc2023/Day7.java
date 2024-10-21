package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

public class Day7 {
    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day7.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day7.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day7.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day7.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    public static String answerPart1(String[] prompt) {
        @SuppressWarnings("unchecked")
        ArrayList<String[]>[] handTypeBuckets = new ArrayList[7]; // hand type buckets for general power grouping
        for (int i = 0; i < handTypeBuckets.length; i++) {
            handTypeBuckets[i] = new ArrayList<>();
        }

        for (String line : prompt) {
            String[] cardsAndBid = line.split(" "); // seoerate cards and bid
            // i dont really care about the exact value of the chars/cards, as long as the
            // strength stays consistent. To allow later easier sorting, replace all of the
            // letters with something more in line as the digits natural ordering
            char base = '9';
            cardsAndBid[0] = cardsAndBid[0].replace('T', ++base).replace('J', ++base).replace('Q', ++base)
                    .replace('K', ++base)
                    .replace('A', ++base);

            HashMap<Character, Integer> cards = new HashMap<>();
            // count cards
            for (int i = 0; i < cardsAndBid[0].length(); i++) {
                cards.compute(cardsAndBid[0].charAt(i), new BiFunction<Character, Integer, Integer>() {
                    @Override
                    public Integer apply(Character t, Integer u) {
                        if (u == null)
                            return 1;
                        return u + 1;
                    }
                });
            }
            // move into list to allow sorting
            ArrayList<Object[]> sortableList = new ArrayList<>();
            for (Character c : cards.keySet()) {
                sortableList.add(new Object[] { c, cards.get(c) });
            }
            sortableList.sort((o1, o2) -> (Integer) o2[1] - (Integer) o1[1]);// sort from high to low card quantity

            // store hand by type
            switch ((Integer) sortableList.get(0)[1]) {
                // five-kind
                case 5 -> {
                    handTypeBuckets[0].add(cardsAndBid);
                }
                // four-kind
                case 4 -> {
                    handTypeBuckets[1].add(cardsAndBid);
                }
                case 3 -> {
                    if ((Integer) sortableList.get(1)[1] == 2) {
                        // full-house
                        handTypeBuckets[2].add(cardsAndBid);
                    } else {
                        // three-kind
                        handTypeBuckets[3].add(cardsAndBid);
                    }
                }
                case 2 -> {
                    if ((Integer) sortableList.get(1)[1] == 2) {
                        // two-pair
                        handTypeBuckets[4].add(cardsAndBid);
                    } else {
                        // one-pair
                        handTypeBuckets[5].add(cardsAndBid);
                    }
                }
                default -> {
                    // high-card
                    handTypeBuckets[6].add(cardsAndBid);
                }
            }

        }
        // further sorting within types, based on highest card at front|further back on
        // tie
        for (ArrayList<String[]> listOfHandsOfType : handTypeBuckets) {
            listOfHandsOfType.sort((o1, o2) -> {
                String s1 = (String) o1[0];
                String s2 = (String) o2[0];
                int index = 0;
                for (; s1.charAt(index) == s2.charAt(index); index++)
                    ;
                return s1.charAt(index) - s2.charAt(index);
            });
        }
        // result is sum of hand bid times hand rank, worst hand is rank 1
        long multiplier = 1;
        long sum = 0;
        for (int i = handTypeBuckets.length - 1; i >= 0; i--) {
            for (int j = 0; j < handTypeBuckets[i].size(); j++) {
                // System.out.println("Rank:" + i + " Mult:" + multiplier + " Hand:"
                // + handTypeBuckets[i].get(j)[0] + " Value:"
                // + handTypeBuckets[i].get(j)[1]);
                sum += multiplier++ * Integer.parseInt(handTypeBuckets[i].get(j)[1]);
            }
        }

        return sum + "";

    }

    public static String answerPart2(String[] prompt) {

        @SuppressWarnings("unchecked")
        ArrayList<String[]>[] handTypeBuckets = new ArrayList[7];// hand type buckets for general power grouping
        for (int i = 0; i < handTypeBuckets.length; i++) {
            handTypeBuckets[i] = new ArrayList<>();
        }

        for (String line : prompt) {
            String[] cardsAndBid = line.split(" ");
            // i dont really care about the exact value of the chars/cards, as long as the
            // strength stays consistent. To allow later easier sorting, replace all of the
            // letters with something more in line as the digits natural ordering
            char base = '9';
            // note: J is now assigned 1 because it is the joker and weakest in direct card
            // comparisons
            cardsAndBid[0] = cardsAndBid[0].replace('J', '1').replace('T', ++base).replace('Q', ++base)
                    .replace('K', ++base)
                    .replace('A', ++base);

            HashMap<Character, Integer> cards = new HashMap<>();
            // count cards
            for (int i = 0; i < cardsAndBid[0].length(); i++) {
                cards.compute(cardsAndBid[0].charAt(i), new BiFunction<Character, Integer, Integer>() {
                    @Override
                    public Integer apply(Character t, Integer u) {
                        if (u == null)
                            return 1;
                        return u + 1;
                    }
                });
            }
            // move into list to allow sorting
            ArrayList<Object[]> sortableList = new ArrayList<>();
            for (Character c : cards.keySet()) {
                sortableList.add(new Object[] { c, cards.get(c) });
            }
            // sort by pulling Joker ('1') to front (if presenet), all other from high to
            // low card quantity
            sortableList.sort((o1, o2) -> (Character) o1[0] == '1' ? -1
                    : (Character) o2[0] == '1' ? 1 : (Integer) o2[1] - (Integer) o1[1]);
            int jokers = 0;
            if ((Character) sortableList.get(0)[0] == '1') {
                jokers = (Integer) sortableList.get(0)[1];
                // remove joker so i dont have to change the switch-case from part 1
                sortableList.remove(0);
            }
            switch (jokers) {
                // five-kind
                case 4, 5 -> {
                    handTypeBuckets[0].add(cardsAndBid);
                }
                case 3 -> {
                    if ((Integer) sortableList.get(0)[1] == 2) {
                        // five-kind
                        handTypeBuckets[0].add(cardsAndBid);
                    } else {
                        // four-kind
                        handTypeBuckets[1].add(cardsAndBid);
                    }
                }
                case 2 -> {
                    switch ((Integer) sortableList.get(0)[1]) {
                        // five-kind
                        case 3 -> {
                            handTypeBuckets[0].add(cardsAndBid);
                        }
                        // four-kind
                        case 2 -> {
                            handTypeBuckets[1].add(cardsAndBid);
                        }
                        // three-kind
                        case 1 -> {
                            handTypeBuckets[3].add(cardsAndBid);
                        }
                    }
                }
                case 1 -> {
                    switch ((Integer) sortableList.get(0)[1]) {
                        // five-kind
                        case 4 -> {
                            handTypeBuckets[0].add(cardsAndBid);
                        }
                        // four-kind
                        case 3 -> {
                            handTypeBuckets[1].add(cardsAndBid);
                        }
                        case 2 -> {
                            if ((Integer) sortableList.get(1)[1] == 2) {
                                // full-house
                                handTypeBuckets[2].add(cardsAndBid);
                            } else {
                                // three-kind
                                handTypeBuckets[3].add(cardsAndBid);
                            }
                        }
                        case 1 -> {
                            // one-pair
                            handTypeBuckets[5].add(cardsAndBid);
                        }

                    }
                }
                case 0 -> {
                    switch ((Integer) sortableList.get(0)[1]) {
                        // five-kind
                        case 5 -> {
                            handTypeBuckets[0].add(cardsAndBid);
                        }
                        // four-kind
                        case 4 -> {
                            handTypeBuckets[1].add(cardsAndBid);
                        }
                        case 3 -> {
                            if ((Integer) sortableList.get(1)[1] == 2) {
                                // full-house
                                handTypeBuckets[2].add(cardsAndBid);
                            } else {
                                // three-kind
                                handTypeBuckets[3].add(cardsAndBid);
                            }
                        }
                        case 2 -> {
                            if ((Integer) sortableList.get(1)[1] == 2) {
                                // two-pair
                                handTypeBuckets[4].add(cardsAndBid);
                            } else {
                                // one-pair
                                handTypeBuckets[5].add(cardsAndBid);
                            }
                        }
                        default -> {
                            // high-card
                            handTypeBuckets[6].add(cardsAndBid);
                        }
                    }
                }

            }

        }

        // further sorting within types, based on highest card at front|further back on
        // tie
        for (ArrayList<String[]> rank : handTypeBuckets) {
            rank.sort((o1, o2) -> {
                String s1 = (String) o1[0];
                String s2 = (String) o2[0];
                int index = 0;
                for (; s1.charAt(index) == s2.charAt(index); index++)
                    ;
                return s1.charAt(index) - s2.charAt(index);
            });
        }

        // result is sum of hand bid times hand rank, worst hand is rank 1
        long multiplier = 1;
        long sum = 0;
        for (int i = handTypeBuckets.length - 1; i >= 0; i--) {
            for (int j = 0; j < handTypeBuckets[i].size(); j++) {
                // System.out.println(
                // "Rank:" + i + " Mult:" + multiplier + " Hand:" + handTypeBuckets[i].get(j)[0]
                // + " Value:"
                // + handTypeBuckets[i].get(j)[1]);
                sum += multiplier++ * Integer.parseInt(handTypeBuckets[i].get(j)[1]);
            }
        }

        return sum + "";

    }

}
