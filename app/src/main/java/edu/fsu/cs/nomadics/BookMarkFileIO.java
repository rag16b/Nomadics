package edu.fsu.cs.nomadics;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BookMarkFileIO {
    private static final String TAG = "BookMarkFileIO";

    // ----- member data -----
    private static final String FILENAME = "bookmarks.txt";
    private ArrayList< Pair  <String, Coordinate> > bmNames= new ArrayList<>();
    private Context context;

    // ----- public class functions -----
    public BookMarkFileIO (Context context_C){
        context = context_C;

        // load data into array
        FileInputStream fis = null;

        try {
            fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while((line = br.readLine()) != null){
                String[] data = line.split(":");
                String bm = data[0];
                double lon = Double.valueOf(data[1]);
                double lat = Double.valueOf(data[2]);
                Coordinate co = new Coordinate(lon, lat);

                bmNames.add(Pair.create(bm, co));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // adds a bookmark
    public void add(String bMark, double lon, double lat){
        if (exists(bMark) != -1)
            return;
        else {
            Coordinate co = new Coordinate(lon, lat);
            bmNames.add(Pair.create(bMark, co));
            write();
        }
    }

    // deletes a bookmark
    public void delete(String bMark){
        int index = exists(bMark);
        if (index != -1){
            bmNames.remove(index);
            write();
        }
        else
            return;
    }

    // ----- private helper functions -----

    // tells whether a bookmark already exists
    // returns the bookmarks index or -1 in its absence
    private int exists(String bMark){
        for (int i = 0; i < bmNames.size(); i++){
            if (bmNames.get(i).first == bMark)
                return i;
        }
        return -1;
    }

    // write altered list to file
    private void write(){
        // save bmNames contents to a single string line by line
        String bms = new String();
        for (int i = 0; i < bmNames.size(); i++){
            bms.concat(bmNames.get(i).first + ":");
            bms.concat(String.valueOf(bmNames.get(i).second.lon) + ":");
            bms.concat(String.valueOf(bmNames.get(i).second.lat) + "\n");
        }

        // write bms to the file
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(bms.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Private Coordinate Class
    private class Coordinate{
        public double lon;
        public double lat;

        public Coordinate(double lon_C, double lat_C){
            lon = lon_C;
            lat = lat_C;
        }
    }
}
