/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services.restores;

import org.json.simple.JSONObject;

/**
 *
 * @author KieuAnh
 */
public interface Restorer {
    void restore(String uid, JSONObject data, long now);
}
