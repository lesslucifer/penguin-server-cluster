/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.services;

/**
 *
 * @author Salm
 */
public interface PenguinUpdator
{
    long nextActionTime();
    boolean update();
    void save();
}
