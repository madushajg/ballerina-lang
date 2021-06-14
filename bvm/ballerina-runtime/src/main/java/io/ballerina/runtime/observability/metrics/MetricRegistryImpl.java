/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.runtime.observability.metrics;

import io.ballerina.runtime.observability.metrics.spi.MetricProvider;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * The implementation of {@link MetricRegistry} for keeping metrics by name.
 */
public class MetricRegistryImpl implements MetricRegistry {

    // Metric Provider implementation, which provides actual implementations
    private final MetricProvider metricProvider;
    // Metrics Map by ID
    private final ConcurrentMap<MetricId, Metric> metrics;

    public MetricRegistryImpl(MetricProvider metricProvider) {
        this.metricProvider = metricProvider;
        this.metrics = new ConcurrentHashMap<>();
    }

    @Override
    public Counter counter(MetricId id) {
        return getOrCreate(id, Counter.class, () -> metricProvider.newCounter(id));
    }

    @Override
    public Counter register(Counter counter) {
        return register(counter, Counter.class);
    }

    @Override
    public void unregister(Counter counter) {
        unregister(counter, Counter.class);
    }

    @Override
    public Gauge gauge(MetricId id, StatisticConfig... statisticConfigs) {
        return getOrCreate(id, Gauge.class, () -> metricProvider.newGauge(id, statisticConfigs));
    }

    @Override
    public Gauge register(Gauge gauge) {
        return register(gauge, Gauge.class);
    }

    @Override
    public void unregister(Gauge gauge) {
        unregister(gauge, Gauge.class);
    }

    @Override
    public <T> PolledGauge polledGauge(MetricId id, T obj, ToDoubleFunction<T> valueFunction) {
        return getOrCreate(id, PolledGauge.class, () -> metricProvider.newPolledGauge(id, obj, valueFunction));
    }

    @Override
    public PolledGauge register(PolledGauge gauge) {
        return register(gauge, PolledGauge.class);
    }

    @Override
    public void unregister(PolledGauge gauge) {
        unregister(gauge, PolledGauge.class);
    }

    private <M extends Metric> M getOrCreate(MetricId id, Class<M> metricClass, Supplier<M> metricSupplier) {
        M metric = readMetric(id, metricClass);
        if (metric == null) {
            M newMetric = metricSupplier.get();
            return writeMetricIfNotExists(newMetric, metricClass);
        } else {
            return metric;
        }
    }

    private <M extends Metric> M readMetric(MetricId metricId, Class<M> metricClass) {
        Metric existingMetrics = lookup(metricId);
        if (existingMetrics != null) {
            if (metricClass.isInstance(existingMetrics)) {
                return (M) existingMetrics;
            } else {
                throw new IllegalArgumentException(metricId + " is already used for a different type " +
                        "of metric: " + metricClass.getSimpleName());
            }
        }
        return null;
    }

    private <M extends Metric> M writeMetricIfNotExists(M metric, Class<M> metricClass) {
        final Metric existing = metrics.putIfAbsent(metric.getId(), metric);
        if (existing != null) {
            if (metricClass.isInstance(existing)) {
                return (M) existing;
            } else {
                throw new IllegalArgumentException(metric.getId() + " is already used for a different type of metric: "
                        + metricClass.getSimpleName());
            }
        }
        return metric;
    }

    private <M extends Metric> M register(M registerMetric, Class<M> metricClass) {
        M metric = readMetric(registerMetric.getId(), metricClass);
        if (metric == null) {
            return writeMetricIfNotExists(registerMetric, metricClass);
        } else {
            return metric;
        }
    }

    private <M extends Metric> void unregister(Metric registerMetric, Class<M> metricClass) {
        Metric metric = readMetric(registerMetric.getId(), metricClass);
        if (metric != null) {
            metrics.remove(registerMetric.getId());
        }
    }

    @Override
    public void remove(String name) {
        List<MetricId> ids = metrics.keySet().stream()
                .filter(id -> id.getName().equals(name)).collect(Collectors.toList());
        ids.forEach(metrics::remove);
    }

    @Override
    public MetricProvider getMetricProvider() {
        return metricProvider;
    }

    @Override
    public Metric[] getAllMetrics() {
        return this.metrics.values().toArray(new Metric[this.metrics.values().size()]);
    }

    @Override
    public Metric lookup(MetricId metricId) {
        return metrics.get(metricId);
    }
}
