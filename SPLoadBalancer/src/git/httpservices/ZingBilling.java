/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.httpservices;

import git.httpservices.TargetedServlet;
import share.Methods;

/**
 *
 * @author LinhTA
 */
public class ZingBilling extends TargetedServlet {
    public ZingBilling()
    {
        super(Methods.Http.ZING_BILLING);
    }
}
