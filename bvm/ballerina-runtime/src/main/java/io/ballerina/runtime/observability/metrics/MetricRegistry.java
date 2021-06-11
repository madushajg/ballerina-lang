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

import java.util.function.ToDoubleFunction;


public interface MetricRegistry {


    Counter counter(MetricId id);

    Counter register(Counter counter);

    void unregister(Counter counter);

    Gauge gauge(MetricId id, StatisticConfig... statisticConfigs);

    Gauge register(Gauge gauge);

    void unregister(Gauge gauge);

    <T> PolledGauge polledGauge(MetricId id, T obj, ToDoubleFunction<T> valueFunction);

    PolledGauge register(PolledGauge gauge);

    void unregister(PolledGauge gauge);

    void remove(String name);

    MetricProvider getMetricProvider();

    Metric[] getAllMetrics();

    Metric lookup(MetricId metricId);
}
