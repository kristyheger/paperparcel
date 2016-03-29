package nz.bradcampbell.paperparcel.internal.properties;

import static nz.bradcampbell.paperparcel.internal.utils.PropertyUtils.literal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import nz.bradcampbell.paperparcel.internal.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class SparseArrayProperty extends Property {
  private Property typeArgument;

  public SparseArrayProperty(Property typeArgument, boolean isNullable, TypeName typeName, boolean isInterface,
                             String name, @Nullable String accessorMethodName) {
    super(isNullable, typeName, isInterface, name, accessorMethodName);
    this.typeArgument = typeArgument;
  }

  @Override
  protected CodeBlock readFromParcelInner(CodeBlock.Builder block, ParameterSpec in, @Nullable FieldSpec classLoader,
                                          Map<ClassName, FieldSpec> typeAdapters) {
    // Read size
    String sparseArraySize = getName() + "Size";
    block.addStatement("$T $N = $N.readInt()", int.class, sparseArraySize, in);

    // Create SparseArray to read into
    String sparseArrayName = getName();
    TypeName typeName = getTypeName();
    if (typeName instanceof WildcardTypeName) {
      typeName = ((WildcardTypeName) typeName).upperBounds.get(0);
    }

    TypeName parameterTypeName = typeArgument.getTypeName();
    if (parameterTypeName instanceof WildcardTypeName) {
      ParameterizedTypeName originalType = (ParameterizedTypeName) typeName;
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
      typeName = ParameterizedTypeName.get(originalType.rawType, parameterTypeName);
    }

    block.addStatement("$T $N = new $T($N)", typeName, sparseArrayName, typeName, sparseArraySize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    String keyName = getName() + "Key";

    // Read the key
    block.addStatement("$T $N = $N.readInt()", int.class, keyName, in);

    // Read in the value.
    CodeBlock parameterLiteral = typeArgument.readFromParcel(block, in, classLoader, typeAdapters);

    // Add the parameter to the output list
    block.addStatement("$N.put($N, $L)", sparseArrayName, keyName, parameterLiteral);

    block.endControlFlow();

    return literal("$N", sparseArrayName);
  }

  @Override
  protected void writeToParcelInner(CodeBlock.Builder block, ParameterSpec dest, ParameterSpec flags,
                                    CodeBlock sourceLiteral, Map<ClassName, FieldSpec> typeAdapters) {
    // Write size
    String sparseArraySize = getName() + "Size";
    block.addStatement("$T $N = $L.size()", int.class, sparseArraySize, sourceLiteral);
    block.addStatement("$N.writeInt($N)", dest, sparseArraySize);

    // Write a loop to iterate through each parameter
    String indexName = getName() + "Index";
    block.beginControlFlow("for (int $N = 0; $N < $N; $N++)", indexName, indexName, sparseArraySize, indexName);

    TypeName parameterTypeName = typeArgument.getTypeName();

    // Handle wildcard types
    if (parameterTypeName instanceof WildcardTypeName) {
      parameterTypeName = ((WildcardTypeName) parameterTypeName).upperBounds.get(0);
    }

    String keyName = getName() + "Key";
    block.addStatement("$T $N = $L.keyAt($N)", int.class, keyName, sourceLiteral, indexName);
    block.addStatement("$N.writeInt($N)", dest, keyName);

    String valueName = getName() + "Value";
    block.addStatement("$T $N = $L.get($N)", parameterTypeName, valueName, sourceLiteral, keyName);

    CodeBlock parameterSource = literal("$N", valueName);

    // Write in the parameter.
    typeArgument.writeToParcel(block, dest, flags, parameterSource, typeAdapters);

    block.endControlFlow();
  }

  @Override public boolean requiresClassLoader() {
    return typeArgument.requiresClassLoader();
  }

  @Override public Set<ClassName> requiredTypeAdapters() {
    return typeArgument.requiredTypeAdapters();
  }
}
