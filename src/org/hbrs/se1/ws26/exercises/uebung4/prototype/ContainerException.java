package org.hbrs.se1.ws26.exercises.uebung4.prototype;

public class ContainerException extends Exception {

	private Integer id;

	public ContainerException(String s) {
		super(s);
	}

	@Override
	public void printStackTrace() {
		if (this.id != null) {
			System.out.println("Das UserStory-Objekt mit der ID " + this.id + " ist bereits vorhanden!");
		} else {
			System.out.println(this.getMessage());
		}
	}

	public void addID(Integer id) {
		this.id = id;
	}
}