/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.impl.java.element;

import java.io.IOException;
import java.util.Objects;

import net.sf.mmm.code.api.element.CodeElementWithModifiers;
import net.sf.mmm.code.api.modifier.CodeModifiers;

/**
 * Implementation of {@link CodeElementWithModifiers} for Java.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public abstract class JavaElementWithModifiers extends JavaElement implements CodeElementWithModifiers {

  private CodeModifiers modifiers;

  /**
   * The constructor.
   *
   * @param modifiers the {@link #getModifiers() modifiers}.
   */
  public JavaElementWithModifiers(CodeModifiers modifiers) {

    super();
    this.modifiers = modifiers;
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link JavaElementWithModifiers} to copy.
   */
  public JavaElementWithModifiers(JavaElementWithModifiers template) {

    super(template);
    this.modifiers = template.modifiers;
  }

  @Override
  public CodeModifiers getModifiers() {

    return this.modifiers;
  }

  @Override
  public void setModifiers(CodeModifiers modifiers) {

    Objects.requireNonNull(modifiers, "modifiers");
    verifyMutalbe();
    this.modifiers = modifiers;
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent) throws IOException {

    super.doWrite(sink, newline, defaultIndent, currentIndent);
    sink.append(currentIndent);
    doWriteModifiers(sink);
  }

  /**
   * @param sink the {@link Appendable}.
   * @throws IOException if thrown by {@link Appendable}.
   */
  protected void doWriteModifiers(Appendable sink) throws IOException {

    sink.append(this.modifiers.toString());
  }

}
