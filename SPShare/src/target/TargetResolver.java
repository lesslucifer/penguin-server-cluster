/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package target;

/**
 *
 * @author KieuAnh
 */
public interface TargetResolver {
    Target getUserTarget(String uid);
    Target getMasterTarget();
}