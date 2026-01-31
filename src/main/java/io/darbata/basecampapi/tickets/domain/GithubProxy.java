package io.darbata.basecampapi.tickets.domain;

import java.util.UUID;

public interface GithubProxy {
    public boolean assignIssueToUser(String userId, UUID project, int issueNumber);
}