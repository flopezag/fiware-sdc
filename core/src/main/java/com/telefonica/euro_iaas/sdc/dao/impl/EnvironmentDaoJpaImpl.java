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

package com.telefonica.euro_iaas.sdc.dao.impl;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.sdc.dao.EnvironmentDao;
import com.telefonica.euro_iaas.sdc.model.Environment;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.EnvironmentSearchCriteria;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/*
 * import com.telefonica.euro_iaas.sdc.model.OS; import com.telefonica.euro_iaas.sdc.model.Product; import
 * com.telefonica.euro_iaas.sdc.model.ProductRelease; import
 * com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductReleaseSearchCriteria;
 */

/**
 * JPA based implementation for Environment
 * 
 * @author Jesus M. Movilla
 */
public class EnvironmentDaoJpaImpl extends AbstractBaseDao<Environment, String> implements EnvironmentDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Environment> findAll() {
        return super.findAll(Environment.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment load(String arg0) throws EntityNotFoundException {
        return super.loadByField(Environment.class, "name", arg0);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Environment> findByCriteria(EnvironmentSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(Environment.class);
        if (criteria.getProductRelease() != null) {
            baseCriteria.add(Restrictions.eq("productRelease", criteria.getProductRelease()));
        }

        List<Environment> environments = setOptionalPagination(criteria, baseCriteria).list();

        return environments;
    }

}
