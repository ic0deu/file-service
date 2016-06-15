package i.c0d.eu.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 15/06/2016.
 */
public class FileListResponse {

    @JsonProperty
    private List<String> files = new ArrayList<>();

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
