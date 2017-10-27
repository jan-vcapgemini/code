/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.annoation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.mmm.code.api.annotation.CodeAnnotation;
import net.sf.mmm.code.api.annotation.CodeAnnotations;
import net.sf.mmm.code.api.member.CodeMethod;
import net.sf.mmm.code.api.node.CodeNodeItemWithGenericParent;
import net.sf.mmm.code.api.syntax.CodeSyntax;
import net.sf.mmm.code.api.type.CodeType;
import net.sf.mmm.code.base.element.BaseElementImpl;
import net.sf.mmm.code.base.node.BaseNodeItemContainerHierarchical;
import net.sf.mmm.code.base.type.BaseType;
import net.sf.mmm.code.base.type.InternalSuperTypeIterator;
import net.sf.mmm.util.collection.base.AbstractIterator;

/**
 * Base implementation of {@link CodeAnnotations}.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 * @since 1.0.0
 */
public class BaseAnnotations extends BaseNodeItemContainerHierarchical<CodeAnnotation>
    implements CodeAnnotations, CodeNodeItemWithGenericParent<BaseElementImpl, BaseAnnotations> {

  private final BaseElementImpl parent;

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent}.
   */
  public BaseAnnotations(BaseElementImpl parent) {

    super();
    this.parent = parent;
  }

  /**
   * The copy-constructor.
   *
   * @param template the {@link BaseAnnotations} to copy.
   * @param parent the {@link #getParent() parent}.
   */
  public BaseAnnotations(BaseAnnotations template, BaseElementImpl parent) {

    super(template);
    this.parent = parent;
  }

  @Override
  public BaseElementImpl getParent() {

    return this.parent;
  }

  @Override
  public BaseType getDeclaringType() {

    if (this.parent == null) {
      return null;
    }
    return this.parent.getDeclaringType();
  }

  @Override
  public CodeAnnotation getDeclared(CodeType type) {

    for (CodeAnnotation annotation : getDeclared()) {
      if (annotation.getType().asType().equals(type)) {
        return annotation;
      }
    }
    return null;
  }

  @Override
  public CodeAnnotation add(CodeType type) {

    verifyMutalbe();
    CodeAnnotation annotation = createAnnoation(type);
    add(annotation);
    return annotation;
  }

  /**
   * @param type the {@link BaseAnnotation#getType() type} of the {@link BaseAnnotation} to create.
   * @return the new {@link BaseAnnotation} instance.
   */
  protected BaseAnnotation createAnnoation(CodeType type) {

    return new BaseAnnotation(getContext(), type);
  }

  @Override
  public void add(CodeAnnotation item) {

    super.add(item);
  }

  @Override
  public CodeAnnotation getDeclaredOrAdd(CodeType type) {

    CodeAnnotation annotation = getDeclared(type);
    if (annotation == null) {
      annotation = add(type);
    }
    return annotation;
  }

  @Override
  public Iterable<? extends CodeAnnotation> getAll() {

    if (this.parent instanceof BaseType) {
      return () -> new TypeAnnotationIterator((BaseType) this.parent);
    } else if (this.parent instanceof CodeMethod) {
      return () -> new MethodAnnotationIterator((CodeMethod) this.parent);
    } else {
      return getDeclared();
    }
  }

  @Override
  public BaseAnnotations copy() {

    return copy(this.parent);
  }

  @Override
  public BaseAnnotations copy(BaseElementImpl newParent) {

    return new BaseAnnotations(this, newParent);
  }

  @Override
  protected void doWrite(Appendable sink, String newline, String defaultIndent, String currentIndent, CodeSyntax syntax) throws IOException {

    String prefix = "";
    for (CodeAnnotation annotation : getDeclared()) {
      if (defaultIndent == null) {
        sink.append(prefix);
        prefix = " ";
      } else {
        sink.append(newline);
        sink.append(currentIndent);
      }
      annotation.write(sink, newline, defaultIndent, currentIndent, syntax);
    }
    sink.append(prefix);
  }

  private abstract class AnnotationIterator extends AbstractIterator<CodeAnnotation> {

    private final Set<CodeType> iteratedAnnotations;

    private Iterator<CodeAnnotation> currentIterator;

    protected AnnotationIterator() {

      super();
      this.iteratedAnnotations = new HashSet<>();
      this.currentIterator = getList().iterator();
    }

    @Override
    protected CodeAnnotation findNext() {

      while (this.currentIterator.hasNext()) {
        CodeAnnotation annotation = this.currentIterator.next();
        CodeType annotationType = annotation.getType().asType();
        boolean added = this.iteratedAnnotations.add(annotationType);
        if (added) {
          return annotation;
        }
      }
      this.currentIterator = nextParent();
      if (this.currentIterator == null) {
        return null;
      }
      return findNext();
    }

    protected abstract Iterator<CodeAnnotation> nextParent();
  }

  private class MethodAnnotationIterator extends AnnotationIterator {

    private CodeMethod method;

    private MethodAnnotationIterator(CodeMethod method) {

      super();
      this.method = method;
      findFirst();
    }

    @Override
    protected Iterator<CodeAnnotation> nextParent() {

      this.method = this.method.getParentMethod();
      if (this.method == null) {
        return null;
      }
      return this.method.getAnnotations().iterator();
    }
  }

  private class TypeAnnotationIterator extends AnnotationIterator {

    private InternalSuperTypeIterator iterator;

    private TypeAnnotationIterator(BaseType type) {

      super();
      this.iterator = new InternalSuperTypeIterator(type);
      findFirst();
    }

    @Override
    protected Iterator<CodeAnnotation> nextParent() {

      if (this.iterator.hasNext()) {
        this.iterator = this.iterator.next();
      } else {
        return null;
      }
      if (this.iterator == null) {
        return null;
      }
      return this.iterator.getType().asType().getAnnotations().iterator();
    }
  }

}
