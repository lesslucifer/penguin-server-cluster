/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.services;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Salm
 */
public class PenguinUpdatorPQ extends PriorityQueue<PenguinUpdator> {
    public PenguinUpdatorPQ()
    {
        super(new Comparator<PenguinUpdator>() {
            @Override
            public int compare(PenguinUpdator o1, PenguinUpdator o2) {
                return ((o1.nextActionTime() > o2.nextActionTime())?1:(
                        (o1.nextActionTime() == o2.nextActionTime()?0:-1)));
            }
        });
    }
}
