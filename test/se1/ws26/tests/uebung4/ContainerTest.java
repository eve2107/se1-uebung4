package se1.ws26.tests.uebung4;

import org.hbrs.se1.ws26.exercises.uebung4.prototype.Container;
import org.hbrs.se1.ws26.exercises.uebung4.prototype.ContainerException;
import org.hbrs.se1.ws26.exercises.uebung4.prototype.PersistenceStrategyStream;
import org.hbrs.se1.ws26.exercises.uebung4.prototype.UserStory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

    private Container container;
    private PersistenceStrategyStream<UserStory> strategy;
    private final String TEST_FILE = "testStories.ser";

    private UserStory story1;
    private UserStory story2;
    private UserStory story3;

    @BeforeEach
    public void setUp() {
        container = Container.getInstance();
        container.deleteAll();

        strategy = new PersistenceStrategyStream<>();
        strategy.setLocation(TEST_FILE);
        container.setPersistenceStrategie(strategy);

        story1 = new UserStory(1, "Login-Funktion", "Login per Mail moeglich", 5, 3, 2, 2, "Coll@HBRS");
        story2 = new UserStory(2, "Profil anlegen", "Profilbild speicherbar", 4, 4, 1, 1, "Coll@HBRS");
        story3 = new UserStory(3, "Passwort-Reset", "Reset-Link per Mail", 2, 1, 3, 3, "TeamMatch");
    }

    @AfterEach
    public void tearDown() {
        container.deleteAll();
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testGlogerPrioBerechnung() {
        assertEquals(2.0, story1.getPrio(), 0.001);
        assertEquals(4.0, story2.getPrio(), 0.001);
        assertEquals(0.5, story3.getPrio(), 0.001);
        assertEquals(0.0, UserStory.berechnePrio(5, 5, 0, 0), 0.001);
    }

    @Test
    public void testAddAndDeleteUserStories() throws ContainerException {
        assertEquals(0, container.size());

        container.addUserStory(story1);
        assertEquals(1, container.size());
        assertEquals(story1, container.getUserStory(1));

        container.addUserStory(story2);
        assertEquals(2, container.size());

        container.deleteAll();
        assertEquals(0, container.size());
        assertNull(container.getUserStory(1));
    }

    @Test
    public void testAddDuplicateUserStoryThrowsException() throws ContainerException {
        container.addUserStory(story1);
        UserStory duplicate = new UserStory(1, "Duplikat", "Kriterium", 3, 3, 2, 1, "Coll@HBRS");

        assertThrows(ContainerException.class, () -> {
            container.addUserStory(duplicate);
        });
        assertEquals(1, container.size());
    }

    @Test
    public void testCompareToAndSorting() throws ContainerException {
        container.addUserStory(story1);
        container.addUserStory(story2);
        container.addUserStory(story3);

        List<UserStory> sortedList = container.getCurrentList().stream()
                .sorted()
                .toList();

        assertEquals(2, sortedList.get(0).getId());
        assertEquals(1, sortedList.get(1).getId());
        assertEquals(3, sortedList.get(2).getId());
    }

    @Test
    public void testStoreAndLoadRoundTrip() throws ContainerException {
        container.addUserStory(story1);
        container.addUserStory(story2);
        assertEquals(2, container.size());

        container.store();

        container.deleteAll();
        assertEquals(0, container.size());

        container.load();
        assertEquals(2, container.size());
        assertNotNull(container.getUserStory(1));
        assertNotNull(container.getUserStory(2));
        assertEquals("Login-Funktion", container.getUserStory(1).getTitel());
        assertEquals(4.0, container.getUserStory(2).getPrio(), 0.001);
    }

    @Test
    public void testStoreWithoutStrategyThrowsException() throws ContainerException {
        container.setPersistenceStrategie(null);
        container.addUserStory(story1);

        assertThrows(ContainerException.class, () -> {
            container.store();
        });
    }

    @Test
    public void testLoadFromNonExistentFileThrowsException() {
        strategy.setLocation("nicht_vorhandene_datei_12345.ser");

        assertThrows(ContainerException.class, () -> {
            container.load();
        });
    }
}