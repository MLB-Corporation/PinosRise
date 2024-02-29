package utils;

public class Timer {
    private long startTime;
    private long elapsedTime;
    private boolean running;

    public Timer() {
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }

    // Start the timer
    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    // Stop the timer
    public void stop() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    // Reset the timer
    public void reset() {
        elapsedTime = 0;
        startTime = 0;
        running = false;
    }

    // Get the elapsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        } else {
            return elapsedTime;
        }
    }
}