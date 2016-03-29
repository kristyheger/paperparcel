package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class ArrayTests {

  @Test public void booleanArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Boolean[] child;",
        "public Test(Boolean[] child) {",
        "this.child = child;",
        "}",
        "public Boolean[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Boolean[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Boolean[] child = new Boolean[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Boolean outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "outChildComponent = in.readInt() == 1;",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child;",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Boolean[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Boolean childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childItem ? 1 : 0);",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void arrayOfListsOfNonParcelableObjectsTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.List;",
        "@PaperParcel",
        "public final class Test {",
        "private final List<Child>[] child;",
        "public Test(List<Child>[] child) {",
        "this.child = child;",
        "}",
        "public List<Child>[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "List<Child>[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "List<Child>[] child = new List[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<Child> outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "int childComponentSize = in.readInt();",
        "List<Child> childComponent = new ArrayList<Child>(childComponentSize);",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child outChildComponentItem = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildComponentItemChild = null;",
        "if (in.readInt() == 0) {",
        "outChildComponentItemChild = in.readInt();",
        "}",
        "outChildComponentItem = new Child(outChildComponentItemChild);",
        "}",
        "childComponent.add(outChildComponentItem);",
        "}",
        "outChildComponent = childComponent;",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child;",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<Child>[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<Child> childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childComponentSize = childItem.size();",
        "dest.writeInt(childComponentSize);",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child childComponentItem = childItem.get(childComponentIndex);",
        "if (childComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (childComponentItem.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childComponentItem.getChild());",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonParcelableArrayOfArraysTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import android.util.SparseArray;",
        "@PaperParcel",
        "public final class Test {",
        "private final Child[][] child;",
        "public Test(Child[][] child) {",
        "this.child = child;",
        "}",
        "public Child[][] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Child[][] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Child[][] child = new Child[childSize][];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child[] outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "int childComponentSize = in.readInt();",
        "Child[] childComponent = new Child[childComponentSize];",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child outChildComponentComponent = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildComponentComponentChild = null;",
        "if (in.readInt() == 0) {",
        "outChildComponentComponentChild = in.readInt();",
        "}",
        "outChildComponentComponent = new Child(outChildComponentComponentChild);",
        "}",
        "childComponent[childComponentIndex] = outChildComponentComponent;",
        "}",
        "outChildComponent = childComponent;",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child;",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child[][] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child[] childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childComponentSize = childItem.length;",
        "dest.writeInt(childComponentSize);",
        "for (int childComponentIndex = 0; childComponentIndex < childComponentSize; childComponentIndex++) {",
        "Child childComponentItem = childItem[childComponentIndex];",
        "if (childComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (childComponentItem.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childComponentItem.getChild());",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void parcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Bitmap[] child;",
        "public Test(Bitmap[] child) {",
        "this.child = child;",
        "}",
        "public Bitmap[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Bitmap[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Bitmap[] child = new Bitmap[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "outChildComponent = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Bitmap[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Bitmap childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "childItem.writeToParcel(dest, flags);",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonParcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Child[] child;",
        "public Test(Child[] child) {",
        "this.child = child;",
        "}",
        "public Child[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Child[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Child[] child = new Child[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildComponentChild = null;",
        "if (in.readInt() == 0) {",
        "outChildComponentChild = in.readInt();",
        "}",
        "outChildComponent = new Child(outChildComponentChild);",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child;",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (childItem.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childItem.getChild());",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void charSequenceArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final CharSequence[] child;",
        "public Test(CharSequence[] child) {",
        "this.child = child;",
        "}",
        "public CharSequence[] getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import android.text.TextUtils;",
        "import java.lang.CharSequence;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "CharSequence[] outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "CharSequence[] child = new CharSequence[childSize];",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "CharSequence outChildComponent = null;",
        "if (in.readInt() == 0) {",
        "outChildComponent = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);",
        "}",
        "child[childIndex] = outChildComponent;",
        "}",
        "outChild = child;",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "CharSequence[] child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.length;",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "CharSequence childItem = child[childIndex];",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "TextUtils.writeToParcel(childItem, dest, flags);",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
