package com.allen.fileIO;


import com.allen.pojo.Account;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class MultipleFileReader {
    private int threadSize;
    private String charset;
    private int bufferSize;
    private Handle handle;
    private ExecutorService executorService;
    private long fileLength;
    private RandomAccessFile rAccessFile;
    private Set<StartEndPair> startEndPairs;
    private CyclicBarrier cyclicBarrier;
    private AtomicLong counter = new AtomicLong(0);
    private HashMap<String, Account> returnAcMap;
    private List<String> acList;
    private List<String> curList;

    private int isDone = 0;

    //读取对应csv中的目标账户信息
    private MultipleFileReader(File file, Handle handle, String charset, int bufferSize, int threadSize,List<String> acList, HashMap<String,Account> returnAcMap,List<String> curList){
        this.fileLength = file.length();
        this.handle = handle;
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.threadSize = threadSize;
        this.acList = acList;
        this.returnAcMap = returnAcMap;
        this.curList = curList;
        try {
            this.rAccessFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.executorService = Executors.newFixedThreadPool(threadSize);
        startEndPairs = new HashSet<StartEndPair>();
    }

    public void start(){
        long everySize = this.fileLength/this.threadSize;
        try {
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final long startTime = System.currentTimeMillis();
        //第一个是参与线程的个数
        //第二个构造方法有一个 Runnable 参数，这个参数的意思是最后一个到达线程要做的任务
        cyclicBarrier = new CyclicBarrier(startEndPairs.size(),new Runnable() {

            @Override
            public void run() {
                System.out.println("use time: "+(System.currentTimeMillis()-startTime));
                System.out.println("all line: "+counter.get());
                if (acList.size()==0){
                    executorService.shutdown();
                    handle.returnAcMap(returnAcMap);
                    isDone = 1;
                }

                //逐一去除不在文件索引中的，防止错误
                while (acList.size()>0){
                    String Acid = acList.get(0);

                    String fileIndex = Acid.substring(0,4);

                    //String加入新的就绪List后，acList要去除，如果账号符合格式但是不存在，map里没有，curList有acList没有
                    //如果账号不符合格式，也只将其放入curList
                    curList = new ArrayList<>();
                    for (String ac : acList){
                        if (ac.matches(fileIndex + "(.*)")){
                            curList.add(ac);
                        }
                    }
                    acList.removeIf(curList::contains);

                    File file = new File("D:\\Program\\BankData\\resultSet\\result_" + fileIndex + ".csv");
                    if (file.exists()){
                        try {
                            resetVariable(file);
                            start();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    //第二种情况，不在索引,而且是最后一个被取走
                    if (acList.size()==0 && !file.exists()){
                        executorService.shutdown();
                        handle.returnAcMap(returnAcMap);
                        isDone = 1;
                    }
                }


            }
        });
        for(MultipleFileReader.StartEndPair pair:startEndPairs){
            System.out.println("分配分片："+pair);
            this.executorService.execute(new MultipleFileReader.SliceReaderTask(pair));
        }
    }

    public int getIsDone(){
        return isDone;
    }

    private void resetVariable(File file) throws FileNotFoundException {
        this.rAccessFile = new RandomAccessFile(file,"r");
        this.fileLength = file.length();
        startEndPairs = new HashSet<StartEndPair>();
    }

    private void calculateStartEnd(long start,long size) throws IOException{
        if(start>fileLength-1){
            return;
        }
        MultipleFileReader.StartEndPair pair = new MultipleFileReader.StartEndPair();
        pair.start=start;
        long endPosition = start+size-1;
        if(endPosition>=fileLength-1){
            pair.end=fileLength-1;
            startEndPairs.add(pair);
            return;
        }

        rAccessFile.seek(endPosition);
        byte tmp =(byte) rAccessFile.read();
        while(tmp!='\n' && tmp!='\r'){
            endPosition++;
            if(endPosition>=fileLength-1){
                endPosition=fileLength-1;
                break;
            }
            rAccessFile.seek(endPosition);
            tmp =(byte) rAccessFile.read();
        }
        pair.end=endPosition;
        startEndPairs.add(pair);

        calculateStartEnd(endPosition+1, size);

    }

    public void shutdown(){
        try {
            this.rAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executorService.shutdown();
    }
    private void handle(byte[] bytes) throws IOException, InterruptedException {
        String line = null;
        if(this.charset==null){
            line = new String(bytes);
        }else{
            line = new String(bytes,charset);
        }
        if(line!=null && !"".equals(line)){
            this.handle.handle(line,returnAcMap,curList);
            counter.incrementAndGet();
        }
    }
    private static class StartEndPair{
        public long start;
        public long end;

        @Override
        public String toString() {
            return "star="+start+";end="+end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MultipleFileReader.StartEndPair other = (MultipleFileReader.StartEndPair) obj;
            if (end != other.end)
                return false;
            if (start != other.start)
                return false;
            return true;
        }

    }
    private class SliceReaderTask implements Runnable{
        private long start;
        private long sliceSize;
        private byte[] readBuff;


        public SliceReaderTask(MultipleFileReader.StartEndPair pair) {
            this.start = pair.start;
            this.sliceSize = pair.end-pair.start+1;
            this.readBuff = new byte[bufferSize];
        }

        @Override
        public void run() {
            try {
                MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY,start, this.sliceSize);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for(int offset=0;offset<sliceSize;offset+=bufferSize){
                    int readLength;
                    if(offset+bufferSize<=sliceSize){
                        readLength = bufferSize;
                    }else{
                        readLength = (int) (sliceSize-offset);
                    }
                    mapBuffer.get(readBuff, 0, readLength);
                    for(int i=0;i<readLength;i++){
                        byte tmp = readBuff[i];
                        if(tmp=='\n' || tmp=='\r'){
                            handle(bos.toByteArray());
                            bos.reset();
                        }else{
                            bos.write(tmp);
                        }
                    }
                }
                if(bos.size()>0){
                    handle(bos.toByteArray());
                }
                cyclicBarrier.await();//测试性能用
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class Builder{
        private int threadSize=1;
        private String charset=null;
        private int bufferSize=1024*1024*10;
        private Handle handle;
        private File file;
        private List<String> acIdList;
        private List<String> curList;
        private HashMap<String,Account> acMap;
        public Builder(String file,Handle handle){
            this.file = new File(file);
            if(!this.file.exists())
                throw new IllegalArgumentException("文件不存在！");
            this.handle = handle;
        }

        public MultipleFileReader.Builder withTreahdSize(int size){
            this.threadSize = size;
            return this;
        }

        public MultipleFileReader.Builder withCharset(String charset){
            this.charset= charset;
            return this;
        }

        public MultipleFileReader.Builder withBufferSize(int bufferSize){
            this.bufferSize = bufferSize;
            return this;
        }

        public MultipleFileReader.Builder withSetAcIdList(List<String> acIdList){
            this.acIdList = acIdList;
            return this;
        }

        public MultipleFileReader.Builder withSetAcMap(HashMap<String,Account> acMap){
            this.acMap = acMap;
            return this;
        }

        public MultipleFileReader.Builder withSetCurList(List<String> curList){
            this.curList = curList;
            return this;
        }

        public MultipleFileReader build(){
            return new MultipleFileReader(this.file,this.handle,this.charset,this.bufferSize,this.threadSize,this.acIdList,this.acMap,this.curList);
        }
    }


}