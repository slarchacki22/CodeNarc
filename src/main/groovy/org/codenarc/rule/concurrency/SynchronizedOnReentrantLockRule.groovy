/*
 * Copyright 2009 the original author or authors.
 * 
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
 */
package org.codenarc.rule.concurrency

import java.util.concurrent.locks.ReentrantLock
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Synchronizing on a ReentrantLock field is almost never the intended usage. A ReentrantLock should be obtained using the lock() method and released in a finally block using the unlock() method. 
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision$ - $Date$
 */
class SynchronizedOnReentrantLockRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedOnReentrantLock'
    int priority = 2
    Class astVisitorClass = SynchronizedOnReentrantLockAstVisitor
}

class SynchronizedOnReentrantLockAstVisitor extends AbstractAstVisitor {

    ClassNode currentClassNode = null

    @Override
    protected void visitClassEx(ClassNode node) {

        currentClassNode = node
        super.visitClassEx(node)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (statement.expression instanceof VariableExpression) {
            if (AstUtil.getFieldType(currentClassNode, statement.expression.variable) == ReentrantLock) {
                addViolation(statement, "Synchronizing on a ReentrantLock field $statement.expression.variable. This is almost never the intended usage; use the lock() and unlock() methods instead")
            }
        }
        super.visitSynchronizedStatement(statement)
    }
}