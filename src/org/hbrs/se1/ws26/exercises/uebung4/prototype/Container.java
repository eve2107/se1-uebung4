package org.hbrs.se1.ws26.exercises.uebung4.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Container {

	private List<UserStory> liste = null;
	private static Container instance = new Container();
	final static String LOCATION = "allStories.ser";
	private PersistenceStrategy<UserStory> strategy;

	public static Container getInstance() {
		return instance;
	}

	private Container() {
		liste = new ArrayList<UserStory>();
		PersistenceStrategyStream<UserStory> streamStrategy = new PersistenceStrategyStream<UserStory>();
		streamStrategy.setLocation(LOCATION);
		this.strategy = streamStrategy;
	}

	public void setPersistenceStrategie(PersistenceStrategy<UserStory> strategy) {
		this.strategy = strategy;
	}

	public static void main(String[] args) {
		Container con = Container.getInstance();
		con.startEingabe();
	}

	public void startEingabe() {
		String strInput = null;
		System.out.println("UserStory-Tool V1.3");
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("> ");
			strInput = scanner.nextLine().trim();
			if (strInput.isEmpty()) {
				continue;
			}

			String[] strings = strInput.split(" ");
			String command = strings[0].toLowerCase();

			switch (command) {
				case "help":
					System.out.println("Folgende Befehle stehen zur Verfuegung:");
					System.out.println("  enter            - Eingabe einer neuen User Story");
					System.out.println("  store            - Speichern der User Stories auf Datentraeger");
					System.out.println("  load             - Laden der User Stories vom Datentraeger");
					System.out.println("  dump [projekt]   - Tabellarische Ausgabe (optional gefiltert nach Projekt)");
					System.out.println("  exit             - Verlassen der Anwendung");
					System.out.println("  help             - Anzeige dieser Hilfe");
					break;

				case "dump":
					String projectFilter = (strings.length > 1) ? strings[1] : null;
					startAusgabe(projectFilter);
					break;

				case "enter":
					try {
						System.out.print("ID: ");
						int id = Integer.parseInt(scanner.nextLine().trim());

						System.out.print("Titel: ");
						String titel = scanner.nextLine().trim();

						System.out.print("Akzeptanzkriterium: ");
						String kriterium = scanner.nextLine().trim();

						int mehrwert = leseGlogerWert(scanner, "Mehrwert (1-5): ", false);
						int strafe = leseGlogerWert(scanner, "Strafe (1-5): ", false);
						int aufwand = leseGlogerWert(scanner, "Aufwand (> 0, z.B. Fibonacci): ", true);
						int risiko = leseGlogerWert(scanner, "Risiko (1-5): ", false);

						System.out.print("Projekt: ");
						String project = scanner.nextLine().trim();

						UserStory us = new UserStory(id, titel, kriterium, mehrwert, strafe, aufwand, risiko, project);
						this.addUserStory(us);
						System.out.printf("User Story erfolgreich angelegt (Berechnete Prio: %.2f)%n", us.getPrio());
					} catch (NumberFormatException e) {
						System.out.println("Fehler: Bitte eine gueltige Ganzzahl eingeben!");
					} catch (ContainerException e) {
						e.printStackTrace();
					}
					break;

				case "store":
					try {
						this.store();
					} catch (ContainerException e) {
						e.printStackTrace();
					}
					break;

				case "load":
					try {
						this.load();
					} catch (ContainerException e) {
						e.printStackTrace();
					}
					break;

				case "exit":
					System.out.println("Anwendung wird beendet.");
					scanner.close();
					return;

				default:
					System.out.println("Unbekannter Befehl. Nutzen Sie 'help' fuer eine Uebersicht.");
					break;
			}
		}
	}

	private int leseGlogerWert(Scanner scanner, String prompt, boolean mussGroesserNullSein) {
		while (true) {
			System.out.print(prompt);
			try {
				int wert = Integer.parseInt(scanner.nextLine().trim());
				if (mussGroesserNullSein && wert <= 0) {
					System.out.println("Ungueltiger Wert! Der Wert muss groesser als 0 sein.");
				} else if (!mussGroesserNullSein && wert <= 0) {
					System.out.println("Ungueltiger Wert! Negative Zahlen oder 0 sind nicht erlaubt.");
				} else {
					return wert;
				}
			} catch (NumberFormatException e) {
				System.out.println("Ungueltige Eingabe! Bitte eine ganze Zahl eingeben.");
			}
		}
	}

	public void startAusgabe() {
		startAusgabe(null);
	}

	public void startAusgabe(String projectFilter) {
		if (liste.isEmpty()) {
			System.out.println("Keine User Stories vorhanden.");
			return;
		}

		String trennlinie = "+------+----------------------+---------------------------+----------+--------+---------+--------+--------+-----------------+";
		System.out.println(trennlinie);
		System.out.printf("| %-4s | %-20s | %-25s | %-8s | %-6s | %-7s | %-6s | %-6s | %-15s |%n",
				"ID", "Titel", "Akzeptanzkriterium", "Mehrwert", "Strafe", "Aufwand", "Risiko", "Prio", "Projekt");
		System.out.println(trennlinie);

		liste.stream()
				.filter(story -> projectFilter == null || (story.getProject() != null && story.getProject().equalsIgnoreCase(projectFilter)))
				.sorted()
				.map(UserStory::toString)
				.forEach(System.out::println);

		System.out.println(trennlinie);
	}

	public void store() throws ContainerException {
		if (this.strategy == null) {
			throw new ContainerException("Keine Persistenz-Strategie gesetzt!");
		}
		this.strategy.save(this.liste);
		System.out.println(this.size() + " User Stories wurden erfolgreich gespeichert!");
	}

	public void load() throws ContainerException {
		if (this.strategy == null) {
			throw new ContainerException("Keine Persistenz-Strategie gesetzt!");
		}
		List<UserStory> geladeneListe = this.strategy.load();
		if (geladeneListe != null) {
			this.liste = geladeneListe;
		}
		System.out.println("Es wurden " + this.size() + " User Stories erfolgreich geladen!");
	}

	public void addUserStory(UserStory userStory) throws ContainerException {
		if (contains(userStory)) {
			ContainerException ex = new ContainerException("ID bereits vorhanden!");
			ex.addID(userStory.getId());
			throw ex;
		}
		liste.add(userStory);
	}

	private boolean contains(UserStory userStory) {
		int id = userStory.getId();
		for (UserStory us : liste) {
			if (us.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return liste.size();
	}

	public List<UserStory> getCurrentList() {
		return this.liste;
	}

	public UserStory getUserStory(int id) {
		for (UserStory userStory : liste) {
			if (id == userStory.getId()) {
				return userStory;
			}
		}
		return null;
	}

	public void deleteAll() {
		this.liste.clear();
	}
}