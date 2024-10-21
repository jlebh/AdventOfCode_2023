package aoc2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Day5 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Example example1 = Meta.readExample1(Day5.class);
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
                .println("Answer for Part 1: " + answerPart1(Meta.readPrompt(Day5.class)) + " " + Meta.timerElapsed());
        Thread.sleep(3000);
        Example example2 = Meta.readExample2(Day5.class);
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
                .println("Answer for Part 2: " + answerPart2(Meta.readPrompt(Day5.class)) + " " + Meta.timerElapsed());
        Thread.sleep(2000);
    }

    public static String answerPart1(String[] prompt) {

        final int DEST = 0, SRC = 1, RANGE = 2; // how the translation data is ordered in input lines
        String seedsLine = prompt[0];
        String[] seeds = seedsLine.split(": ")[1].split(" ");

        long[] valuesToTranslate = new long[seeds.length];
        for (int i = 0; i < valuesToTranslate.length; i++) {
            valuesToTranslate[i] = Long.parseLong(seeds[i]);
        }

        // line 1 empty
        for (int promptLineIndex = 2; promptLineIndex < prompt.length; promptLineIndex++) {
            // System.out.println(prompt[i]);
            promptLineIndex++; // skip x-to-y map line (contains only informational text)
            // stores all mappings for this layer (eg seed-to-soil)
            ArrayList<long[]> listLayerMappings = new ArrayList<>();
            // add up all mappings for this stage (cutoff is empty line)
            while (promptLineIndex < prompt.length && !prompt[promptLineIndex].equals("")) {
                String[] sValues = prompt[promptLineIndex].split(" ");
                long[] mapping = new long[3];
                for (int j = 0; j < 3; j++) {
                    mapping[j] = Long.parseLong(sValues[j]);
                }
                listLayerMappings.add(mapping);
                promptLineIndex++;
            }

            long[][] layerMappings = (long[][]) listLayerMappings.toArray(new long[listLayerMappings.size()][]);

            for (int i = 0; i < valuesToTranslate.length; i++) {
                long value = valuesToTranslate[i];
                // we try find a suitable mapping here,
                // otherwise destination number = source number
                for (long[] mappingCandidate : layerMappings) {
                    // exclusive end since range is effectively -1(the start position is also
                    // counted in it)
                    if (mappingCandidate[SRC] <= value && (mappingCandidate[SRC] + mappingCandidate[RANGE]) > value) {
                        valuesToTranslate[i] = mappingCandidate[DEST] + value - mappingCandidate[SRC];
                        break;
                    }
                }
            }
        }
        // answer is lowest location number
        long result = Long.MAX_VALUE;
        for (long l : valuesToTranslate) {
            result = result < l ? result : l;
        }
        return result + "";
    }

    public static String answerPart2(String[] prompt) {
        final int DEST = 0, SRC = 1, RANGE = 2;// how the translation data is ordered in input lines
        final int SEED_POS = 0, SEED_RANGE = 1;// indices for input/output of layers//positional data
        String seedsLine = prompt[0];
        String[] valuesToTranslate = seedsLine.split(": ")[1].split(" ");

        ArrayList<long[]> inputRanges = new ArrayList<>();
        for (int i = 0; i < valuesToTranslate.length; i += 2) {
            inputRanges
                    .add(new long[] { Long.parseLong(valuesToTranslate[i]), Long.parseLong(valuesToTranslate[i + 1]) });
        }
        // next line is empty
        for (int promptLineIndex = 2; promptLineIndex < prompt.length; promptLineIndex++) {
            // System.out.println(prompt[promptLineIndex]);
            promptLineIndex++;// skip x-to-y map line (contains only informational text)
            // stores all mappings for this layer (eg seed-to-soil). This time all mappings
            // are needed to be ordered, so read all for this x-to-y stage and then order
            // sort them
            ArrayList<long[]> listLayerMappings = new ArrayList<>();
            // add up all mappings for this stage (cutoff is empty line)
            while (promptLineIndex < prompt.length && !prompt[promptLineIndex].equals("")) {
                String[] sValues = prompt[promptLineIndex].split(" ");
                long[] mapping = new long[3];
                for (int j = 0; j < 3; j++) {
                    mapping[j] = Long.parseLong(sValues[j]);
                }
                listLayerMappings.add(mapping);
                promptLineIndex++;
            }
            // sort layers so the mapping logic is easier
            listLayerMappings.sort(new Comparator<long[]>() {
                @Override
                public int compare(long[] o1, long[] o2) {
                    if (o1[SRC] == o2[SRC])
                        return 0;
                    return o1[SRC] - o2[SRC] > 0 ? 1 : -1;
                }
            });
            // convert for some ease-of-use
            long[][] layerMappings = (long[][]) listLayerMappings.toArray(new long[listLayerMappings.size()][]);

            ArrayList<long[]> outputRanges = new ArrayList<>(); // output for the mapped seed ranges of this stage

            while (!inputRanges.isEmpty()) {
                long[] currentSeedSegment = inputRanges.remove(0);
                long[] currentTranslationMapping;
                int indexNextTranslationMapping = 0;
                // the segment is not exhausted and there is more mappings to try
                while (currentSeedSegment[SEED_RANGE] > 0 && indexNextTranslationMapping < layerMappings.length) {
                    currentTranslationMapping = layerMappings[indexNextTranslationMapping];
                    // does some part of the seed segment point to before the next possible
                    // translation?
                    if (currentSeedSegment[SEED_POS] < currentTranslationMapping[SRC]) {
                        // split into two segments. non-overlapping part gets sent to output,
                        // overlapping part gets cropped and used later
                        long overlapRange = Math.max(currentSeedSegment[SEED_RANGE],
                                currentTranslationMapping[SRC] - currentSeedSegment[SEED_POS]);
                        outputRanges.add(new long[] { currentSeedSegment[SEED_POS], overlapRange });
                        currentSeedSegment[SEED_POS] += overlapRange;
                        currentSeedSegment[SEED_RANGE] -= overlapRange;
                        continue;

                    }
                    // no overlap with current segment? (=position/value is higher than what this
                    // mapping covers)
                    if (currentSeedSegment[SEED_POS] > currentTranslationMapping[SRC]
                            + currentTranslationMapping[RANGE]) {
                        indexNextTranslationMapping++;
                        continue;
                    }
                    // is there overlap between seeds and mapping?
                    if (currentSeedSegment[SEED_POS] >= currentTranslationMapping[SRC]
                            && currentSeedSegment[SEED_POS] <= (currentTranslationMapping[SRC]
                                    + currentTranslationMapping[RANGE] - 1)) {

                        // find the overlapping part in this translation (seed start will usually be
                        // same as range start, but might also start somewhere inside the mapping range)
                        long overlapRange = Math.min(currentSeedSegment[SEED_RANGE], currentTranslationMapping[RANGE]
                                - (currentSeedSegment[SEED_POS] - currentTranslationMapping[SRC]));

                        // calculate new seed segmnet start from DEST and possible offset (if the seed
                        // range began inside the mapping, not at the start of it)
                        outputRanges.add(new long[] { currentTranslationMapping[DEST]
                                + (currentSeedSegment[SEED_POS] - currentTranslationMapping[SRC]),
                                overlapRange });

                        currentSeedSegment[SEED_POS] += overlapRange;
                        currentSeedSegment[SEED_RANGE] -= overlapRange;
                        // this mapping is exhausted (well not necessarily, but either the mapping is
                        // exhausted or the seeds range, but in the second case increasing index is not
                        // bad)
                        indexNextTranslationMapping++;
                        continue;
                    }

                }
                // segment not exhausted after all mappings? keep range unchanged for next stage
                if (currentSeedSegment[SEED_RANGE] > 0) {
                    outputRanges.add(currentSeedSegment);
                }
            }
            inputRanges = outputRanges;
        }
        // answer is lowest location number
        long result = Long.MAX_VALUE;
        for (long[] range : inputRanges) {
            result = result < range[SEED_POS] ? result : range[SEED_POS];
        }
        return result + "";
    }

}