package com.sg.mtfont;

import java.util.ArrayList;

import com.sg.mtfont.bean.FontFile;

public interface IAsyncTaskHandler {

	 public int receiveData(Object o);

	 public int onDataLoadCompleted(ArrayList<FontFile> list);
}
