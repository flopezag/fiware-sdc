package com.telefonica.euro_iaas.sdc.manager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.sdc.dao.ApplicationInstanceDao;
import com.telefonica.euro_iaas.sdc.dao.ChefNodeDao;
import com.telefonica.euro_iaas.sdc.manager.impl.ApplicationInstanceManagerChefImpl;
import com.telefonica.euro_iaas.sdc.model.Application;
import com.telefonica.euro_iaas.sdc.model.ApplicationInstance;
import com.telefonica.euro_iaas.sdc.model.ApplicationRelease;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.sdc.model.Product;
import com.telefonica.euro_iaas.sdc.model.ProductInstance;
import com.telefonica.euro_iaas.sdc.model.ProductRelease;
import com.telefonica.euro_iaas.sdc.model.dto.ChefNode;
import com.telefonica.euro_iaas.sdc.model.dto.VM;
import com.telefonica.euro_iaas.sdc.util.RecipeNamingGenerator;
import com.telefonica.euro_iaas.sdc.util.SDCClientUtils;
import com.telefonica.euro_iaas.sdc.util.SystemPropertiesProvider;

public class ApplicationInstanceManagerChefImplTest{

    private SystemPropertiesProvider propertiesProvider;
    private ApplicationInstanceDao applicationInstanceDao;
    private RecipeNamingGenerator recipeNamingGenerator;
    private ChefNodeDao chefNodeDao;
    private SDCClientUtils sdcClientUtils;


    VM vm;
    List<ProductInstance> products;
    Application application;
    ApplicationRelease appRelease;
    ApplicationInstance applicationInstance;

    @Before
    public void setUp() throws Exception {

        recipeNamingGenerator = mock(RecipeNamingGenerator.class);
        when(recipeNamingGenerator.getInstallRecipe(
                any(ApplicationInstance.class))).thenReturn("war::app-p1-p2");

        propertiesProvider = mock(SystemPropertiesProvider.class);


        vm = new VM("ip", "hostname", "domain");

        sdcClientUtils = mock(SDCClientUtils.class);
        sdcClientUtils.execute(vm);


        Product p1 = new Product("p1","description");
        Product p2 = new Product("p2","description");
        ProductRelease pr1 = new ProductRelease(
                "version1", "releaseNotes1", null, p1, null, null);
        ProductRelease pr2 = new ProductRelease(
                "version2", "releaseNotes2", null, p2, null, null);

        products = Arrays.asList(new ProductInstance(pr1,
                ProductInstance.Status.INSTALLED, vm), new ProductInstance(pr2,
                ProductInstance.Status.INSTALLED, vm));
        application = new Application("app", "desc", "war");
        appRelease = new ApplicationRelease(
                "version", "releaseNotes", null, application,
                Arrays.asList(pr1, pr2), null);

        applicationInstance = new ApplicationInstance(appRelease, products,
                Status.INSTALLED);

        applicationInstanceDao = mock(ApplicationInstanceDao.class);
        when(applicationInstanceDao.create(Mockito
           .any(ApplicationInstance.class))).thenReturn(applicationInstance);

        chefNodeDao = mock(ChefNodeDao.class);
        when(chefNodeDao.loadNode(vm))
                .thenReturn(new ChefNode());
        when(chefNodeDao.updateNode((ChefNode)anyObject()))
        .thenReturn(new ChefNode());

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testInstallWhenEverythingIsOk() throws Exception {
        // preparation
        ApplicationInstanceManagerChefImpl manager = new ApplicationInstanceManagerChefImpl();
        manager.setPropertiesProvider(propertiesProvider);
        manager.setApplicationInstanceDao(applicationInstanceDao);
        manager.setRecipeNamingGenerator(recipeNamingGenerator);
        manager.setChefNodeDao(chefNodeDao);
        manager.setSdcClientUtils(sdcClientUtils);
        // execution
        ApplicationInstance installedApp = manager.install(vm, products,
                appRelease, new ArrayList<Attribute>());
        // make assertions
        Assert.assertEquals(installedApp.getApplication(), appRelease);
        Assert.assertEquals(installedApp.getProducts(), products);
        Assert.assertEquals(installedApp.getStatus(), Status.INSTALLED);

        verify(recipeNamingGenerator, times(1)).getInstallRecipe(
                any(ApplicationInstance.class));

        verify(applicationInstanceDao, times(1)).create(
                (Mockito.any(ApplicationInstance.class)));


        verify(chefNodeDao, times(2)).loadNode(vm);
        verify(chefNodeDao, times(2)).updateNode((ChefNode)anyObject());
        verify(sdcClientUtils, times(2)).execute(vm);

    }
}
