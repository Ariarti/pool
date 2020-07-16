package com.company;

public class WorkerThread implements Runnable {

    private int command;

    public WorkerThread(int command) {
        this.command = command;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start. Command = " + command);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}

