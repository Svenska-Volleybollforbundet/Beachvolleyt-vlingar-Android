package com.ngusta.cupassist.io;

import android.content.Context;
import com.ngusta.cupassist.activity.MyApplication;

import java.io.*;
import java.util.Collection;

public abstract class Cache<T> {

    protected void save(Collection<T> object, String fileName, Context context) throws IOException {
        FileOutputStream fileOutputStream;
        if (MyApplication.RUN_AS_ANDROID_APP) {
            File file = new File(context.getFilesDir(), fileName + ".data");
            fileOutputStream = new FileOutputStream(file);
        } else {
            fileOutputStream = new FileOutputStream(fileName + ".data");
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
    }

    protected Collection<T> load(String fileName, Context context) {
        try {
            FileInputStream fileInputStream;
            if (MyApplication.RUN_AS_ANDROID_APP) {
                File file = new File(context.getFilesDir(), fileName + ".data");
                fileInputStream = new FileInputStream(file);
            } else {
                fileInputStream = new FileInputStream(fileName + ".data");
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Collection<T>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
