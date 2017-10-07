/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.block;

import java.io.IOException;
import java.util.List;

import net.sf.mmm.code.api.block.CodeBlock;
import net.sf.mmm.code.api.block.CodeBlockDoWhile;
import net.sf.mmm.code.api.block.CodeBlockIf;
import net.sf.mmm.code.api.expression.CodeCondition;
import net.sf.mmm.code.api.node.CodeNodeItemWithGenericParent;
import net.sf.mmm.code.api.statement.CodeStatement;

/**
 * Generic implementation of {@link CodeBlockDoWhile}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class GenericBlockIf extends GenericBlockStatementWithCondition implements CodeBlockIf, CodeNodeItemWithGenericParent<CodeBlock, GenericBlockIf> {

  private GenericBlockIf elseIf;

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   * @param condition the {@link #getCondition() condition}.
   * @param statements the {@link #getStatements() statements}.
   */
  public GenericBlockIf(CodeBlock parent, CodeCondition condition, CodeStatement... statements) {

    super(parent, condition, statements);
  }

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   * @param condition the {@link #getCondition() condition}.
   * @param statements the {@link #getStatements() statements}.
   */
  public GenericBlockIf(CodeBlock parent, CodeCondition condition, List<CodeStatement> statements) {

    super(parent, condition, statements);
  }

  /**
   * The copy-constructor.
   *
   * @param parent the {@link #getParent() parent}.
   * @param template the {@link GenericBlockStatementWithCondition} to copy.
   */
  public GenericBlockIf(GenericBlockStatementWithCondition template, CodeBlock parent) {

    super(template, parent);
  }

  @Override
  public CodeBlockIf getElse() {

    return this.elseIf;
  }

  @Override
  public GenericBlockIf copy() {

    return copy(getParent());
  }

  @Override
  public GenericBlockIf copy(CodeBlock newParent) {

    return new GenericBlockIf(this, newParent);
  }

  @Override
  protected void writePrefix(Appendable sink, String newline, String defaultIndent, String currentIndent) throws IOException {

    CodeCondition condition = getCondition();
    if (condition != null) {
      sink.append("if (");
      condition.write(sink, newline, defaultIndent, currentIndent);
      sink.append(") ");
    }
  }

  @Override
  protected void writeSuffix(Appendable sink, String newline, String defaultIndent, String currentIndent) throws IOException {

    if (this.elseIf == null) {
      super.writeSuffix(sink, newline, defaultIndent, currentIndent);
    } else {
      sink.append(" else ");
      this.elseIf.write(sink, newline, defaultIndent, currentIndent);
    }
  }

}