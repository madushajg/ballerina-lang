/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import io.ballerina.runtime.observability.metrics.noop.NoOpCounter;
import io.ballerina.runtime.observability.metrics.noop.NoOpGauge;
import io.ballerina.runtime.observability.metrics.noop.NoOpPolledGauge;
import io.ballerina.runtime.observability.metrics.spi.MetricProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.ToDoubleFunction;

/**
 * Provide No-Op implementations of metrics registry.
 */
public class NoOpMetricRegistry implements MetricRegistry {
    // Metric Provider implementation, which provides actual implementations
    private final MetricProvider metricProvider;
    // Metrics Map by ID
    private final ConcurrentMap<MetricId, Metric> metrics;

    public NoOpMetricRegistry(MetricProvider metricProvider) {
        this.metricProvider = metricProvider;
        this.metrics = new ConcurrentHashMap<>();
    }

    @Override
    public Counter counter(MetricId id) {
        return new NoOpCounter(id);
    }

    @Override
    public Counter register(Counter counter) {
        return register(counter, Counter.class);
    }

    @Override
    public void unregister(Counter counter) {
        // Do nothing
    }

    @Override
    public Gauge gauge(MetricId id, StatisticConfig... statisticConfigs) {
        return new NoOpGauge(id);
    }

    @Override
    public Gauge register(Gauge gauge) {
        return register(gauge, Gauge.class);
    }

    @Override
    public void unregister(Gauge gauge) {
        // Do nothing
    }

    @Override
    public <T> PolledGauge polledGauge(MetricId id, T obj, ToDoubleFunction<T> valueFunction) {
        return new NoOpPolledGauge(id);
    }

    @Override
    public PolledGauge register(PolledGauge gauge) {
        return register(gauge, PolledGauge.class);
    }

    @Override
    public void unregister(PolledGauge gauge) {
        // Do nothing
    }

    private <M extends Metric> M register(M registerMetric, Class<M> metricClass) {
        return (M) new NoOpCounter(registerMetric.getId());
    }

    public void remove(String name) {
        // Do nothing
    }

    public MetricProvider getMetricProvider() {
        return metricProvider;
    }

    public Metric[] getAllMetrics() {
        return this.metrics.values().toArray(new Metric[this.metrics.values().size()]);
    }

    public Metric lookup(MetricId metricId) {
        return metrics.get(metricId);
    }
}
