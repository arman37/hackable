package io.hackable;

@FunctionalInterface
public interface Filter<R> {

    R filter(R r);

}
