/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.httpservices;

import share.Methods;

/**
 *
 * @author LinhTA
 */
public class BillingItem extends TargetedServlet {
    public BillingItem() {
        super(Methods.Http.BILLING_ITEM);
    }
}
