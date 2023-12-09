package com.ourartag.eandroid;
import android.os.Handler;
public class esTimer {
    private Handler handler;
    private boolean paused;
    private long startTime, durationtime;
    private int interval;
    private int startval;
    public esTimer (Runnable runnable, int startv, int interv, boolean started) {
        paused = true;
        handler = new Handler ();
        this.runnable = runnable;
        this.interval = interv;
        this.startval = startv;
        if (started)
            startTimer ();
    }
    private Runnable task = new Runnable () {
        @Override
        public void run() {
            if (!paused) {
                startTime = System.currentTimeMillis();
                runnable.run ();

                esTimer.this.handler.postDelayed (this, interval);
            }
        }
    };

    private Runnable runnable;

    public int getInterval() {
        return interval;
    }
    public long getCurrentDuration() {
        durationtime = System.currentTimeMillis() - startTime;
        return durationtime;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void startTimer () {
        paused = false;
        startTime = System.currentTimeMillis();
        handler.postDelayed (task, startval);
    }

    public void stopTimer () {
        paused = true;
        handler.removeCallbacks(task);
        handler.removeCallbacksAndMessages(null);
    }

    public Boolean isTimerOn(){
        if(paused){
            return false;
        }else{
            return true;
        }
    }

}
