/**
 *   (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights
 *   Reserved.
 * 
 *   The copyright to the software program(s) is property of Telefonica I+D.
 *   The program(s) may be used and or copied only with the express written
 *   consent of Telefonica I+D or in accordance with the terms and conditions
 *   stipulated in the agreement/contract under which the program(s) have
 *   been supplied.
 */

package com.telefonica.euro_iaas.sdc.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.telefonica.euro_iaas.sdc.manager.ProductManager;
import com.telefonica.euro_iaas.sdc.manager.async.ProductInstanceAsyncManager;
import com.telefonica.euro_iaas.sdc.manager.async.TaskManager;
import com.telefonica.euro_iaas.sdc.model.Artifact;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.sdc.model.OS;
import com.telefonica.euro_iaas.sdc.model.Product;
import com.telefonica.euro_iaas.sdc.model.ProductInstance;
import com.telefonica.euro_iaas.sdc.model.ProductRelease;
import com.telefonica.euro_iaas.sdc.model.Task;
import com.telefonica.euro_iaas.sdc.model.dto.ProductInstanceDto;
import com.telefonica.euro_iaas.sdc.model.dto.ReleaseDto;
import com.telefonica.euro_iaas.sdc.model.dto.VM;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductInstanceSearchCriteria;
import com.telefonica.euro_iaas.sdc.rest.resources.ProductInstanceResourceImpl;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductInstanceResourceImplTest {

    public static String VDC = "vdc";
    public static String HREF = "href";

    ProductRelease pr1 = null;
    ProductInstanceDto productInstance = null;
    ProductManager productManager = null;
    ProductInstanceResourceImpl productInstanceResource = null;
    ProductInstanceAsyncManager productInstanceAsyncManager = null;
    Product product = null;

    @Before
    public void setUp() throws Exception

    {
        productInstanceResource = new ProductInstanceResourceImpl();

        productManager = mock(ProductManager.class);
        productInstanceAsyncManager = mock(ProductInstanceAsyncManager.class);
        TaskManager taskManager = mock(TaskManager.class);
        productInstanceResource.setProductManager(productManager);
        productInstanceResource.setProductInstanceAsyncManager(productInstanceAsyncManager);
        productInstanceResource.setTaskManager(taskManager);

        ReleaseDto productReleaseDto = new ReleaseDto();
        productReleaseDto.setName("Product::server");
        productReleaseDto.setType("type");
        productReleaseDto.setVersion("Product::version");

        Attribute att = new Attribute("key1", "value1", "description1");
        java.util.List<Attribute> atts = new ArrayList<Attribute>();
        atts.add(att);

        Artifact artifact2 = new Artifact();
        artifact2.addAttribute(att);

        VM vm = new VM("ip", "hostname", "domain");
        productInstance = new ProductInstanceDto();
        productInstance.setVm(vm);
        productInstance.setProduct(productReleaseDto);
        productInstance.setVdc(VDC);

        OS os = new OS("os1", "1", "os1 description", "v1");
        product = new Product("Product::server", "Product::version");
        ProductRelease productRelease = new ProductRelease("version", "releaseNotes", null, product, Arrays.asList(os),
                null);
        ProductInstance productIns = new ProductInstance(productRelease, Status.INSTALLED, vm, VDC);
        Task task = new Task();
        task.setHref("href");
        List<ProductInstance> lProductInstance = new ArrayList<ProductInstance>();
        lProductInstance.add(productIns);

        when(productManager.load(any(String.class))).thenReturn(product);
        when(taskManager.createTask(any(Task.class))).thenReturn(task);
        when(productInstanceAsyncManager.load(any(String.class), any(String.class))).thenReturn(productIns);
        when(productInstanceAsyncManager.findByCriteria(any(ProductInstanceSearchCriteria.class))).thenReturn(
                lProductInstance);
        doNothing().when(productInstanceAsyncManager).install(any(VM.class), any(String.class),
                any(ProductRelease.class), any(List.class), any(Task.class), any(String.class));

    }

    @Test
    public void testInstallProduct() throws Exception {

        String callback = "";
        Task task = productInstanceResource.install(VDC, productInstance, callback);
        assertEquals(task.getHref(), HREF);

    }

    @Test
    public void testUnInstallProduct() throws Exception {

        String callback = "";
        Task task = productInstanceResource.uninstall(VDC, "name", callback);
        assertEquals(task.getHref(), HREF);

    }

    @Test
    public void testListProduct() throws Exception {

        List<ProductInstance> pProductInstance = productInstanceResource.findAll("hostname", "domain", "ip", "fqn",
                null, null, null, null, Status.INSTALLED, VDC, "Product::server");
        assertEquals(pProductInstance.size(), 1);

    }
}
