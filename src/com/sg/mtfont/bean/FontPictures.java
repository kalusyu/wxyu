
package com.sg.mtfont.bean;

import java.io.Serializable;

/**
 * @author Kalus Yu
 */
public class FontPictures implements Serializable {

    String fontFileId;
    String id;
    
    String picType; // big or thumbnail
    String fontPicUrl;
    String picSize;
    String fontPicName;

    public FontPictures() {}

}
