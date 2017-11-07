/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.expression;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sf.mmm.code.api.arg.CodeParameter;
import net.sf.mmm.code.api.expression.CodeExpression;
import net.sf.mmm.code.api.expression.CodeMethodInvocation;
import net.sf.mmm.code.api.member.CodeMethod;
import net.sf.mmm.code.api.language.CodeLanguage;
import net.sf.mmm.code.api.type.CodeGenericType;

/**
 * Base implementation of {@link CodeMethodInvocation}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class BaseMethodInvocation extends GenericMemberReference implements CodeMethodInvocation {

  private final CodeGenericType type;

  private final CodeMethod method;

  private final List<? extends CodeGenericType> typeParameters;

  private final List<? extends CodeExpression> arguments;

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   */
  public BaseMethodInvocation(CodeMethod method) {

    this(method, Collections.emptyList(), null, null, null);
    List<? extends CodeParameter> parameters = method.getParameters().getDeclared();
    int size = parameters.size();
    if ((size > 1) || ((size == 1) && !parameters.get(0).isVarArgs())) {
      throw new IllegalArgumentException(method.toString());
    }
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   */
  public BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments) {

    this(method, arguments, null, null, null);
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   * @param type the optional {@link #getType() type}.
   */
  public BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments, CodeGenericType type) {

    this(method, arguments, null, type, null);
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   * @param type the optional {@link #getType() type}.
   * @param typeParameters the optional {@link #getTypeParameters() type parameters}.
   */
  public BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments, CodeGenericType type,
      List<? extends CodeGenericType> typeParameters) {

    this(method, arguments, null, type, typeParameters);
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   * @param expression the optional {@link #getExpression() expression}.
   */
  public BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments, CodeExpression expression) {

    this(method, arguments, expression, null, null);
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   * @param expression the optional {@link #getExpression() expression}.
   * @param typeParameters the optional {@link #getTypeParameters() type parameters}.
   */
  public BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments, CodeExpression expression,
      List<? extends CodeGenericType> typeParameters) {

    this(method, arguments, expression, null, typeParameters);
  }

  /**
   * The constructor.
   *
   * @param method the {@link #getMember() method} to invoke.
   * @param arguments the {@link #getArguments() arguments} for the method invocation.
   * @param expression the optional {@link #getExpression() expression}.
   * @param type the optional {@link #getType() type}.
   * @param typeParameters the optional {@link #getTypeParameters() type parameters}.
   */
  protected BaseMethodInvocation(CodeMethod method, List<? extends CodeExpression> arguments, CodeExpression expression, CodeGenericType type,
      List<? extends CodeGenericType> typeParameters) {

    super(expression);
    Objects.requireNonNull(method, "method");
    this.method = method;
    this.arguments = Collections.unmodifiableList(arguments);
    this.type = type;
    if (typeParameters == null) {
      this.typeParameters = null;
    } else {
      this.typeParameters = Collections.unmodifiableList(typeParameters);
    }
  }

  @Override
  public List<? extends CodeExpression> getArguments() {

    return this.arguments;
  }

  @Override
  public List<? extends CodeGenericType> getTypeParameters() {

    return this.typeParameters;
  }

  @Override
  public CodeGenericType getType() {

    return this.type;
  }

  @Override
  public CodeMethod getMember() {

    return this.method;
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent, CodeLanguage language) throws IOException {

    super.doWrite(sink, newline, defaultIndent, currentIndent, language);
    if (this.type != null) {
      this.type.writeReference(sink, false);
      sink.append('.');
    }
    if (this.typeParameters != null) {
      String prefix = "<";
      for (CodeGenericType typeParam : this.typeParameters) {
        sink.append(prefix);
        typeParam.writeReference(sink, false);
        prefix = ", ";
      }
      sink.append("> ");
    }
    sink.append(this.method.getName());
    sink.append('(');
    String prefix = "";
    for (CodeExpression arg : this.arguments) {
      sink.append(prefix);
      arg.write(sink, newline, defaultIndent, currentIndent, language);
      prefix = ", ";
    }
    sink.append(')');
  }

}
