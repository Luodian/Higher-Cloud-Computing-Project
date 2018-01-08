package com.Utility.IgniteUtl;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by 王烨臻 on 2018/1/6.
 */
public class ArrayListSerializaion<E> extends ArrayList<E> implements Externalizable {

    public ArrayListSerializaion() {
        super();
    }

    public ArrayListSerializaion(@NotNull Collection<? extends E> collection) {
        super(collection);
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
       objectOutput.writeInt(size());
       for (int i = 0;i<size();i++){
           if (get(i) instanceof  Serializable){
               objectOutput.writeObject(get(i));
           }
           else objectOutput.writeObject(null);
       }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
            int elementCount = objectInput.readInt();
            this.ensureCapacity(elementCount);
            for (int i = 0; i<elementCount;i++){
                this.add((E)objectInput.readObject());
            }
    }
}
