package Vehiclecomputer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Nov 17, 2014
 * Contributors: 
 */
public class RAFBackupTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
//            String data1 = "Test# ";
//            int data2 = 1;
            String data1 = null;
            Integer data2 = null;
                    
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data1);
            oos.flush();
            oos.writeObject(data2);
            byte[] data = bos.toByteArray();
            
            // Write to file
            RandomAccessFile raf = new RandomAccessFile("raf_bu_test", "rw");
            raf.write(data);
            raf.close();
            
            RandomAccessFile rafIn = new RandomAccessFile("raf_bu_test", "r");
            byte[] buff = new byte[256];
            rafIn.read(buff);
            ByteArrayInputStream bis = new ByteArrayInputStream(buff);
            ObjectInputStream ois = new ObjectInputStream(bis);
            
            String res1 = (String) ois.readObject();
            int res2 = (Integer) ois.readObject();
            
            System.out.println(res1 + res2);
            
    }
}
