/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

/**
 *
 * @author KieuAnh
 */
public class LoginDayRecord implements QuestRecord
{
    private final long loginTime;

    public LoginDayRecord(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getLoginTime() {
        return loginTime;
    }
}
