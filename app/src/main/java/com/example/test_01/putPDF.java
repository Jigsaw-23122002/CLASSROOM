package com.example.test_01;

public class putPDF {
        public String name;
        public String url;
        public String id;

        public putPDF() {
        }

        public putPDF(String name, String url, String id) {
            this.name = name;
            this.url = url;
            this.id=id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
