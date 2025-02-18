/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spark.bigquery.v2;

import com.google.cloud.spark.bigquery.v2.context.DataSourceWriterContext;
import com.google.cloud.spark.bigquery.v2.context.WriterCommitMessageContext;
import java.util.stream.Stream;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.writer.DataSourceWriter;
import org.apache.spark.sql.sources.v2.writer.DataWriterFactory;
import org.apache.spark.sql.sources.v2.writer.WriterCommitMessage;

public class BigQueryDataSourceWriter implements DataSourceWriter {

  private DataSourceWriterContext ctx;

  public BigQueryDataSourceWriter(DataSourceWriterContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public DataWriterFactory<InternalRow> createWriterFactory() {
    return new Spark24DataWriterFactory(ctx.createWriterContextFactory());
  }

  @Override
  public boolean useCommitCoordinator() {
    return ctx.useCommitCoordinator();
  }

  @Override
  public void onDataWriterCommit(WriterCommitMessage message) {
    ctx.onDataWriterCommit(toWriterCommitMessageContext(message));
  }

  @Override
  public void commit(WriterCommitMessage[] messages) {
    ctx.commit(toWriterCommitMessageContextArray(messages));
  }

  @Override
  public void abort(WriterCommitMessage[] messages) {
    ctx.abort(toWriterCommitMessageContextArray(messages));
  }

  private WriterCommitMessageContext toWriterCommitMessageContext(WriterCommitMessage message) {
    return ((Spark24WriterCommitMessage) message).getContext();
  }

  private WriterCommitMessageContext[] toWriterCommitMessageContextArray(
      WriterCommitMessage[] messages) {
    return Stream.of(messages)
        .map(this::toWriterCommitMessageContext)
        .toArray(WriterCommitMessageContext[]::new);
  }
}
