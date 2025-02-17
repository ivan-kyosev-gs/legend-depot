//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.store.server.guice;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.core.http.guice.BaseModule;
import org.finos.legend.depot.store.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.admin.api.metrics.StorageMetrics;
import org.finos.legend.depot.store.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.store.notifications.domain.QueueManagerConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;

import javax.inject.Named;

public class DepotStoreServerModule extends BaseModule<DepotStoreServerConfiguration>
{

    @Override
    public void configure(Binder binder)
    {
        super.configure(binder);
        binder.bind(ArtifactRepositoryProviderConfiguration.class).toProvider(this::getArtifactRepositoryConfiguration);
        binder.bind(IncludeProjectPropertiesConfiguration.class).toProvider(this::getIncludePropertiesConfiguration);
        binder.bind(ArtifactsRetentionPolicyConfiguration.class).toProvider(this::getRetentionPolicyConfiguration);
        binder.bind(QueueManagerConfiguration.class).toProvider(this::getQueueManagerConfiguration);
    }

    private QueueManagerConfiguration getQueueManagerConfiguration()
    {
        return getConfiguration().getQueueManagerConfiguration() != null ? getConfiguration().getQueueManagerConfiguration() : new QueueManagerConfiguration();
    }

    private IncludeProjectPropertiesConfiguration getIncludePropertiesConfiguration()
    {
        return getConfiguration().getIncludeProjectPropertiesConfiguration();
    }

    private ArtifactsRetentionPolicyConfiguration getRetentionPolicyConfiguration()
    {
        return getConfiguration().getRetentionPolicyConfiguration();
    }

    private ArtifactRepositoryProviderConfiguration getArtifactRepositoryConfiguration()
    {
        ArtifactRepositoryProviderConfiguration configuration = getConfiguration().getArtifactRepositoryProviderConfiguration();
        return (configuration == null) ? ArtifactRepositoryProviderConfiguration.voidConfiguration() : configuration;
    }

    @Provides
    @Singleton
    @Named("storage-metrics")
    boolean scheduleStorageMetrics(SchedulesFactory schedulesFactory, StorageMetrics storageMetrics)
    {
        storageMetrics.init();
        schedulesFactory.register("storage-metrics", 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE,storageMetrics::reportMetrics);
        return true;
    }

}
