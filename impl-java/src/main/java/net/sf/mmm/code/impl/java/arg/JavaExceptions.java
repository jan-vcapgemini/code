/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.impl.java.arg;

import java.io.IOException;
import java.util.List;

import net.sf.mmm.code.api.arg.CodeException;
import net.sf.mmm.code.api.arg.CodeExceptions;
import net.sf.mmm.code.api.node.CodeNodeItemWithGenericParent;
import net.sf.mmm.code.api.type.CodeGenericType;
import net.sf.mmm.code.impl.java.member.JavaOperation;

/**
 * Implementation of {@link CodeExceptions} for Java.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class JavaExceptions extends JavaOperationArgs<CodeException, JavaException>
    implements CodeExceptions, CodeNodeItemWithGenericParent<JavaOperation, JavaExceptions> {

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public JavaExceptions(JavaOperation parent) {

    super(parent);
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link JavaExceptions} to copy.
   * @param parent the {@link #getParent() parent}.
   */
  public JavaExceptions(JavaExceptions template, JavaOperation parent) {

    super(template, parent);
  }

  @Override
  public JavaException get(CodeGenericType type) {

    for (JavaException exception : getAll()) {
      if (exception.getType().equals(type)) {
        return exception;
      }
    }
    return null;
  }

  @Override
  public JavaException add(CodeGenericType type) {

    verifyMutalbe();
    JavaException exception = new JavaException(this);
    exception.setType(type);
    add(exception);
    return exception;
  }

  @Override
  public JavaExceptions copy() {

    return copy(getParent());
  }

  @Override
  public JavaExceptions copy(JavaOperation newParent) {

    return new JavaExceptions(this, newParent);
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent) throws IOException {

    List<JavaException> list = getList();
    if (list.isEmpty()) {
      return;
    }
    String prefix = " throws ";
    for (JavaException exception : list) {
      sink.append(prefix);
      exception.write(sink, newline, null, null);
      prefix = ", ";
    }
  }

}
