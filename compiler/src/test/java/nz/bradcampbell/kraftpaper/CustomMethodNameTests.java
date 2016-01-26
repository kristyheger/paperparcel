package nz.bradcampbell.kraftpaper;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class CustomMethodNameTests {

    @Test public void customMethodNameTest() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                "package test;",
                "import nz.bradcampbell.kraftpaper.KraftPaper;",
                "import nz.bradcampbell.kraftpaper.GetterMethodName;",
                "@KraftPaper",
                "public final class Test {",
                "@GetterMethodName(\"customGetterMethod\")",
                "private final int child;",
                "public Test(int child) {",
                "this.child = child;",
                "}",
                "public int customGetterMethod() {",
                "return this.child;",
                "}",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
                "package test;",
                "import android.os.Parcel;",
                "import android.os.Parcelable;",
                "import java.lang.Override;",
                "public class TestParcel implements Parcelable {",
                "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
                "@Override public TestParcel createFromParcel(Parcel in) {",
                "return new TestParcel(in);",
                "}",
                "@Override public TestParcel[] newArray(int size) {",
                "return new TestParcel[size];",
                "}",
                "};",
                "private final Test data;",
                "private TestParcel(Test data) {",
                "this.data = data;",
                "}",
                "private TestParcel(Parcel in) {",
                "int child = in.readInt();",
                "this.data = new Test(child);",
                "}",
                "public static final TestParcel wrap(Test data) {",
                "return new TestParcel(data);",
                "}",
                "public Test getContents() {",
                "return data;",
                "}",
                "@Override public int describeContents() {",
                "return 0;",
                "}",
                "@Override public void writeToParcel(Parcel dest, int flags) {",
                "int child = data.customGetterMethod();",
                "dest.writeInt(child);",
                "}",
                "}"
        ));

        assertAbout(javaSource()).that(source)
                .processedWith(new KraftPaperProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}
