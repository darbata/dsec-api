package io.darbata.basecampapi.tickets.domain;

import java.util.List;

public class Contributor {

    private String id;
    private List<Ticket> activeTickets;
    private GithubProxy proxy;

    private Contributor(GithubProxy proxy) {
        this.proxy = proxy;
    }

    public Contributor load(GithubProxy proxy) {
        Contributor contributor = new Contributor(proxy);
        return contributor;
    }

    public void assignTicketToSelf(Ticket ticket) {
        if (activeTickets.size() >= 3) throw new TooManyActiveTicketsException();

        ticket.assignTo(this.id);
        this.activeTickets.add(ticket);
        this.proxy.assignIssueToUser(this.id, ticket.getProjectId(), ticket.getNumber()); // issue number
    }
}
