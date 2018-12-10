package model.util;

import java.util.Vector;

public class Observable {
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
