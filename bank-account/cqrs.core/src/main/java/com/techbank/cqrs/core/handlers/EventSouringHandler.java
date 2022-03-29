package com.techbank.cqrs.core.handlers;

import com.techbank.cqrs.core.domain.AggregateRoot;

public interface EventSouringHandler<T> {
    void save(AggregateRoot aggregateRoot);
    T getById(String id);
    void rePublishEvents();
}
