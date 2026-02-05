package io.darbata.basecampapi.github;

public enum IssueStatus {
    TODO("Todo"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String value;

    IssueStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}