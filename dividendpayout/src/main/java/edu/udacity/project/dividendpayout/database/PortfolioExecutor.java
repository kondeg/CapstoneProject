package edu.udacity.project.dividendpayout.database;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class PortfolioExecutor implements Executor{

    @Override
    public void execute(@NonNull Runnable runnable) {
        new Thread(runnable).start();
    }

}
