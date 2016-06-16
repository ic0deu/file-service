package i.c0d.eu.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by antonio on 15/06/2016.
 */
public class TagRequest {

    @JsonProperty
    private String fileName;
    @JsonProperty
    private String tag = "";
    @JsonProperty
    private String newFileName = "";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }
}
