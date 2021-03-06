package paperparcel;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class PaperParcelAutoValueExtensionTest {

  @Test public void basicAutoValueTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelAutoValue_Test {",
            "  @NonNull",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(@NonNull AutoValue_Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

  @Test public void nullableAnnotationTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.support.annotation.Nullable;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  @Nullable public abstract Integer count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.Nullable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(@Nullable Integer count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass);
  }

  @Test public void genericAutoValueClassWithParcelableExtendsBoundsTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test<T extends Parcelable> implements Parcelable {",
            "  public abstract T count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test<T extends Parcelable> extends $AutoValue_Test<T> {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(T count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass);
  }

  @Test public void omitDescribeContentsWhenAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelAutoValue_Test {",
            "  @NonNull",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(@NonNull AutoValue_Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

  @Test public void failWhenWriteToParcelAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.MANUAL_IMPLEMENTATION_OF_WRITE_TO_PARCEL,
            "test.Test"))
        .in(source)
        .onLine(9);
  }

  @Test public void failWhenCreatorAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  public static final Parcelable.Creator<Test> CREATOR = ",
            "      new Parcelable.Creator<Test>() {",
            "    @Override public Test createFromParcel(Parcel in) {",
            "      return null;",
            "    }",
            "    @Override public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.MANUAL_IMPLEMENTATION_OF_CREATOR,
            "test.Test"))
        .in(source)
        .onLine(8);
  }

  @Test public void resolveTypeParameterPropertyTest() throws Exception {
    JavaFileObject foo =
        JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join(
            "package test;",
            "public interface Foo {",
            "}"
        ));

    JavaFileObject bar =
        JavaFileObjects.forSourceString("test.Bar", Joiner.on('\n').join(
            "package test;",
            "public interface Bar<F extends Foo> {",
            "  F foo();",
            "}"
        ));

    JavaFileObject pFoo =
        JavaFileObjects.forSourceString("test.PFoo", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class PFoo implements Foo, Parcelable {",
            "}"
        ));

    JavaFileObject pBar =
        JavaFileObjects.forSourceString("test.PBar", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class PBar implements Bar<PFoo>, Parcelable {",
            "}"
        ));

    JavaFileObject autoValuePFoo =
        JavaFileObjects.forSourceString("test/AutoValue_PFoo", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_PFoo extends $AutoValue_PFoo {",
            "  public static final Parcelable.Creator<AutoValue_PFoo> CREATOR = PaperParcelAutoValue_PFoo.CREATOR;",
            "  AutoValue_PFoo() {",
            "    super();",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_PFoo.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject autoValuePBar =
        JavaFileObjects.forSourceString("test/AutoValue_PBar", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_PBar extends $AutoValue_PBar {",
            "  public static final Parcelable.Creator<AutoValue_PBar> CREATOR = PaperParcelAutoValue_PBar.CREATOR;",
            "  AutoValue_PFoo(PFoo foo) {",
            "    super(foo);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_PBar.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(foo, bar, pFoo, pBar))
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValuePFoo, autoValuePBar);
  }

  @Test public void basicGenericAutoValueTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test<T extends Parcelable> implements Parcelable {",
            "  public abstract T count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test<T extends Parcelable> extends $AutoValue_Test<T> {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(T count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass);
  }

  @Test public void paperParcelOptionsAutoValueTest() throws Exception {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Exclude", Joiner.on('\n').join(
            "package test;",
            "public @interface Exclude {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeAnnotations = Exclude.class)",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  @Exclude private int excludeMe;",
            "  public abstract int count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelAutoValue_Test {",
            "  @NonNull",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(@NonNull AutoValue_Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

  @Test public void paperParcelOptionsAutoValueTest2() throws Exception {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Exclude", Joiner.on('\n').join(
            "package test;",
            "public @interface Exclude {}"
        ));

    JavaFileObject myOptions =
        JavaFileObjects.forSourceString("test.MyOptions", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeAnnotations = Exclude.class)",
            "public @interface MyOptions {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@MyOptions",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  @Exclude private int excludeMe;",
            "  public abstract int count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelAutoValue_Test {",
            "  @NonNull",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(@NonNull AutoValue_Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, myOptions, excludeAnnotation))
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

}
