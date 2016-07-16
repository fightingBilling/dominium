
package com.github.haschi.haushaltsbuch.abfragen;

import org.apache.deltaspike.jpa.api.entitymanager.PersistenceUnitName;
import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@SuppressWarnings("checkstyle:designforextension")
// @Alternative
// @Priority(Interceptor.Priority.APPLICATION + 10)
// @ApplicationScoped
public class EntityManagerProducer {

    @Inject
    @PersistenceUnitName("test")
    private EntityManagerFactory factory;

    @Produces
    @Default
    @RequestScoped
    // @ApplicationScoped
    // @TransactionScoped
    public EntityManager entityManager() {
        return this.factory.createEntityManager();
    }

    public void close(@Disposes @Any final EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}


