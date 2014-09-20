/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package httpservices;

import share.Methods;

/**
 *
 * @author KieuAnh
 */
public class WhiteList extends TargetedServlet
{
    public WhiteList() {
        super(Methods.Http.WHITE_LIST);
    }
}
