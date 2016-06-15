package i.c0d.eu.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by antonio on 15/06/2016.
 */
public class TagResponse {
    @JsonProperty
    private String oldFileName;
    @JsonProperty
    private String newFileName;

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }
}
