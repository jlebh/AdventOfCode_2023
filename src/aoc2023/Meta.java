package aoc2023;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//TODO give some way to read more than one example for any specific day/prompt (rare but theres some days with multiple example prompts)

public class Meta {
    private static long timerStartTimeStamp = System.currentTimeMillis();

    public static Example readExample1(Class<?> c) throws IOException {
        String dayPath = "days\\" + c.getName().substring(c.getPackageName().length() + 4) + "\\";
        BufferedReader inExample = new BufferedReader(
                new FileReader(dayPath + "example1"));
        ArrayList<String> list = new ArrayList<>();

        while (inExample.ready()) {
            list.add(inExample.readLine());
        }
        inExample.close();
        BufferedReader inAnswer = new BufferedReader(
                new FileReader(dayPath + "answer1"));
        String answer = inAnswer.readLine();
        inAnswer.close();
        Example example = new Example();
        example.prompt = list.toArray(new String[list.size()]);
        example.answer = answer;
        return example;
    }

    public static Example readExample2(Class<?> c) throws IOException {
        String dayPath = "days\\" + c.getName().substring(c.getPackageName().length() + 4) + "\\";
        BufferedReader inExample = new BufferedReader(
                new FileReader(dayPath + "example2"));
        ArrayList<String> list = new ArrayList<>();

        while (inExample.ready()) {
            list.add(inExample.readLine());
        }
        inExample.close();
        BufferedReader inAnswer = new BufferedReader(
                new FileReader(dayPath + "answer2"));
        String answer = inAnswer.readLine();
        inAnswer.close();
        Example example = new Example();
        example.prompt = list.toArray(new String[list.size()]);
        example.answer = answer;
        return example;
    }

    public static String[] readPrompt(Class<?> c) throws IOException {
        String dayPath = "days\\" + c.getName().substring(c.getPackageName().length() + 4) + "\\";

        BufferedReader in = new BufferedReader(new FileReader(dayPath + "input"));
        ArrayList<String> list = new ArrayList<>();
        while (in.ready()) {
            list.add(in.readLine());
        }
        in.close();
        return list.toArray(new String[list.size()]);
    }

    public static void timerStart() {
        timerStartTimeStamp = System.currentTimeMillis();
    }

    public static String timerElapsed() {
        long elapsedTimeMs = System.currentTimeMillis() - timerStartTimeStamp;
        String s = String.format("[%02d:%02d:%02d.%03d]", elapsedTimeMs / (60 * 60 * 1000),
                (elapsedTimeMs % (60 * 60 * 1000)) / (60 * 1000), (elapsedTimeMs % (60 * 1000)) / 1000,
                elapsedTimeMs % 1000);
        return s;
    }

    public static String timerLap() {
        long elapsedTimeMs = System.currentTimeMillis() - timerStartTimeStamp;
        String s = String.format("[%02d:%02d:%02d.%03d]", elapsedTimeMs / (60 * 60 * 1000),
                (elapsedTimeMs % (60 * 60 * 1000)) / (60 * 1000), (elapsedTimeMs % (60 * 1000)) / 1000,
                elapsedTimeMs % 1000);
        timerStartTimeStamp = System.currentTimeMillis();
        return s;
    }

    public static String formatMsToString(long timeInMs) {
        long minutes = timeInMs / 60000;
        return (minutes == 0 ? "" : minutes + "min ") + ((timeInMs / 1000) % 60) + "sec " + (timeInMs % 1000) + "ms";
    }

    public static int getMax(int[] ints){
        int max = Integer.MIN_VALUE;
        for (int val : ints) {
           max = Math.max(max, val) ;
        }
        return max;
    }
    public static long getMax(long[] longs){
        long max = Long.MIN_VALUE;
        for (long val : longs) {
           max = Math.max(max, val) ;
        }
        return max;
    } public static int getMin(int[] ints){
        int min = Integer.MAX_VALUE;
        for (int val : ints) {
           min = Math.min(min, val) ;
        }
        return min;
    }
    public static long getMin(long[] longs){
        long min = Long.MAX_VALUE;
        for (long val : longs) {
           min = Math.min(min, val) ;
        }
        return min;
    }

}

// TODO: possible int overflow for very large recursive workload creations!!.
// change workunit type to long, and change semaphore system so it will look for
// no more work or something similar as a signal for completion. also the
// awaitFinalization probaly needs a change too since a LONG amount of permits
// is prolly not allowed
class MyThreadCoordinator {
    public List<String> results = Collections.synchronizedList(new ArrayList<>());
    Semaphore idleCores, waitingThreads;
    Queue<MyRunnable> queuedThreads;
    int cores;
    long workunits = 0;
    long workunit_sub = 0; // these are spawned seperately from normal workunits and are produced by them
                           // and other subs
    private Semaphore workunitsFinished;

    private Semaphore noMoreInputs;
    long threadActiveTimeMs = 0;
    long timeOfInstantiation;
    long timeOfLastWorkunitAddition = 0;
    Semaphore allWorkDone; // this sempaphore will oly be released after all workunits have finished
    Semaphore event; // maybe remove this field:::::this indicates that something has happened. most
                     // likely: a thread has finished or a WU has been added. The coordinator will
                     // wait

    MyThreadCoordinator() {
        timeOfInstantiation = System.currentTimeMillis();
        cores = Runtime.getRuntime().availableProcessors();
        idleCores = new Semaphore(cores);
        waitingThreads = new Semaphore(0);
        workunitsFinished = new Semaphore(0);
        allWorkDone = new Semaphore(0);
        noMoreInputs = new Semaphore(0);
        event = new Semaphore(1);
        queuedThreads = new LinkedList<>();
        new Thread() {
            public void run() {
                this.setPriority((NORM_PRIORITY + MIN_PRIORITY) / 2);
                // continue to wait and run new threads as long as there are new threads
                // waiting, or none have been finished (in case adding first WU takes a long
                // time), or awaitFinalization has not been called (via noMoreInput), or any
                // work is
                // still ongoing (for the case that a WU can spawn additional WUs)
                while (queuedThreads.peek() != null || workunitsFinished.availablePermits() == 0
                        || noMoreInputs.availablePermits() == 0
                        || idleCores.availablePermits() < cores) {
                    try {
                        idleCores.acquire();
                        if (waitingThreads.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                            queuedThreads.poll().start();
                        } else {
                            idleCores.release();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                allWorkDone.release();
            };
        }.start();
    }

    void newThread(MyRunnable runnable) {
        runnable.myCoordinator = this;
        workunits++;
        queuedThreads.add(runnable);
        waitingThreads.release();
    }

    void newSubThread(MyRunnable runnable) {

    }

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    synchronized void reportResult(String input, String output, long timeItTookMs) {
        results.add(output);
        threadActiveTimeMs += timeItTookMs;
        idleCores.release();
        System.out.println(input + " -> " + output);
        workunitsFinished.release();
        System.out.println("Progress:" + workunitsFinished.availablePermits() + "/" + workunits + " finished. ("
                + (decimalFormat.format((double) workunitsFinished.availablePermits() / workunits * 100)) + "%)");
    }

    void awaitFinalization() {
        try {
            noMoreInputs.release();
            allWorkDone.acquire();
            System.out.println("Finished " + workunits + " workunits");
            System.out
                    .println("Total time: " + Meta.formatMsToString(System.currentTimeMillis() - timeOfInstantiation));
            System.out.println("Time in threads:" + Meta.formatMsToString(threadActiveTimeMs));
            System.out.println("Average time per workunit:" + Meta.formatMsToString(threadActiveTimeMs / workunits));

            // Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    long sumResults() {
        long sum = 0;
        for (String string : results) {
            sum += Long.parseLong(string);
        }
        return sum;
    }
}

abstract class MyRunnable extends Thread {
    final String input;
    long startTimeMs;
    MyThreadCoordinator myCoordinator;

    public MyRunnable(String input) {
        this.input = input;
    }

    abstract String payload(String input);

    public void run() {
        startTimeMs = System.currentTimeMillis();
        String output = payload(input);
        myCoordinator.reportResult(input, output, System.currentTimeMillis() - startTimeMs);
    }
}

class Example {
    public Example() {
    }

    String answer;
    String[] prompt;
}