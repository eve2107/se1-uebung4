package org.hbrs.se1.ws26.exercises.uebung4.prototype;

import java.io.Serializable;

public class UserStory implements Comparable<UserStory>, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titel;
    private String akzeptanzkriterium;
    private int mehrwert;
    private int strafe;
    private int aufwand;
    private int risiko;
    private double prio;
    private String project;

    public UserStory() {
    }

    public UserStory(int id, String titel, String akzeptanzkriterium, int mehrwert, int strafe, int aufwand, int risiko, String project) {
        this.id = id;
        this.titel = titel;
        this.akzeptanzkriterium = akzeptanzkriterium;
        this.mehrwert = mehrwert;
        this.strafe = strafe;
        this.aufwand = aufwand;
        this.risiko = risiko;
        this.project = project;
        this.prio = berechnePrio(mehrwert, strafe, aufwand, risiko);
    }

    public UserStory(int id, String titel, double prio) {
        this.id = id;
        this.titel = titel;
        this.prio = prio;
    }

    public static double berechnePrio(int mehrwert, int strafe, int aufwand, int risiko) {
        if (aufwand + risiko == 0) {
            return 0.0;
        }
        return (double) (mehrwert + strafe) / (aufwand + risiko);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAkzeptanzkriterium() {
        return akzeptanzkriterium;
    }

    public void setAkzeptanzkriterium(String akzeptanzkriterium) {
        this.akzeptanzkriterium = akzeptanzkriterium;
    }

    public int getMehrwert() {
        return mehrwert;
    }

    public void setMehrwert(int mehrwert) {
        this.mehrwert = mehrwert;
    }

    public int getStrafe() {
        return strafe;
    }

    public void setStrafe(int strafe) {
        this.strafe = strafe;
    }

    public int getAufwand() {
        return aufwand;
    }

    public void setAufwand(int aufwand) {
        this.aufwand = aufwand;
    }

    public int getRisiko() {
        return risiko;
    }

    public void setRisiko(int risiko) {
        this.risiko = risiko;
    }

    public double getPrio() {
        return prio;
    }

    public void setPrio(double prio) {
        this.prio = prio;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public int compareTo(UserStory other) {
        return Double.compare(other.getPrio(), this.getPrio());
    }

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-25s | %-8d | %-6d | %-7d | %-6d | %-6.2f | %-15s |",
                id, titel, akzeptanzkriterium, mehrwert, strafe, aufwand, risiko, prio, project);
    }
}