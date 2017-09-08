/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.impl.java.member;

import java.io.IOException;
import java.util.function.Consumer;

import net.sf.mmm.code.api.member.CodeMember;
import net.sf.mmm.code.api.member.CodeMembers;
import net.sf.mmm.code.impl.java.node.JavaNodeItemContainer;
import net.sf.mmm.code.impl.java.type.JavaType;

/**
 * Implementation of {@link CodeMembers} for Java.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @param <J> the type of the contained {@link JavaMember}.
 * @param <M> type of the contained {@link CodeMember}s.
 * @since 1.0.0
 */
public abstract class JavaMembers<M extends CodeMember, J extends M> extends JavaNodeItemContainer<M, J> implements CodeMembers<M> {

  private final JavaType parent;

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public JavaMembers(JavaType parent) {

    super();
    this.parent = parent;
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link JavaMembers} to copy.
   * @param parent the {@link #getParent() parent}.
   */
  public JavaMembers(JavaMembers<M, J> template, JavaType parent) {

    super(template);
    this.parent = parent;
  }

  @Override
  public JavaType getParent() {

    return this.parent;
  }

  @Override
  protected void rename(J member, String oldName, String newName, Consumer<String> renamer) {

    super.rename(member, oldName, newName, renamer);
    this.parent.getProperties().renameMember((JavaMember) member, oldName, newName);
  }

  @Override
  public abstract JavaMembers<M, J> copy();

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent) throws IOException {

    for (M declaredMember : getList()) {
      sink.append(newline);
      declaredMember.write(sink, defaultIndent, currentIndent);
    }
  }
}
