package model.util;

import java.util.Vector;

public class Observable {
    /**
     * Used to update the UI when any changes occur in the repo or any of the program states
     * The program states are the base observables and they notify the repo (which is both an observer and observable) which in turn notifies the observers
     * specified to the execution controller
     */
    private Vector<Observer> observers;

    protected Observable() {
        observers = new Vector<>();
    }

    public void registerObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    protected void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}
