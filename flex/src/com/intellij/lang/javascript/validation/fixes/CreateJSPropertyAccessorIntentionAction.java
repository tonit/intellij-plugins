package com.intellij.lang.javascript.validation.fixes;

import com.intellij.codeInsight.template.Template;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.refactoring.util.JSRefactoringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.util.text.StringUtil;

/**
* Created by IntelliJ IDEA.
* User: Maxim.Mossienko
* Date: 04.06.2009
* Time: 18:25:39
* To change this template use File | Settings | File Templates.
*/
public class CreateJSPropertyAccessorIntentionAction extends CreateJSFunctionIntentionActionBase {
  private final boolean myIsGetter;
  public CreateJSPropertyAccessorIntentionAction(String name, boolean getter) {
    super(name, getter ? "javascript.create.get.property.intention.name" : "javascript.create.set.property.intention.name");
    myIsGetter = getter;
  }

  @Override
  protected void writeFunctionAndName(Template template, String referencedName, boolean ecma) {
    template.addTextSegment("function ");
    template.addTextSegment(myIsGetter ? "get ":"set ");
    template.addTextSegment(referencedName);
  }

  protected void addParameters(Template template, JSReferenceExpression refExpr, PsiFile file, boolean ecma) {
    if (!myIsGetter) {
      template.addTextSegment(refExpr.getReferencedName() +":");
      guessTypeAndAddTemplateVariable(template, refExpr, file, false);
    }
  }

  protected void addReturnType(Template template, JSReferenceExpression referenceExpression, PsiFile file) {
    if (myIsGetter) {
      guessTypeAndAddTemplateVariable(template, referenceExpression, file, false);
    } else {
      template.addTextSegment("void");
    }
  }

  protected void addBody(Template template, JSReferenceExpression refExpr, PsiFile file) {
    String varName = refExpr.getReferencedName();
    String paramName = varName;
    varName = JSRefactoringUtil.transformAccessorNameToPropertyName(varName, file.getProject());

    if (varName.equals(paramName)) {
      varName = StringUtil.fixVariableNameDerivedFromPropertyName(varName);
    }

    if (myIsGetter) {
      template.addTextSegment("return ");

      addVarName(template, varName);
      template.addEndVariable();
    } else {
      addVarName(template, varName);
      template.addEndVariable();
      template.addTextSegment(" = " + paramName);
    }
    addSemicolonSegment(template, file);
  }

  protected static void addVarName(Template template, String varName) {
    MyExpression expression = new MyExpression(varName);
    template.addVariable("name", expression, expression, true);
  }

}
