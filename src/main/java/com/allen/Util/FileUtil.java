package com.allen.Util;

import com.allen.pojo.Account;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FileUtil<T> {
    public FileUtil() {
    }

    public void Serialize(List<T> lst,Class clazz) throws Exception{
        //将多个对象保存在一个list中，然后将list当作一个对象存入到文件中（list已经实现了Serializable接口）
        File file = null;
        ObjectOutputStream oo = null;
        if(clazz.equals(Account.class)){
            file = new File("file/account.txt");
            oo = new ObjectOutputStream(new FileOutputStream(new File("file/account.txt"),true));
        }
        else{
            file = new File("file/custom.txt");
            oo = new ObjectOutputStream(new FileOutputStream(new File("file/custom.txt"),true));
        }
        if(!file.exists()){
            file.createNewFile();

        }
        oo.writeObject(lst);
        oo.close();
    }

    //反序列化方法
    public List<T> Deserialize(Class clazz) throws Exception{
        File file = null;
        ObjectInputStream oi = null;
        if(clazz.equals(Account.class)){
            file = new File("file/account.txt");
            oi=new ObjectInputStream(new FileInputStream(new File("file/account.txt")));
        }
        else{
            file = new File("file/custom.txt");
            oi=new ObjectInputStream(new FileInputStream(new File("file/custom.txt")));
        }

        List list=(List)oi.readObject();
        return list;
    }
}
