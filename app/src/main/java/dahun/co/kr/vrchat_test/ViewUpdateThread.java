package dahun.co.kr.vrchat_test;

import android.support.annotation.MainThread;

public class ViewUpdateThread extends Thread {
    boolean runningFlag = true;

    public void terminate(){
        runningFlag = false;
    }

    @Override
    public void run() {
        //super.run();
        while(runningFlag) {
            MainActivity.handler.sendEmptyMessage(0);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
