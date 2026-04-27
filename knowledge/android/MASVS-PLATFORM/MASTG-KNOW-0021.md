---
masvs_category: MASVS-PLATFORM
platform: android
title: Object Serialization and Deserialization
---

Object serialization converts an in-memory object graph into a sequence of bytes (or a structured representation such as JSON or XML) so it can be stored, transferred between components, or sent over a network. Deserialization is the reverse: a byte sequence or structured payload is read back and an object instance is reconstructed from it. On Android, both directions are used routinely for inter-component communication, persistence, and network I/O, and the platform provides multiple built-in mechanisms in addition to the Java standard library.

The reconstruction step makes deserialization different from generic input parsing: the framework reads field values from the input and either populates a newly allocated instance or invokes type-specific reconstruction logic supplied by the class itself. Knowing which mechanism is in use determines which APIs participate in the reconstruction and what the entry point looks like.

## Java Object Deserialization

The Java standard library exposes binary object serialization through [`java.io.Serializable`](https://developer.android.com/reference/java/io/Serializable) and [`java.io.Externalizable`](https://developer.android.com/reference/java/io/Externalizable). Classes opt into serialization by implementing one of these interfaces.

Reconstruction goes through [`java.io.ObjectInputStream`](https://developer.android.com/reference/java/io/ObjectInputStream). Relevant entry points include:

- `ObjectInputStream.readObject` reads the next object from the stream. For `Serializable` classes, the runtime allocates an instance and populates fields directly. If the class declares a `private void readObject(ObjectInputStream in)` method, the runtime invokes it during reconstruction.
- `ObjectInputStream.readUnshared` is the same as `readObject` but does not allow back-references to the returned instance.
- `Externalizable.readExternal(ObjectInput in)` is invoked during reconstruction of `Externalizable` classes. Unlike `Serializable`, the class is responsible for reading and assigning every field through this method.

Subclasses can override `ObjectInputStream.resolveClass` and `ObjectInputStream.resolveObject` to constrain or customize the types that are accepted during reconstruction.

## Common Patterns on Android

Android applications use several deserialization patterns. The choice of mechanism affects which APIs are involved and where the reconstruction logic lives.

- **Java object streams.** `ObjectInputStream` over a file, asset, or network stream, typically used for ad-hoc persistence or process boundaries that do not use Android's IPC primitives.
- **Parcelable / Parcel.** [`Parcelable`](https://developer.android.com/reference/android/os/Parcelable) is the Android-native mechanism for high-performance object marshalling across IPC boundaries (Binder, Intents, Bundles). Each `Parcelable` class supplies a `static final Parcelable.Creator<T>` whose `createFromParcel(Parcel)` method reconstructs an instance by reading typed values from a [`Parcel`](https://developer.android.com/reference/android/os/Parcel).
- **Bundle.** [`Bundle`](https://developer.android.com/reference/android/os/Bundle) is a typed key/value container backed by a `Parcel`. Its values can include primitives, `Parcelable`s, `Serializable`s, and nested `Bundle`s. Reconstruction is lazy: values are decoded the first time they are accessed.
- **Intent extras.** [`Intent`](https://developer.android.com/reference/android/content/Intent) extras are stored in a `Bundle`. When a component receives an `Intent`, deserialization happens at the point each extra is read by the receiving code.
- **Structured-data formats.** JSON, XML, and Protocol Buffers are mapped to objects by libraries such as `org.json.JSONObject`, GSON, Moshi, Jackson, `XmlPullParser`, SAX, and the protobuf runtime. Reconstruction here typically happens through reflection or generated code rather than through Java's `ObjectInputStream`.

## Android APIs and Entry Points

The following APIs are common entry points for object reconstruction in Android apps. Knowing which API is used identifies where the deserialized data crosses into the application's object model.

- `Intent.getSerializableExtra(String name)` returns a `Serializable` from the intent's extras. Deserialization happens on first access.
- `Intent.getSerializableExtra(String name, Class<T> clazz)` is the typed overload added in API level 33; it returns the requested type or `null`.
- `Bundle.getSerializable(String key)` and `Bundle.getSerializable(String key, Class<T> clazz)` (API 33+) read a `Serializable` from a bundle.
- `Intent.getParcelableExtra(String name)` and `Intent.getParcelableExtra(String name, Class<T> clazz)` (API 33+) read a `Parcelable` from intent extras.
- `Bundle.getParcelable(String key)` and `Bundle.getParcelable(String key, Class<T> clazz)` (API 33+) read a `Parcelable` from a bundle.
- `Bundle.getParcelableArrayList`, `Bundle.getSparseParcelableArray`, and their typed overloads read `Parcelable` collections.
- `Parcel.readParcelable`, `Parcel.readSerializable`, `Parcel.readValue`, and `Parcel.readBundle` are the lower-level primitives the higher-level APIs above are built on.
- `Parcelable.Creator.createFromParcel(Parcel)` is the application-defined reconstruction point for any `Parcelable` class. The class controls what is read from the `Parcel` and how the instance is initialized.
- `ObjectInputStream.readObject` and `ObjectInputStream.readUnshared` are the entry points for Java standard-library deserialization on Android.

The typed overloads introduced in [API level 33 (Android 13)](https://developer.android.com/reference/android/os/Bundle#getParcelable(java.lang.String,%20java.lang.Class)) accept a `Class<T>` parameter so the caller declares the expected type at the call site. Earlier API levels rely on the value's recorded type alone.
