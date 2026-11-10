package org.hbrs.se1.ws26.exercises.uebung4.prototype;

import java.util.List;

public interface PersistenceStrategy<E> {
    public void save(List<E> member) throws ContainerException;
    public List<E> load() throws ContainerException;
}