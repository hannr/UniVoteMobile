package ch.bfh.univote.registration;
import java.io.*;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

/**
 * Component for reading/writing from / to a file on an android device.
 * @author Raphael Hänni
 */
public class IOUtil {

    public static void writeFile(String content, String filename, Context context) throws IOException {
        // File creation mode: the default mode, where the created file can only be accessed by the calling application (or all applications sharing the same user ID).
        // FileOutputStream weil wir die random Bytes aus der AES Encryption ins File schreiben wollen und nicht ein "normaler" text
        // ansonsten würde man FileWriter nehmen
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.close();
    }


    public static String readFromFile(String filename, Context context) throws IOException {
        FileInputStream fin = context.openFileInput(filename);
        int c;
        String temp="";
        while( (c = fin.read()) != -1){
            temp = temp + Character.toString((char)c);
        }
        //string temp contains all the data of the file.
        fin.close();
        return temp;
    }
}