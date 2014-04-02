/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

/**
 * 
 */
package com.telefonica.euro_iaas.sdc.manager.async;

import com.telefonica.euro_iaas.sdc.model.Task;

/**
 * @author alberts
 */
public interface NodeAsyncManager {

    /**
     * Delete a NodeClient from ChefServer / PuppetMaster
     * 
     * @param nodeName
     *            the name of the node to be deleted from chef server / puppet master
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the execution will be sent
     */
    void nodeDelete(String vdc, String chefClientname, Task task, String callback);
}