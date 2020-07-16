package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadPool_final implements AutoCloseable {

    private AtomicBoolean isClosed = new AtomicBoolean(false);
    private NewList<Runnable> tasks = new NewList<>();
    private List<Thread> threads = new ArrayList<>();
    private final Object help = new Object();
    private int NumThread;

    public ThreadPool_final(int threadNum) { //Конструктор класса
        this.NumThread = threadNum; //Число потоков пула
        initThreads(this.NumThread);  // запускаем метод initThread и из него запускаем потоки
    }

    public void enqueue(Runnable task) {
        if (!isClosed.get()) {
            tasks.add(task);  //Вносим задачи в наш массив
            synchronized (help) {
                help.notify();  //Запускаем работу newTask
            }
        } else
            System.out.println("Thread pool is closed");
    }

    private void initThreads(int N) {
        for (int i = 0; i < N; ++i) {
            Thread worker = new TaskWorker();   // Создаём потоки
            worker.setName("Worker" + i);       // Задаём имя каждому потоку
            threads.add(worker);                // В массив threads вносим потки worker
            worker.start();                     // Запускаем потоки
        }
    }

    @Override
    public void close() {
        isClosed.set(true);
        synchronized (help) {
            help.notifyAll();
        }
        for (Thread thread : threads)
            thread.interrupt();
    }


    private final class TaskWorker extends Thread {

        @Override
        public void run() {
            while (!isClosed.get()) {                   // Потоки не остановленны?
                try {
                    Runnable nextTask = tasks.get();    // Получаем задачу
                    if (nextTask != null) {             // Задача есть?
                        nextTask.run();                 // Выполняем задачу в потоке
                    } else
                        synchronized (help) {
                        if(!isClosed.get())
                            help.wait();                //help уходит в ожидание задач
                        }
                } catch (InterruptedException e) {
                    e.getStackTrace();
                    this.interrupt();
                }
            }

            System.out.println(this.getName() + ": stop");
        }
    }
}


class NewList<T> {
    private Node<T> N;
    private AtomicReference<Node<T>> head;
    private AtomicReference<Node<T>> tail;


    public NewList() {
        N = new Node<>(null, new AtomicReference<>());
        head = new AtomicReference<>(N);
        tail = new AtomicReference<>(N);
    }

    public T get() {
        T item;
        Node<T> head, next;
        while (true) {
            head = this.head.get();
            next = head.next.get();
            if (next == null) {
                return null;
            } else if (this.head.compareAndSet(head, next)) {
                item = next.value;
                break;
            }
        }
        return item;
    }

    public void add(T item) {
        Node<T> newTail = new Node<>(item, new AtomicReference<>());
        Node<T> tail;
        while (true) {
            tail = this.tail.get();
            if (tail.next.compareAndSet(null, newTail)) {
                this.tail.compareAndSet(tail, newTail);
                return;
            } else
                this.tail.compareAndSet(tail, tail.next.get());
        }
    }

    private class Node<T> {
        T value;
        AtomicReference<Node<T>> next;

        public Node(T value, AtomicReference<Node<T>> next) {
            this.value = value;
            this.next = next;
        }
    }
}

