/*
 * Copyright 2018, OpenCensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opencensus.spring.brave.old;

import io.opencensus.trace.Annotation;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.EndSpanOptions;
import io.opencensus.trace.Link;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opencensus.trace.TraceOptions;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Map;

public class OpenCensusBraveSpan extends Span {

  private static final EnumSet<Options> recordOptions = EnumSet.of(Options.RECORD_EVENTS);
  private static final EnumSet<Options> notRecordOptions = EnumSet.noneOf(Options.class);

  private static final TraceOptions sampledOptions =
      TraceOptions.builder().setIsSampled(true).build();
  private static final TraceOptions notSampledOptions =
      TraceOptions.builder().setIsSampled(false).build();

  OpenCensusBraveSpan(org.springframework.cloud.sleuth.Span span) {
    super(
        fromSleuthSpan(span),
        Boolean.TRUE.equals(span.isExportable()) ? recordOptions : notRecordOptions);
  }

  @Override
  public void addAnnotation(String s, Map<String, AttributeValue> map) {}

  @Override
  public void addAnnotation(Annotation annotation) {}

  @Override
  public void addLink(Link link) {}

  @Override
  public void end(EndSpanOptions endSpanOptions) {}

  private static SpanContext fromSleuthSpan(org.springframework.cloud.sleuth.Span span) {
    return SpanContext.create(
        TraceId.fromBytes(
            ByteBuffer.allocate(TraceId.SIZE)
                .putLong(span.getTraceIdHigh())
                .putLong(span.getTraceId())
                .array()),
        SpanId.fromBytes(ByteBuffer.allocate(SpanId.SIZE).putLong(span.getSpanId()).array()),
        Boolean.TRUE.equals(span.isExportable()) ? sampledOptions : notSampledOptions);
  }
}
