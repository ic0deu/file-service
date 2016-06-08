package i.c0d.eu.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by antonio on 07/06/2016.
 */
public class UploadResponse {

    class File {
        @JsonProperty
        private String name;
        @JsonProperty
        private Long size;
        @JsonProperty
        private String url;
        @JsonProperty
        private String thumbnailUrl;
        @JsonProperty
        private String deleteUrl;
        @JsonProperty
        private String deleteType;
        @JsonProperty
        private String error;

        public String getName() {
            return name;
        }

        public File setName(String name) {
            this.name = name;
            return this;
        }

        public Long getSize() {
            return size;
        }

        public File setSize(Long size) {
            this.size = size;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public File setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public File setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public String getDeleteUrl() {
            return deleteUrl;
        }

        public File setDeleteUrl(String deleteUrl) {
            this.deleteUrl = deleteUrl;
            return this;
        }

        public String getDeleteType() {
            return deleteType;
        }

        public File setDeleteType(String deleteType) {
            this.deleteType = deleteType;
            return this;
        }

        public String getError() {
            return error;
        }

        public File setError(String error) {
            this.error = error;
            return this;
        }
    }

    @JsonProperty
    private List<File> files=new ArrayList<>();

    @JsonIgnore
    public void addFile(String name, Long size, String url, String thumbnailUrl, String deleteUrl, String deleteType, Optional<String> error) {
        if(error.isPresent()) {
            files.add(new File().setName(name).setSize(size).setError(error.get()));
        } else {
            files.add(new File().setName(name).setSize(size).setDeleteType(deleteType).setDeleteUrl(deleteUrl).setThumbnailUrl(thumbnailUrl).setUrl(url));
        }
    }

}
