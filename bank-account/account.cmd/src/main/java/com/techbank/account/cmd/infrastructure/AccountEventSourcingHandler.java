package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handlers.EventSouringHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventSourcingHandler implements EventSouringHandler<AccountAggregate> {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private EventProducer eventProducer;

    @Override
    public void save(AggregateRoot aggregateRoot) {
        this.eventStore.saveEvents(aggregateRoot.getId(), aggregateRoot.getUncommittedChanges(), aggregateRoot.getVersion());
        aggregateRoot.markChangesAsCommitted();

    }

    @Override
    public AccountAggregate getById(String id) {
        var aggregateRoot = new AccountAggregate();
        var events = this.eventStore.getEvents(id);
        aggregateRoot.replayEvents(events);
        aggregateRoot.setVersion(events.stream().mapToInt(x -> x.getVersion()).max().getAsInt());
        return aggregateRoot;
    }

    @Override
    public void rePublishEvents() {
        var ids = this.eventStore.getAggregateIds();

        for(var id : ids) {
            var aggregateRoot = getById(id);
            if(aggregateRoot == null || !aggregateRoot.getActive()) continue;

            var events = eventStore.getEvents(id);

            for(var event :events) {
                this.eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
    }
}
