package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        List<WorkerThread> tasks = new ArrayList<>();        // Создаём массив c пустыми заданиями WorkerThread
        for (int i = 0; i < 10; ++i)
            tasks.add(new WorkerThread(i));  //  Добавляем задания в массив

        ThreadPool_final threadPool = new ThreadPool_final(5); // Создаём пул с 5ю потоками


            for (WorkerThread task : tasks) {
                threadPool.enqueue(task);  // Вставляем в пул задания
            }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        threadPool.close(); // Останавливаем пул
    }
}

