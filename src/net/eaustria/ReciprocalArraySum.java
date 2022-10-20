/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria;

/**
 *
 * @author bmayr
 */

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        return Arrays.stream(input).sum();
    }
  

    /**
     * This class stub can be filled in to implement the body of each task
     * created to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveTask<Double> {
        /**
         * Starting index for traversal done by this task.
         */
        private final int startIndexInclusive;
        /**
         * Ending index for traversal done by this task.
         */
        private final int endIndexExclusive;
        /**
         * Input array to reciprocal sum.
         */
        private final double[] input;
        /**
         * Intermediate value produced by this task.
         */
        private double value;
        
        public static int SEQUENTIAL_THRESHOLD = 2;

        /**
         * Constructor.
         * @param setStartIndexInclusive Set the starting index to begin
         *        parallel traversal at.
         * @param setEndIndexExclusive Set ending index for parallel traversal.
         * @param setInput Input values
         */
        public ReciprocalArraySumTask(final int setStartIndexInclusive,
                final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }


        /**
         * Getter for the value produced by this task.
         * @return Value produced by this task
         */
        public double getValue() {
            return value;
        }

        @Override
        protected Double compute() {

            ReciprocalArraySumTask left = null;
            ReciprocalArraySumTask right = null;
            var arr = Arrays.copyOfRange(input, startIndexInclusive, endIndexExclusive);
            if (arr.length > SEQUENTIAL_THRESHOLD) {

                left = new ReciprocalArraySumTask(startIndexInclusive, endIndexExclusive - (endIndexExclusive - startIndexInclusive) / 2, input);
                right = new ReciprocalArraySumTask(endIndexExclusive - (endIndexExclusive - startIndexInclusive) / 2, endIndexExclusive, input);

            } else {
                return seqArraySum(arr);
            }
            // Implement Thread forking on Threshold value. (If size of
            // array smaller than threshold: compute sequentially else, fork 
            // 2 new threads
            invokeAll(left, right);
            return left.join() + right.join();
        }
    }
  

    /**
     *  Extend the work you did to implement parArraySum to use a set
     * number of tasks to compute the reciprocal array sum. 
     *
     * @param input Input array
     * @param numTasks The number of tasks to create
     * @return The sum of the reciprocals of the array input
     */
    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {
        double sum = 0;
       // Start Calculation with help of ForkJoinPool
       ForkJoinPool pool = new ForkJoinPool();
       ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length+1, input);
       ReciprocalArraySumTask.SEQUENTIAL_THRESHOLD = input.length / numTasks;
       sum = pool.invoke(task);
       pool.shutdown();
       return sum;
    }

    public static void main(String[] args) {
        double[] input = new double[4];
        for (int i = 1; i <= input.length; i++) {
            input[i-1] = 1d / i;
        }
        System.out.println(parManyTaskArraySum(input, 11));
    }
}

