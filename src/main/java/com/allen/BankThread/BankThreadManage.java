package com.allen.BankThread;

import java.util.*;

import static java.lang.Thread.State.RUNNABLE;


public class BankThreadManage implements Runnable{

    public static Queue<PCB> ready;

    //用index表示线程到达的先后次序
    public static int pcbIndex = 0;

    //初始化PCB，向ready队列中添加PCB
    public static void addThread(Thread thread){
        PCB pcb = new PCB();
        pcb.setIndex(pcbIndex);

        if (thread.getName().equals("single")){
            pcb.setPRI(2);
        } else if (thread.getName().equals("multiple")) {
            pcb.setPRI(1);
        }

        pcb.setThread(thread);
        ready.add(pcb);
        pcbIndex++;
    }

    //优先级队列设置优先级排序，优先级高的排队列前面，如果优先级相同，先到达的排前面
    @Override
    public void run() {
        ready = new PriorityQueue<PCB>(new Comparator<PCB>() {
            public int compare(PCB o1, PCB o2) {
                if(o1.PRI != o2.PRI){
                    return (o2.PRI - o1.PRI) > 0 ? 1 : -1;
                }else {
                    return o1.getIndex() - o2.getIndex();
                }
            }
        });


        PCB curPCB = null;
        Thread tmpThread = null;
        while (true){

            if (ready.peek()==null){
                while (true){

                    //检查新到线程，如果是单账户的线程并且当前批处理进程正在执行，就暂停批处理线程
                    //直到ready队列中单账户的线程执行完再继续执行
                    if (!ready.isEmpty()){

                        if (ready.peek().thread.getName().equals("single") && curPCB!=null && curPCB.thread.getState().equals(RUNNABLE) && curPCB.thread.getName().equals("multiple")){
                            tmpThread = curPCB.thread;
                            try {
                                tmpThread.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            while (true){
                                curPCB = ready.poll();
                                curPCB.thread.start();
                                if (ready.peek()==null||ready.peek().thread.getName().equals("multiple")){
                                    curPCB.thread.notifyAll();
                                    break;
                                }
                            }

                        }

                        break;
                    }
//                    System.out.println("Waiting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else {
                curPCB = ready.poll();
                curPCB.thread.start();
                System.out.println("PCB"+curPCB.getIndex());
            }
        }
    }
}

