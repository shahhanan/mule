/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.osgi;

import java.util.Dictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class MuleBundleListener implements BundleActivator
{
    /** Manifest entry name for listing Mule config files */
    public static final String MULE_CONFIG_HEADER = "Mule-Config";
    
    /** The id of the listener bundle itself */
    protected long bundleId;
    
    /** Bundle listener interested in Mule configs */
    private SynchronousBundleListener muleConfigListener;
    
    private static Log logger = LogFactory.getLog(MuleBundleListener.class);
    
    private class MuleConfigListener implements SynchronousBundleListener
    {
        public void bundleChanged(BundleEvent event)
        {
            Bundle bundle = event.getBundle();

            // ignore current bundle 
            if (bundle.getBundleId() == bundleId)
            {
                return;
            }

            switch (event.getType())
            {
                case BundleEvent.STARTED :
                {
                    String configResources = getMuleConfigResources(bundle);
                    logger.info("New Mule configuration detected: " + configResources);
                    break;
                }
                case BundleEvent.STOPPING :
                {
                    // TODO
                    break;
                }
                default :
                    break;
            }
        }
    }

    public void start(BundleContext context) throws Exception
    {
        this.bundleId = context.getBundle().getBundleId();
        
        muleConfigListener = new MuleConfigListener();
        // listen to any changes in bundles
        context.addBundleListener(muleConfigListener);
    }

    public void stop(BundleContext context) throws Exception
    {
        if (muleConfigListener != null)
        {
            context.removeBundleListener(muleConfigListener);
            muleConfigListener = null;
        }
    }

    protected String getMuleConfigResources(Bundle bundle)
    {
        Dictionary headers = bundle.getHeaders();
        Object header = null;
        if (headers != null)
        {
            header = headers.get(MULE_CONFIG_HEADER);
        }
        return (header != null ? header.toString() : null);
    }
}
