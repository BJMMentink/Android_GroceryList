package com.example.grocerylist;
import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileManager {

    private static final String TAG = "FileManager";
    public static void writeToFile(Context context, String filename, ArrayList<String> data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            for (String line : data) {
                outputStreamWriter.write(line + "\n");
            }
            outputStreamWriter.close();
            Log.d(TAG, "Data written to file: " + filename);
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file: " + filename, e);
        }
    }
    public static ArrayList<String> readFromFile(Context context, String filename) {
        ArrayList<String> data = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(filename);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    data.add(line);
                }
                inputStream.close();
                Log.d(TAG, "Data read from file: " + filename);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + filename, e);
        } catch (IOException e) {
            Log.e(TAG, "Error reading from file: " + filename, e);
        }
        return data;
    }
}
