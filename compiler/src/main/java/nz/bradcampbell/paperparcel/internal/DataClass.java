package nz.bradcampbell.paperparcel.internal;

import com.squareup.javapoet.ClassName;

import java.util.List;
import java.util.Set;

/**
 * A model object that holds information needed to build a Parcelable data class wrapper
 */
public class DataClass {
  private final String classPackage;
  private final List<Property> properties;
  private final ClassName className;
  private final ClassName wrapperClassName;
  private final boolean requiresClassLoader;
  private final Set<Adapter> requiredTypeAdapters;
  private final boolean singleton;

  /**
   * Constructor.
   *
   * @param properties All properties in the data class
   * @param classPackage The package of the data class
   * @param wrapperTypeName The simple name of the wrapper class
   * @param className The data class type name
   * @param requiresClassLoader True if a ClassLoader field is required, false otherwise
   * @param requiredTypeAdapters All of the TypeAdapter types required for this class
   * @param singleton True if the class is a singleton object
   */
  public DataClass(List<Property> properties, String classPackage, ClassName wrapperTypeName, ClassName className,
                   boolean requiresClassLoader, Set<Adapter> requiredTypeAdapters, boolean singleton) {
    this.properties = properties;
    this.classPackage = classPackage;
    this.requiresClassLoader = requiresClassLoader;
    this.wrapperClassName = wrapperTypeName;
    this.className = className;
    this.requiredTypeAdapters = requiredTypeAdapters;
    this.singleton = singleton;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public String getClassPackage() {
    return classPackage;
  }

  public ClassName getWrapperClassName() {
    return wrapperClassName;
  }

  public ClassName getClassName() {
    return className;
  }

  public boolean getRequiresClassLoader() {
    return requiresClassLoader;
  }

  public Set<Adapter> getRequiredTypeAdapters() {
    return requiredTypeAdapters;
  }

  public boolean isSingleton() {
    return singleton;
  }
}
