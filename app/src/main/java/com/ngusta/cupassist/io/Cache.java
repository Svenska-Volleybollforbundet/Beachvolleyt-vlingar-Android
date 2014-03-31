package com.ngusta.cupassist.io;

import com.ngusta.cupassist.activity.MyApplication;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Cache<T> {

    protected void save(Object object, String fileName, Context context) throws IOException {
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

    protected Object load(String fileName, Context context) {
        try {
            FileInputStream fileInputStream;
            if (MyApplication.RUN_AS_ANDROID_APP) {
                File file = new File(context.getFilesDir(), fileName + ".data");
                fileInputStream = new FileInputStream(file);
            } else {
                fileInputStream = new FileInputStream(fileName + ".data");
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return objectInputStream.readObject();
        } catch (InvalidClassException e) {
            System.out.println("Couldn't use cache.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
