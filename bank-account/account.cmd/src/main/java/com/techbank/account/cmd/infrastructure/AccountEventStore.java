package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.account.cmd.exceptions.AggregateNotFoundException;
import com.techbank.account.cmd.exceptions.ConcurrencyException;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.events.EventModel;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {

    @Autowired
    private EventStoreRepository eventStoreRepository;

    @Autowired
    EventProducer eventProducer;

    @Override
    public void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        var eventStream = this.eventStoreRepository.findByAggregateIdentifier(aggregateId);

        if(expectedVersion != -1 && eventStream.get(eventStream.size() - 1).getVersion() != expectedVersion) {
            throw new ConcurrencyException();
        }

        var version = expectedVersion;

        for(var event : events) {
            version++;
            event.setVersion(version);
            var eventModel = EventModel.builder()
                    .aggregateIdentifier(aggregateId)
                    .eventData(event)
                    .eventType(event.getClass().getTypeName())
                    .aggregateType(AccountAggregate.class.getTypeName())
                    .version(version)
                    .timestamp(new Date()).build();
            var persistedEvent = this.eventStoreRepository.save(eventModel);

            if(!persistedEvent.getId().isEmpty()) {
                this.eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        var eventStream = this.eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if(eventStream == null || eventStream.size() == 0) {
            throw new AggregateNotFoundException("Incorrect Account ID provided!");
        }
        return eventStream.stream().map(e -> e.getEventData()).collect(Collectors.toList());
    }
}
