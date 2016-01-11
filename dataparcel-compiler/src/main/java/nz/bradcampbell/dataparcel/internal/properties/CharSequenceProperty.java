package nz.bradcampbell.dataparcel.internal.properties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import nz.bradcampbell.dataparcel.internal.Property;

public class CharSequenceProperty extends Property {
  public CharSequenceProperty(Property.Type propertyType, boolean isNullable, String name) {
    super(propertyType, isNullable, name);
  }

  @Override protected void readFromParcelInner(CodeBlock.Builder block, ParameterSpec in) {
    TypeName wrappedTypeName = getPropertyType().getWrappedTypeName();
    block.addStatement("$N = ($T) $N.readCharSequence()", getName(), wrappedTypeName, in);
  }

  @Override protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, String variableName) {
    block.addStatement("$N.writeCharSequence($N)", dest, variableName);
  }
}
