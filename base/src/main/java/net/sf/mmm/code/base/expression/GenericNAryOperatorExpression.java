/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.mmm.code.api.expression.CodeConstant;
import net.sf.mmm.code.api.expression.CodeExpression;
import net.sf.mmm.code.api.expression.CodeNAryOperatorExpression;
import net.sf.mmm.code.api.operator.CodeNAryOperator;
import net.sf.mmm.code.api.language.CodeLanguage;

/**
 * Generic implementation of {@link CodeNAryOperatorExpression}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class GenericNAryOperatorExpression extends GenericOperatorExpression implements CodeNAryOperatorExpression {

  private final CodeNAryOperator operator;

  private final List<CodeExpression> arguments;

  /**
   * The constructor.
   *
   * @param operator the {@link #getOperator() operator}.
   * @param arguments the {@link #getArguments() arguments}.
   */
  public GenericNAryOperatorExpression(CodeNAryOperator operator, CodeExpression... arguments) {

    this(operator, Arrays.asList(arguments));
  }

  /**
   * The constructor.
   *
   * @param operator the {@link #getOperator() operator}.
   * @param arguments the {@link #getArguments() arguments}.
   */
  public GenericNAryOperatorExpression(CodeNAryOperator operator, List<CodeExpression> arguments) {

    super();
    this.operator = operator;
    this.arguments = Collections.unmodifiableList(new ArrayList<>(arguments));
  }

  @Override
  public CodeConstant evaluate() {

    // can only be implemented in language specific sub-class
    return null;
  }

  @Override
  public List<? extends CodeExpression> getArguments() {

    return this.arguments;
  }

  @Override
  public CodeNAryOperator getOperator() {

    return this.operator;
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent, CodeLanguage language) throws IOException {

    String prefix = "";
    for (CodeExpression arg : this.arguments) {
      sink.append(prefix);
      arg.write(sink, newline, defaultIndent, currentIndent);
      if (prefix.isEmpty()) {
        prefix = " " + this.operator.getName() + " ";
      }
    }
  }

}
