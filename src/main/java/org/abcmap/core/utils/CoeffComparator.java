package org.abcmap.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Simple utility which allow to sort misc object with a numerical coefficient
 *
 * @author remipassmoilesel
 */
public class CoeffComparator implements Comparator<CoeffComparator.ComparableObject> {

    public static class ComparableObject {
        private Object obj;
        private Integer coeff;

        public ComparableObject(Object obj, Integer coeff) {
            this.obj = obj;
            this.coeff = coeff;
        }

        public void addToCoeff(Integer integer) {
            coeff += integer;
        }

        public Object getObject() {
            return obj;
        }

        public Integer getCoeff() {
            return coeff;
        }

        public void setCoeff(Integer coeff) {
            this.coeff = coeff;
        }
    }

    public enum Order {
        ASCENDING, DESCENDING
    }

    private Order order;

    public CoeffComparator(Order o) {
        this.order = o;
    }

    @Override
    public int compare(ComparableObject o1, ComparableObject o2) {

        if (o1.getCoeff() == o2.getCoeff()) {
            return 0;
        }

        if (Order.ASCENDING.equals(order)) {
            return o1.getCoeff() > o2.getCoeff() ? 1 : -1;
        } else {
            return o1.getCoeff() < o2.getCoeff() ? 1 : -1;
        }

    }

    public static void sort(List<ComparableObject> list) {
        Collections.sort(list, new CoeffComparator(Order.ASCENDING));
    }

    public static void sort(List<ComparableObject> list, Order o) {
        Collections.sort(list, new CoeffComparator(o));
    }

    public static ComparableObject getComparableFor(Object o) {
        return getComparableFor(o, 0);
    }

    public static ComparableObject getComparableFor(Object o, Integer i) {
        return new ComparableObject(o, i);
    }

    public static ArrayList<ComparableObject> getNewList() {
        return new ArrayList<ComparableObject>(50);
    }

}
