package com.allen.BankThread;

public class PCB {

    int index; // 进程在队列中的序号，代表先后到达时间
    int PRI; // 优先级

    Thread thread; //进程

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPRI() {
        return PRI;
    }

    public void setPRI(int PRI) {
        this.PRI = PRI;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
