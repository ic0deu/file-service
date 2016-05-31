package i.c0d.eu.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by antonio on 31/05/2016.
 */
public class FileMetadata {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileMetadata{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
