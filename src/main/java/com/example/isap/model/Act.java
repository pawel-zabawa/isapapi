package com.example.isap.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Act {
    private final String eli;
    private final String title;
    private final String publisher;
    private final String status;
    private final String inForce;
    private final String type;
    private final int year;
    private final int position;
    private final LocalDate promulgationDate;
    private final List<String> keywords;
    private final List<DocumentFile> documents;

    public Act(String eli, String title, String publisher, String status, String inForce, String type, int year, int position,
               LocalDate promulgationDate, List<String> keywords, List<DocumentFile> documents) {
        this.eli = eli;
        this.title = title;
        this.publisher = publisher;
        this.status = status;
        this.inForce = inForce;
        this.type = type;
        this.year = year;
        this.position = position;
        this.promulgationDate = promulgationDate;
        this.keywords = keywords == null ? List.of() : List.copyOf(keywords);
        this.documents = documents == null ? List.of() : List.copyOf(documents);
    }

    public String getEli() {
        return eli;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getInForce() {
        return inForce;
    }

    public String getType() {
        return type;
    }

    public int getYear() {
        return year;
    }

    public int getPosition() {
        return position;
    }

    public LocalDate getPromulgationDate() {
        return promulgationDate;
    }

    public List<String> getKeywords() {
        return Collections.unmodifiableList(keywords);
    }

    public List<DocumentFile> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    public boolean isInForce() {
        return Objects.equals("IN_FORCE", inForce);
    }

    public String getPublisherOrDefault() {
        return publisher == null || publisher.isBlank() ? "MP" : publisher;
    }

    public static class DocumentFile {
        private final String address;
        private final String type;
        private final String fileName;

        public DocumentFile(String address, String type, String fileName) {
            this.address = address;
            this.type = type;
            this.fileName = fileName;
        }

        public String getAddress() {
            return address;
        }

        public String getType() {
            return type;
        }

        public String getFileName() {
            return fileName;
        }

        public String buildDownloadUrl() {
            if (address == null || fileName == null || type == null) {
                return null;
            }
            return String.format("https://isap.sejm.gov.pl/isap.nsf/download.xsp/%s/%s/%s", address, type, fileName);
        }
    }
}
