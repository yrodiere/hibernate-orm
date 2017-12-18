/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.resource.beans.internal;

import javax.enterprise.inject.spi.BeanManager;

import org.hibernate.resource.beans.spi.AbstractManagedBeanRegistry;
import org.hibernate.resource.beans.spi.ManagedBean;

import org.jboss.logging.Logger;

/**
 * A CDI-based ManagedBeanRegistry for Hibernate delaying access to the
 * BeanManager until first need.
 *
 * @see ManagedBeanRegistryCdiExtendedImpl
 * @see ManagedBeanRegistryCdiStandardImpl
 *
 * @author Steve Ebersole
 */
public class ManagedBeanRegistryCdiDelayedImpl extends AbstractManagedBeanRegistry {
	private static final Logger log = Logger.getLogger( ManagedBeanRegistryCdiDelayedImpl.class );

	private final BeanManager beanManager;

	private ManagedBeanRegistryCdiDelayedImpl(BeanManager beanManager) {
		this.beanManager = beanManager;
		log.debugf( "Delayed access requested to CDI BeanManager" );
	}

	@Override
	protected <T> ManagedBean<T> createBean(Class<T> beanClass, boolean shouldRegistryManageLifecycle) {
		return new ManagedBeanImpl<>( beanClass,
				Helper.INSTANCE.getLifecycleManagementStrategy( shouldRegistryManageLifecycle ) );
	}

	@Override
	protected <T> ManagedBean<T> createBean(String beanName, Class<T> beanContract, boolean shouldRegistryManageLifecycle) {
		return new NamedManagedBeanImpl<>( beanName, beanContract,
				Helper.INSTANCE.getLifecycleManagementStrategy( shouldRegistryManageLifecycle ) );
	}

	private class ManagedBeanImpl<T> implements ManagedBean<T> {
		private final Class<T> beanClass;
		private final CdiLifecycleManagementStrategy strategy;

		private ManagedBean<T> delegate = null;

		ManagedBeanImpl(Class<T> beanClass, CdiLifecycleManagementStrategy strategy) {
			this.beanClass = beanClass;
			this.strategy = strategy;
		}

		@Override
		public Class<T> getBeanClass() {
			return beanClass;
		}

		@Override
		public T getBeanInstance() {
			if ( delegate == null ) {
				initialize();
			}
			return delegate.getBeanInstance();
		}

		private void initialize() {
			log.debugf( "Delayed initialization of CDI bean on first use : %s", beanClass.getName() );

			delegate = strategy.createBean( beanManager, beanClass );
		}

		@Override
		public void release() {
			if ( delegate == null ) {
				log.debugf( "Skipping release for (delayed) CDI bean [%s] as it was not initialized", beanClass.getName() );
				return;
			}

			log.debugf( "Releasing (delayed) CDI bean : %s", beanClass.getName() );

			delegate.release();
			delegate = null;
		}
	}

	private class NamedManagedBeanImpl<T> implements ManagedBean<T> {
		private final String beanName;
		private final Class<T> beanContract;
		private final CdiLifecycleManagementStrategy strategy;

		private ManagedBean<T> delegate = null;

		NamedManagedBeanImpl(String beanName, Class<T> beanContract, CdiLifecycleManagementStrategy strategy) {
			this.beanName = beanName;
			this.beanContract = beanContract;
			this.strategy = strategy;
		}

		@Override
		public Class<T> getBeanClass() {
			return beanContract;
		}

		@Override
		public T getBeanInstance() {
			if ( delegate == null ) {
				initialize();
			}
			return delegate.getBeanInstance();
		}

		private void initialize() {
			log.debugf( "Delayed initialization of CDI bean on first use : [%s : %s]", beanName, beanContract.getName() );

			delegate = strategy.createBean( beanManager, beanName, beanContract );
		}

		@Override
		public void release() {
			if ( delegate == null ) {
				log.debugf( "Skipping release for (delayed) CDI bean [%s : %s] as it was not initialized", beanName, beanContract.getName() );
				return;
			}

			log.debugf( "Releasing (delayed) CDI bean [%s : %s]", beanName, beanContract.getName() );

			delegate.release();
			delegate = null;
		}
	}

}
