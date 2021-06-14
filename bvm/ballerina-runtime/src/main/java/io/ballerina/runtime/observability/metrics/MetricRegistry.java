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

import io.ballerina.runtime.observability.metrics.spi.MetricProvider;

import java.util.function.ToDoubleFunction;


/**
 * This represents the interface for metrics registry to be used with {@link DefaultMetricRegistry}.
 */
public interface MetricRegistry {

    /**
     * Use {@link Counter#builder(String)}.
     *
     * @param id The {@link MetricId}.
     * @return A existing or a new {@link Counter} metric.
     */
    Counter counter(MetricId id);

    /**
     * Registers the counter metrics instance.
     *
     * @param counter The {@link Counter} instance.
     * @return A existing or a newly registered {@link Counter} metric.
     */
    Counter register(Counter counter);

    /**
     * Unregister the counter metrics instance.
     *
     * @param counter The {@link Counter} instance.
     */
    void unregister(Counter counter);

    /**
     * Use {@link Gauge#builder(String)}.
     *
     * @param id               The {@link MetricId}.
     * @param statisticConfigs {@link StatisticConfig statistic configurations} to summarize gauge values.
     * @return A existing or a new {@link Gauge} metric.
     */
    Gauge gauge(MetricId id, StatisticConfig... statisticConfigs);

    /**
     * Registers the gauge metrics instance.
     *
     * @param gauge The {@link Gauge} instance.
     * @return A existing or a newly registered {@link Gauge} metric.
     */
    Gauge register(Gauge gauge);

    /**
     * Unregister the gauge metrics instance.
     *
     * @param gauge The {@link Gauge} instance.
     */
    void unregister(Gauge gauge);

    /**
     * Use {@link PolledGauge#builder(String, Object, ToDoubleFunction)}.
     *
     * @param id            The {@link MetricId}.
     * @param obj           State object used to compute a value.
     * @param valueFunction Function that produces an instantaneous gauge value from the state object.
     * @param <T>           The type of the state object from which the gauge value is extracted.
     * @return A existing or a new {@link PolledGauge} metric.
     */
    <T> PolledGauge polledGauge(MetricId id, T obj, ToDoubleFunction<T> valueFunction);

    /**
     * Registers the polled gauge metrics instance.
     *
     * @param gauge The {@link PolledGauge} instance.
     * @return A existing or a newly registered {@link PolledGauge} metric.
     */
    PolledGauge register(PolledGauge gauge);

    /**
     * Unregisters the polled gauge metrics instance.
     *
     * @param gauge The {@link PolledGauge} instance.
     */
    void unregister(PolledGauge gauge);

    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     */
    void remove(String name);

    MetricProvider getMetricProvider();

    Metric[] getAllMetrics();

    Metric lookup(MetricId metricId);
}
