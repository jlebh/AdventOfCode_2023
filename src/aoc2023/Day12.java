package aoc2023;

import java.io.IOException;
import java.util.HashMap;

public class Day12 {

    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day12.class);

        String answer1 = answerPart1V2(example1.prompt);
        if (answer1.equals(example1.answer)) {
            System.out.println("Answer for example Part 1 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 1 does not match.\nIs:" + answer1 + "\nShould be:" + example1.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        Meta.timerStart();
        System.out.println("Answer for Part 1: " + answerPart1V2(Meta.readPrompt(Day12.class))+ Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day12.class);
        String answer2 = answerPart2V2(example2.prompt);
        if (answer2.equals(example2.answer)) {
            System.out.println("Answer for example Part 2 matches.");
        } else {
            System.out.println(
                    "Answer for example Part 2 does not match.\nIs:" + answer2 + "\nShould be:" + example2.answer);
            Thread.sleep(3000);
        }
        Thread.sleep(2000);
        Meta.timerStart();
        System.out.println("Answer for Part 2: " + answerPart2V2(Meta.readPrompt(Day12.class))+ Meta.timerElapsed());
    }

    public static String answerPart1V2(String[] prompt) {
        long sum = 0;
        for (String line : prompt) {

            Day12 lineSolver = new Day12();
            String[] split = line.split(" ");
            char[] pattern = split[0].toCharArray();
            split = split[1].split(",");
            int[] groups = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                groups[i] = Integer.parseInt(split[i]);
            }
            // the minimal length still remainning in the pattern to complete all remaining
            // groups
            int[] completeMinimalSize = new int[groups.length];
            for (int i = groups.length - 1; i >= 0; i--) {
                completeMinimalSize[i] += groups[i];
                for (int j = 0; j < i; j++) {
                    completeMinimalSize[j] += groups[i] + 1;
                }
            }
            sum += lineSolver.countArrangements(pattern, groups);

        }

        return sum + "";
    }

    public static String answerPart2V2(String[] prompt) {
        StringBuilder conditions = new StringBuilder();
        StringBuilder groups = new StringBuilder();
        for (int i = 0; i < prompt.length; i++) {
            String[] parts = prompt[i].split(" "); // [0]->condition, [1]->groups
            // reset buffers
            conditions.setLength(0);
            groups.setLength(0);

            // all patterns are repeated five times and seperated by ? or ,
            for (int j = 0; j < 5; j++) {
                conditions.append(parts[0]).append("?");
                groups.append(parts[1]).append(",");
            }
            conditions.setLength(conditions.length() - 1);// remove last "?"
            groups.setLength(groups.length() - 1);// remove last ","

            prompt[i] = conditions + " " + groups;
        }

        // the calculating logic can remain the same, only the inputs changed
        return answerPart1V2(prompt);

    }

    private char[] pattern;
    private int[] groups;
    private int[] minimalRequiredSizeToCompleteGroups; //the unused length required in pattern to complete all remaining groups from this index onwards.  
    HashMap<Integer, HashMap<Integer, Long>> storedCalculations;

    private long countArrangements(char[] pattern, int[] groups) {
        this.pattern = pattern;
        this.groups = groups;
        storedCalculations = new HashMap<>();

        // calculate the remaining minimum lenghth of pattern for each step in groups,
        // to allow for pruning some impossible branches
        minimalRequiredSizeToCompleteGroups = new int[groups.length];
        for (int i = groups.length - 1; i >= 0; i--) {
            minimalRequiredSizeToCompleteGroups[i] += groups[i];
            for (int j = 0; j < i; j++) {
                minimalRequiredSizeToCompleteGroups[j] += groups[i] + 1;
            }
        }
        return solverV2(0, 0);
    }

    public long solverV2(int patternOffset, int groupOffset) {
        HashMap<Integer, Long> hashMapPatternOffset = storedCalculations.get(patternOffset);
        
        //if patternOffset x groupOffset has been calculated before, return it
        if (hashMapPatternOffset != null && hashMapPatternOffset.containsKey(groupOffset)) {
            return hashMapPatternOffset.get(groupOffset);
        }
        // System.out.println("Solving for pattern offset " + patternOffset + "\t
        // groupOffset " + groupOffset);

        long sum = 0;
        // i is the loop specific offset
        OffsetLoop: for (int loopOffset = patternOffset; loopOffset < pattern.length; loopOffset++) {
            // we cant ignore broken parts
            if (loopOffset > 0 && pattern[loopOffset - 1] == '#') {
                return sum;
            }
            // if there are not enough parts left to satisfy this group, we no longer have
            // work to do
            if (minimalRequiredSizeToCompleteGroups[groupOffset] > pattern.length - loopOffset) {
                return sum;
            }
            for (int groupCheckOffset = 0; groupCheckOffset < groups[groupOffset]; groupCheckOffset++) {
                // if at any point there is a functional part,arrangement immediately fails
                if (pattern[loopOffset + groupCheckOffset] == '.') {
                    continue OffsetLoop;
                }
            }
            int newOffset = loopOffset + groups[groupOffset]; // next char not consumed by group
            if (groupOffset == groups.length - 1) { // if last group: check that there are no more damaged parts
                for (int iCheckNoMoreBrokenParts = newOffset; iCheckNoMoreBrokenParts < pattern.length; iCheckNoMoreBrokenParts++) {
                    if (pattern[iCheckNoMoreBrokenParts] == '#') { // damaged part found=invalid arrangement
                        continue OffsetLoop;
                    }
                }
                // no more damaged parts and all groups done. increase sum and try next offset!
                sum++;
                continue OffsetLoop;
            } else {// not the last group: must have a non-damaged part directly after
                if (pattern[newOffset] == '#') {
                    continue OffsetLoop;
                }

                // all good here?!: we can go into next recursion depth. newOffset+1 because we
                // have cleared one seperator part
                long branchValue = solverV2(newOffset + 1, groupOffset + 1);

                // store the result of the branch
                storeBranchResult(newOffset + 1, groupOffset + 1, branchValue);
                sum += branchValue;

            }

        }
        return sum;

    }

    /**
     * Stores branch results at the given offsets
     * 
     * @param patternOffset
     * @param groupOffset
     * @param value
     */
    private void storeBranchResult(int patternOffset, int groupOffset, long value) {
        HashMap<Integer, Long> hashMapPatternOffset = storedCalculations.get(patternOffset);
        // check if the specific patternOffset and groupOffset has already been
        // calculcated. return if it does. make nested maps when needed
        if (hashMapPatternOffset == null) {
            hashMapPatternOffset = new HashMap<>();
            storedCalculations.put(patternOffset, hashMapPatternOffset);
        }
        hashMapPatternOffset.put(groupOffset, value);
    }
}
