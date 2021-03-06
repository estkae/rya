package org.apache.rya.indexing.mongodb;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Set;

import org.apache.rya.indexing.StatementConstraints;
import org.apache.rya.mongodb.dao.SimpleMongoDBStorageStrategy;
import org.apache.rya.mongodb.document.operators.query.QueryBuilder;
import org.bson.Document;
import org.eclipse.rdf4j.model.IRI;

public class IndexingMongoDBStorageStrategy extends SimpleMongoDBStorageStrategy {
    public Document getQuery(final StatementConstraints contraints) {
        final QueryBuilder queryBuilder = QueryBuilder.start();
        if (contraints.hasSubject()){
            queryBuilder.and(new Document(SUBJECT, contraints.getSubject().toString()));
        }

        if (contraints.hasPredicates()){
            final Set<IRI> predicates = contraints.getPredicates();
            if (predicates.size() > 1){
                for (final IRI pred : predicates){
                    final Document currentPred = new Document(PREDICATE, pred.toString());
                    queryBuilder.or(currentPred);
                }
            }
            else if (!predicates.isEmpty()){
                queryBuilder.and(new Document(PREDICATE, predicates.iterator().next().toString()));
            }
        }
        if (contraints.hasContext()){
            queryBuilder.and(new Document(CONTEXT, contraints.getContext().toString()));
        }
        return queryBuilder.get();
    }
}
