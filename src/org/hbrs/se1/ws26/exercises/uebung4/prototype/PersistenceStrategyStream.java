package org.hbrs.se1.ws26.exercises.uebung4.prototype;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class PersistenceStrategyStream<E> implements PersistenceStrategy<E> {

    private String location = "allStories.ser";

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void save(List<E> list) throws ContainerException {
        try (FileOutputStream fos = new FileOutputStream(location);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new ContainerException("Fehler beim Abspeichern der Datei: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> load() throws ContainerException {
        try (FileInputStream fis = new FileInputStream(location);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<E>) obj;
            }
            throw new ContainerException("Ausgelesenes Objekt ist keine Liste!");
        } catch (IOException | ClassNotFoundException e) {
            throw new ContainerException("Fehler beim Laden der Datei: " + e.getMessage());
        }
    }
}