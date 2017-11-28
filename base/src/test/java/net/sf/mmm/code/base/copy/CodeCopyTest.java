/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.code.base.copy;

import org.junit.Test;

import net.sf.mmm.code.api.CodeContext;
import net.sf.mmm.code.api.CodePackage;
import net.sf.mmm.code.api.arg.CodeParameter;
import net.sf.mmm.code.api.copy.CodeNodeItemCopyable;
import net.sf.mmm.code.api.member.CodeConstructor;
import net.sf.mmm.code.api.member.CodeField;
import net.sf.mmm.code.api.member.CodeMethod;
import net.sf.mmm.code.api.type.CodeGenericType;
import net.sf.mmm.code.api.type.CodeType;
import net.sf.mmm.code.api.type.CodeTypeCategory;
import net.sf.mmm.code.base.BaseContextTest;

/**
 * Test of {@link CodeNodeItemCopyable} the entire AST.
 */
public class CodeCopyTest extends BaseContextTest {

  /**
   * Test of {@link CodeNodeItemCopyable#copy(net.sf.mmm.code.api.copy.CodeCopyMapper)}.
   */
  @Test
  public void testCopy() {

    // given
    CodeContext context = createContext();
    CodeType override = context.getType(Override.class).asType();
    CodeGenericType string = context.getType(String.class);
    CodeGenericType longType = context.getType(Long.class);

    String pkg1Name = "foo";
    CodePackage pkg1 = context.getSource().getRootPackage().getChildren().createPackage(pkg1Name);
    String pkg1Doc = "Cooles package ever!";
    pkg1.getDoc().add(pkg1Doc);
    CodeType result = pkg1.getChildren().getOrCreateFile("Result").getType();
    String resultDoc = "Container bean for the result of {@link Api#getSomething()}.";
    result.getDoc().add(resultDoc);
    result.setCategory(CodeTypeCategory.INTERFACE);
    String resultMethodAName = "getA";
    CodeMethod resultMethodA = result.getMethods().add(resultMethodAName);
    resultMethodA.getReturns().setType(string);
    resultMethodA.getReturns().getDoc().add("the magic A value as {@link String}.");
    String resultMethodBName = "getB";
    CodeMethod resultMethodB = result.getMethods().add(resultMethodBName);
    resultMethodB.getReturns().setType(longType);
    resultMethodB.getReturns().getDoc().add("the magic B value as {@link Long}.");
    CodeType api = pkg1.getChildren().getOrCreateFile("Api").getType();
    api.setCategory(CodeTypeCategory.INTERFACE);
    api.getDoc().add("Coolest API on earth.", "@author hackerboy");
    String apiMethodName = "getSomething";
    CodeMethod apiMethod = api.getMethods().add(apiMethodName);
    apiMethod.getDoc().getLines().add("Gets something special.");
    apiMethod.getReturns().setType(result);
    apiMethod.getReturns().getDoc().getLines().add("the magic {@link Result}.");
    CodeParameter apiMethodArg = apiMethod.getParameters().add("value");
    apiMethodArg.setType(string);
    apiMethodArg.getDoc().getLines().add("the magic value.");

    String pkg11Name = "bar";
    CodePackage pkg11 = pkg1.getChildren().getOrCreatePackage(pkg11Name);
    CodeType resultImpl = pkg11.getChildren().getOrCreateFile("ResultImpl").getType();
    CodeField fieldA = resultImpl.getFields().add("a");
    fieldA.setType(string);
    CodeField fieldB = resultImpl.getFields().add("b");
    fieldB.setType(longType);
    CodeConstructor constructor = resultImpl.getConstructors().add();
    constructor.getParameters().add("a").setType(string);
    constructor.getParameters().add("b").setType(longType);
    constructor.getBody().addText("this.a = a;");
    constructor.getBody().addText("this.b = b;");
    CodeMethod resultMethodAImpl = resultImpl.getMethods().add(resultMethodAName);
    resultMethodAImpl.getReturns().setType(string);
    resultMethodAImpl.getBody().addText("return this.a;");
    resultMethodAImpl.getAnnotations().add(override);
    CodeMethod resultMethodBImpl = resultImpl.getMethods().add(resultMethodBName);
    resultMethodBImpl.getReturns().setType(longType);
    resultMethodBImpl.getBody().addText("return this.b;");
    resultMethodBImpl.getAnnotations().add(override);

    CodeType apiImplBar = pkg11.getChildren().getOrCreateFile("ApiImplBar").getType();
    apiImplBar.getSuperTypes().add(api);
    apiImplBar.getFile().getImports().add(api);
    CodeMethod apiMethodImplBar = apiImplBar.getMethods().add(apiMethodName);
    apiMethodImplBar.getReturns().setType(result);
    CodeParameter apiMethodImplBarArg = apiMethodImplBar.getParameters().add("value");
    apiMethodImplBarArg.setType(string);
    apiMethodImplBar.getAnnotations().add(override);
    apiMethodImplBar.getBody().addText("return new ResultImpl(value, 42);");

    String pkg12Name = "some";
    CodePackage pkg12 = pkg1.getChildren().getOrCreatePackage(pkg12Name);
    CodeType apiImplSome = pkg12.getChildren().getOrCreateFile("ApiImplSome").getType();
    apiImplSome.getSuperTypes().add(api);
    apiImplSome.getFile().getImports().add(api);
    CodeMethod apiMethodImplSome = apiImplSome.getMethods().add(apiMethodName);
    apiMethodImplSome.getReturns().setType(result);
    CodeParameter apiMethodImplSomeArg = apiMethodImplSome.getParameters().add("value");
    apiMethodImplSomeArg.setType(string);
    apiMethodImplSome.getAnnotations().add(override);
    apiImplSome.getFile().getImports().add(override);
    apiImplSome.getFile().getImports().add(apiImplBar);
    apiMethodImplSome.getBody().addText("return new ApiImplBar().getSomething(value);");

    // when
    CodePackage pkg1Copy = pkg1.copy();

    // then
    assertThat(pkg1Copy).isNotNull().isNotSameAs(pkg1);
    assertThat(pkg1Copy.getSimpleName()).isEqualTo(pkg1.getSimpleName());
    assertThat(pkg1Copy.getParentPackage()).isSameAs(pkg1.getParentPackage());
    assertThat(pkg1Copy.getDoc().getLines()).containsExactly(pkg1Doc);
    assertThat(pkg1Copy.getChildren()).hasSize(4);
    CodeType resultCopy = pkg1Copy.getChildren().getType(result.getSimpleName());
    assertThat(resultCopy).isNotNull().isNotSameAs(result);
    assertThat(resultCopy.getParentPackage()).isSameAs(pkg1Copy);
    assertThat(resultCopy.getFile().getSourceCode()).isEqualTo("package foo;\n" + //
        "\n" + //
        "/** Container bean for the result of {@link Api#getSomething()}. */\n" + //
        "public interface Result {\n" + //
        "\n" + //
        "  /**\n" + //
        "   * @return the magic A value as {@link String}.\n" + //
        "   */\n" + //
        "  String getA();\n" + //
        "\n" + //
        "  /**\n" + //
        "   * @return the magic B value as {@link Long}.\n" + //
        "   */\n" + //
        "  Long getB();\n" + //
        "}\n");
    CodeType apiCopy = pkg1Copy.getChildren().getType(api.getSimpleName());
    assertThat(apiCopy).isNotNull().isNotSameAs(api);
    assertThat(apiCopy.getParentPackage()).isSameAs(pkg1Copy);
    assertThat(apiCopy.getFile().getSourceCode()).isEqualTo("package foo;\n" + //
        "\n" + //
        "/**\n" + //
        " * Coolest API on earth.\n" + //
        " * @author hackerboy\n" + //
        " */\n" + //
        "public interface Api {\n" + //
        "\n" + //
        "  /**\n" + //
        "   * Gets something special.\n" + //
        "   * @param value the magic value.\n" + //
        "   * @return the magic {@link Result}.\n" + //
        "   */\n" + //
        "  Result getSomething(String value);\n" + //
        "}\n");
    CodePackage pkg11Copy = pkg1Copy.getChildren().getPackage(pkg11Name);
    assertThat(pkg11Copy).isNotNull().isNotSameAs(pkg11);
    assertThat(pkg11Copy.getParentPackage()).isSameAs(pkg1Copy);
    assertThat(pkg11Copy.getChildren()).hasSize(2);
    CodeType apiImplBarCopy = pkg11Copy.getChildren().getType(apiImplBar.getSimpleName());
    assertThat(apiImplBarCopy).isNotNull().isNotSameAs(apiImplBar);
    assertThat(apiImplBarCopy.getParentPackage()).isSameAs(pkg11Copy);
    assertThat(apiImplBarCopy.getFile().getSourceCode()).isEqualTo("package foo.bar;\n" + //
        "\n" + //
        "import foo.Api;\n" + //
        "\n" + //
        "public class ApiImplBar implements Api {\n" + //
        "\n" + //
        "  @Override\n" + //
        "  public Result getSomething(String value) {\n" + //
        "    return new ResultImpl(value, 42);\n" + //
        "  }\n" + //
        "}\n");
    CodeType resultImplCopy = pkg11Copy.getChildren().getType(resultImpl.getSimpleName());
    assertThat(resultImplCopy).isNotNull().isNotSameAs(resultImpl);
    assertThat(resultImplCopy.getParentPackage()).isSameAs(pkg11Copy);
    assertThat(resultImplCopy.getFile().getSourceCode()).isEqualTo("package foo.bar;\n" + //
        "\n" + //
        "public class ResultImpl {\n" + //
        "\n" + //
        "  private String a;\n" + //
        "\n" + //
        "  private Long b;\n" + //
        "\n" + //
        "  public ResultImpl(String a, Long b) {\n" + //
        "    this.a = a;\n" + //
        "    this.b = b;\n" + //
        "  }\n" + //
        "\n" + //
        "  @Override\n" + //
        "  public String getA() {\n" + //
        "    return this.a;\n" + //
        "  }\n" + //
        "\n" + //
        "  @Override\n" + //
        "  public Long getB() {\n" + //
        "    return this.b;\n" + //
        "  }\n" + //
        "}\n");
    CodePackage pkg12Copy = pkg1Copy.getChildren().getPackage(pkg12Name);
    assertThat(pkg12Copy).isNotNull().isNotSameAs(pkg12);
    assertThat(pkg12Copy.getParentPackage()).isSameAs(pkg1Copy);
    assertThat(pkg12Copy.getChildren()).hasSize(1);
    CodeType apiImplSomeCopy = pkg12Copy.getChildren().getType(apiImplSome.getSimpleName());
    assertThat(apiImplSomeCopy).isNotNull().isNotSameAs(apiImplSome);
    assertThat(apiImplSomeCopy.getParentPackage()).isSameAs(pkg12Copy);
    assertThat(apiImplSomeCopy.getFile().getSourceCode()).isEqualTo("package foo.some;\n" + //
        "\n" + //
        "import foo.Api;\n" + //
        "import foo.bar.ApiImplBar;\n" + //
        "\n" + //
        "public class ApiImplSome implements Api {\n" + //
        "\n" + //
        "  @Override\n" + //
        "  public Result getSomething(String value) {\n" + //
        "    return new ApiImplBar().getSomething(value);\n" + //
        "  }\n" + //
        "}\n");
  }

}
