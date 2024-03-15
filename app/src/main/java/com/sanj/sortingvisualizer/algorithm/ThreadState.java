package com.sanj.sortingvisualizer.algorithm;

public class ThreadState {
    /*
     * Used to indicate the state of the sorting thread and is changed every time the thread state changes
     * */
    public static Boolean threadAlive = false;
    /*
     * Used to indicate the delay time of the sorting thread and is changed every time the thread is initialized
     * */
    public static int delayTime = 0;
}
