/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.api.node;

import net.sf.mmm.code.api.block.CodeBlockBody;
import net.sf.mmm.code.api.item.CodeItemWithVariables;

/**
 * {@link CodeNodeItem} representing a function such as a {@link net.sf.mmm.code.api.member.CodeMethod} or a
 * {@link net.sf.mmm.code.api.expression.CodeLambdaExpression}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public interface CodeFunction extends CodeNodeItem, CodeItemWithVariables {

  /**
   * @return the {@link CodeBlockBody} of this function. May be empty (have no
   *         {@link CodeBlockBody#getStatements() statements}) but never {@code null}.
   */
  CodeBlockBody getBody();

}