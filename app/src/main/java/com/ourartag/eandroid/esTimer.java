package com.ourartag.eandroid;
import android.os.Handler;
public class esTimer {
    private Handler handler;
    private boolean paused;

    private int interval;
    public esTimer (Runnable runnable, int interval, boolean started) {
        paused = true;
        handler = new Handler ();
        this.runnable = runnable;
        this.interval = interval;
        if (started)
            startTimer ();
    }
    private Runnable task = new Runnable () {
        @Override
        public void run() {
            if (!paused) {
                runnable.run ();
                esTimer.this.handler.postDelayed (this, interval);
            }
        }
    };

    private Runnable runnable;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void startTimer () {
        paused = false;
        handler.postDelayed (task, interval);
    }

    public void stopTimer () {
        paused = true;
    }

    public Boolean isTimerOn(){
        if(paused){
            return false;
        }else{
            return true;
        }
    }

}
