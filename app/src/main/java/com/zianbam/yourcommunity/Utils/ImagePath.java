package com.zianbam.yourcommunity.Utils;

import android.os.Environment;

public class ImagePath {
    public String ROOT_DIR  = Environment.getExternalStorageDirectory().getPath();

    public String Pictures = ROOT_DIR + "/Pictures/";
}
