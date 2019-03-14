package com.zianbam.yourcommunity.Utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    //search folder from storage
    public static ArrayList<String> getFolderPath(String folder){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(folder);
        File[] listfiles = file.listFiles();
        for (int i = 0; 1<listfiles.length; i++){
            if (listfiles[i].isDirectory()){
                pathArray.add((listfiles[i].getAbsolutePath()));
            }
        }
        return pathArray;
    }

    //search filrs from folder
    public static ArrayList<String> getfilePath(String folder){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(folder);
        File[] listfiles = file.listFiles();
        for (int i = 0; i<listfiles.length; i++){
            if (listfiles[i].isFile()){
                pathArray.add((listfiles[i].getAbsolutePath()));
            }
        }
        return pathArray;
    }
}
