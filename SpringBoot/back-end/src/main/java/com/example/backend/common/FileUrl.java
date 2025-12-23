package com.example.backend.common;

import java.io.File;

public interface FileUrl {
    public String FILEPATH=System.getProperty("user.dir") + File.separator + "files" + File.separator;
    public String IMAGEPATH=System.getProperty("user.dir") + File.separator + "images" + File.separator;
    public String VIDEOPATH=System.getProperty("user.dir") + File.separator + "videos" + File.separator;
}
