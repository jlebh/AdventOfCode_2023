package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

//No solution for part 2 yet
public class Day8 {
    public static void main(String[] args) throws IOException, InterruptedException {

        Example example1 = Meta.readExample1(Day8.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day8.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day8.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day8.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

private static  HashMap<String, Node> makeNodeMap(String[] prompt){
    HashMap<String, Node> nodes = new HashMap<>(prompt.length - 2);
    for (int i = 2; i < prompt.length; i++) {
        String nodeName = prompt[i].substring(0, 3);
        Node node = nodes.computeIfAbsent(nodeName, t -> new Node(t));
        nodeName = prompt[i].substring(prompt[i].indexOf("(") + 1, prompt[i].indexOf("(") + 4);
        Node left = nodes.computeIfAbsent(nodeName, t -> new Node(t));
        nodeName = prompt[i].substring(prompt[i].indexOf(", ") + 2, prompt[i].indexOf(", ") + 5);
        Node right = nodes.computeIfAbsent(nodeName, t -> new Node(t));
        node.left = left;
        node.right = right;
    }
    return nodes;
}

    public static String answerPart1(String[] prompt) {
        char[] directions = prompt[0].toCharArray();
        // make the node map
        HashMap<String, Node> nodes =makeNodeMap(prompt);
        int stepsTaken = 0;

        Node currentNode = nodes.get("AAA");
        Node targetNode = nodes.get("ZZZ");
        int i = 0;
        // traverse until found
        while (currentNode != targetNode) {
            currentNode = directions[i] == 'L' ? currentNode.left : currentNode.right;
            stepsTaken++;
            i = (i + 1) % directions.length;
        }

        return stepsTaken + "";

    }

    static class Node {
        String name;
        Node left, right;
        boolean endNode;

        Node(String name) {
            this.name = name;
            endNode = name.endsWith("Z");
        }

        @Override
        public String toString() {
            return name + "->" + left.name + "," + right.name;
        }
    }

    public static String answerPart2(String[] prompt) {
        char[] directions = prompt[0].toCharArray();
        HashMap<String, Node> nodes =makeNodeMap(prompt);

        Collection<Day8.Node> values = nodes.values();
        ArrayList<Node> startingNodes = new ArrayList<>();
        for (Node node : values) {
            if (node.name.endsWith("A")) {
                startingNodes.add(node);
            }
        }
        Node[] currentNodes = startingNodes.toArray(new Node[startingNodes.size()]);

        long totalStepsNeeded = 1;
        int i = 0;
        // from testing and online consensus: There is exactly one relationship for each
        // start and end point. The number of steps it takes to reach the end point the
        // first time is the same amount of steps it takes to reach it again
        // so solution can be found by calculating LCM
        int[] stepsTaken = new int[currentNodes.length];
        for (int j = 0; j < currentNodes.length; j++) {
            i = 0;
            while (!currentNodes[j].name.endsWith("Z")) {
                currentNodes[j] = directions[i] == 'L' ? currentNodes[j].left : currentNodes[j].right;
                i = (i + 1) % directions.length;
                stepsTaken[j]++;
            }
        }
        long commonPrimes = 1;
        int maxPrime =Meta.getMin(stepsTaken)/2;
        // remove primes which are part of all distances and only apply them once
        PrimeLoop: for (int primeCandidate = 2; primeCandidate <= maxPrime;) {
            for (int j = 0; j < stepsTaken.length; j++) {
                // if any distance is not divisible by primeCandidate, go to next candidate
                if (stepsTaken[j] % primeCandidate != 0) {
                    primeCandidate++;
                    continue PrimeLoop;
                }
            }
            // all were divisible
            for (int j = 0; j < stepsTaken.length; j++) {
                stepsTaken[j] /= primeCandidate;
            }
            commonPrimes *= primeCandidate;
            // primeCandidate does not get increased, same prime could occur multiple times!
        }
        
        // now calculate the lcm
        for (int distance : stepsTaken) {
            totalStepsNeeded *= distance;
        }
        totalStepsNeeded *= commonPrimes;
        return totalStepsNeeded + "";

    }

}
