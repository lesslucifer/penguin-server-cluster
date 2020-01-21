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
public class ZingController extends TargetedServlet {
    public ZingController()
    {
        super(Methods.Http.ZING_CONTROLLER);
    }
}
