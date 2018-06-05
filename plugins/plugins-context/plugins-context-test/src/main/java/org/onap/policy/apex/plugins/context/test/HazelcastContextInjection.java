/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.plugins.context.test;

import java.util.Map.Entry;
import java.util.Random;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class HazelcastContextInjection {
    private static final int ONE_SECOND = 1000;
    private static final int SIXTY_SECONDS = 60;
    private static final int MAX_INT_10000 = 10000;

    /**
     * Default constructor is private to avoid subclassing.
     */
    private HazelcastContextInjection() {}

    /**
     * The main method.
     * 
     * @param args the arguments to the method
     * @throws ContextException exceptions thrown on context injection
     */
    public static void main(final String[] args) throws ContextException {
        // For convenience, I created model programmatically, it can of course be read in from a
        // policy model
        final AxContextModel contextModel = createContextModel();

        // The model must be registered in the model service.
        ModelService.registerModel(AxContextSchemas.class, contextModel.getSchemas());
        ModelService.registerModel(AxContextAlbums.class, contextModel.getAlbums());

        // Configure APex to use Hazelcast distribution and locking
        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters()
                .setPluginClass("com.ericsson.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor");
        contextParameters.getLockManagerParameters()
                .setPluginClass("com.ericsson.apex.plugins.context.locking.hazelcast.HazelcastLockManager");

        // Fire up our distribution
        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);
        contextDistributor.init(distributorKey);

        // Now, get a handle on the album
        final ContextAlbum myContextAlbum =
                contextDistributor.createContextAlbum(new AxArtifactKey("LongContextAlbum", "0.0.1"));

        final int jvmID = new Random().nextInt(MAX_INT_10000);
        final String myLongKey = "MyLong_" + jvmID;
        final String commonLongKey = "CommonLong";

        // Put the long value for this JVM into the map
        myContextAlbum.put(myLongKey, new Long(0L));

        // Put the common long value to be used across JVMS in the map if its not htere already
        myContextAlbum.lockForWriting(commonLongKey);
        if (myContextAlbum.get(commonLongKey) == null) {
            myContextAlbum.put(commonLongKey, new Long(0L));
        }
        myContextAlbum.unlockForWriting(commonLongKey);

        // Run for 60 seconds to show multi JVM
        for (int i = 0; i < SIXTY_SECONDS; i++) {
            System.out.println("JVM " + jvmID + " iteration " + i);

            for (final Entry<String, Object> albumEntry : myContextAlbum.entrySet()) {
                myContextAlbum.lockForReading(albumEntry.getKey());
                System.out.println(albumEntry.getKey() + "-->" + albumEntry.getValue());
                myContextAlbum.unlockForReading(albumEntry.getKey());
            }
            System.out.println("old " + myLongKey + ": " + myContextAlbum.get(myLongKey));

            myContextAlbum.lockForReading(commonLongKey);
            System.out.println("old CommonLong: " + myContextAlbum.get(commonLongKey));
            myContextAlbum.unlockForReading(commonLongKey);

            Long myLong = (Long) myContextAlbum.get(myLongKey);
            myLong++;
            myContextAlbum.put(myLongKey, myLong);

            myContextAlbum.lockForWriting(commonLongKey);
            Long commonLong = (Long) myContextAlbum.get(commonLongKey);
            commonLong++;
            myContextAlbum.put(commonLongKey, commonLong);
            myContextAlbum.unlockForWriting(commonLongKey);

            System.out.println("new myLong: " + myContextAlbum.get(myLongKey));
            System.out.println("new commonLong: " + myContextAlbum.get(commonLongKey));

            try {
                Thread.sleep(ONE_SECOND);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        contextDistributor.clear();
    }

    /**
     * This method just creates a simple context model programatically.
     * 
     * @return a context model
     */
    public static AxContextModel createContextModel() {
        final AxContextSchema longSchema =
                new AxContextSchema(new AxArtifactKey("LongSchema", "0.0.1"), "Java", "java.lang.Long");

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(longSchema.getKey(), longSchema);

        final AxContextAlbum longAlbumDefinition = new AxContextAlbum(new AxArtifactKey("LongContextAlbum", "0.0.1"),
                "APPLICATION", true, longSchema.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(longAlbumDefinition.getKey(), longAlbumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("LongContextModel", "0.0.1"), schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }
}
