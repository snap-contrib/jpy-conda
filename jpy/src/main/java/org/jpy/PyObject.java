/*
 * Copyright (C) 2014 Brockmann Consult GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, see
 * http://www.gnu.org/licenses/
 */

package org.jpy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static org.jpy.PyLib.assertPythonRuns;

/**
 * Represents a Python object (of Python/C API type {@code PyObject*}) in the Python interpreter.
 *
 * @author Norman Fomferra
 * @since 0.7
 */
public class PyObject {

    /**
     * The value of the Python/C API {@code PyObject*} which this class represents.
     */
    private final long pointer;

    PyObject(long pointer) {
        if (pointer == 0) {
            throw new IllegalArgumentException("pointer == 0");
        }
        PyLib.incRef(pointer);
        this.pointer = pointer;
    }

    /**
     * Decrements the reference count of the Python object which this class represents.
     *
     * @throws Throwable If any error occurs.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // Don't remove this check. 'pointer == 0' really occurred, don't ask me how!
        if (pointer == 0) {
            throw new IllegalStateException("pointer == 0");
        }
        PyLib.decRef(getPointer());
    }

    /**
     * @return A unique pointer to the wrapped Python object.
     */
    public final long getPointer() {
        return pointer;
    }

    /**
     * @return This Python object as a Java {@code int} value.
     */
    public int getIntValue() {
        assertPythonRuns();
        return PyLib.getIntValue(getPointer());
    }

    /**
     * @return This Python object as a Java {@code double} value.
     */
    public double getDoubleValue() {
        assertPythonRuns();
        return PyLib.getDoubleValue(getPointer());
    }

    /**
     * @return This Python object as a Java {@code String} value.
     */
    public String getStringValue() {
        assertPythonRuns();
        return PyLib.getStringValue(getPointer());
    }

    /**
     * Gets this Python object as Java {@code Object} value.
     * <p/>
     * Appropriate type conversions from Python to Java will be performed as needed.
     * If this Python object cannot be converted into a Java object, a Java wrapper of type {@link PyObject} will be returned.
     * If this Python object is a wrapped Java object, it will be unwrapped.
     *
     * @return This Python object as a Java {@code Object} value.
     */
    public Object getObjectValue() {
        assertPythonRuns();
        return PyLib.getObjectValue(getPointer());
    }

    /**
     * Gets this Python object as Java {@code Object[]} value of the given item type.
     * <p/>
     * Appropriate type conversions from Python to Java will be performed as needed.
     * If a Python item value cannot be converted into a Java item object, a Java wrapper of type {@link PyObject} will be returned.
     * If a Python item value is a wrapped Java object, it will be unwrapped.
     * If this Python object value is a wrapped Java array object of given type, it will be unwrapped.
     *
     * @return This Python object as a Java {@code Object[]} value.
     */
    public <T> T[] getObjectArrayValue(Class<T> itemType) {
        assertPythonRuns();
        return PyLib.getObjectArrayValue(getPointer(), itemType);
    }

    /**
     * Gets the Python value of a Python attribute.
     *
     * @param name A name of the Python attribute.
     * @return A wrapper for the returned Python attribute value.
     */
    public PyObject getAttribute(String name) {
        assertPythonRuns();
        long pointer = PyLib.getAttributeObject(getPointer(), name);
        return pointer != 0 ? new PyObject(pointer) : null;
    }

    /**
     * Gets the value of a Python attribute as Java object of a given type.
     * <p/>
     * Appropriate type conversions from Python to Java will be performed as needed.
     * If the Python value cannot be converted into a Java object, a Java wrapper of type {@link PyObject} will be returned.
     * If the Python value is a wrapped Java object, it will be unwrapped.
     *
     * @param name      A name of the Python attribute.
     * @param valueType The type of the value or {@code null}, if unknown
     * @return The Python attribute value as Java object.
     */
    public <T> T getAttribute(String name, Class<T> valueType) {
        assertPythonRuns();
        return PyLib.getAttributeValue(getPointer(), name, valueType);
    }

    /**
     * Sets the value of a Python attribute from the given Java object.
     * <p/>
     * Appropriate type conversions from Java to Python will be performed as needed.
     * If the Java value cannot be converted into a Python object, a Java wrapper will be created instead.
     * If the Java value is a wrapped Python object of type {@link PyObject}, it will be unwrapped.
     *
     * @param name  A name of the Python attribute.
     * @param value The new attribute value as Java object.
     */
    public void setAttribute(String name, Object value) {
        assertPythonRuns();
        PyLib.setAttributeValue(getPointer(), name, value, value != null ? value.getClass() : (Class) null);
    }

    /**
     * Sets the value of a Python attribute from the given Java object and Java type (if given).
     * <p/>
     * Appropriate type conversions from Java to Python will be performed as needed using the given value type.
     * If the Java value cannot be converted into a Python object, a Java wrapper will be created instead.
     * If the Java value is a wrapped Python object of type {@link PyObject}, it will be unwrapped.
     *
     * @param name      A name of the Python attribute.
     * @param value     The new attribute value as Java object.
     * @param valueType The value type used for the conversion. May be {@code null}.
     */
    public <T> void setAttribute(String name, T value, Class<T> valueType) {
        assertPythonRuns();
        PyLib.setAttributeValue(getPointer(), name, value, valueType);
    }

    /**
     * Call the callable Python method with the given name and arguments.
     *
     * @param name A name of a Python attribute that evaluates to a callable object.
     * @param args The arguments for the method call.
     * @return A wrapper for the returned Python object.
     */
    public PyObject callMethod(String name, Object... args) {
        assertPythonRuns();
        long pointer = PyLib.callAndReturnObject(getPointer(), true, name, args.length, args, null);
        return pointer != 0 ? new PyObject(pointer) : null;
    }

    /**
     * Call the callable Python object with the given name and arguments.
     *
     * @param name A name of a Python attribute that evaluates to a callable object,
     * @param args The arguments for the call.
     * @return A wrapper for the returned Python object.
     */
    public PyObject call(String name, Object... args) {
        assertPythonRuns();
        long pointer = PyLib.callAndReturnObject(getPointer(), false, name, args.length, args, null);
        return pointer != 0 ? new PyObject(pointer) : null;
    }

    /**
     * Create a Java proxy instance of this Python object which contains compatible methods to the ones provided in the
     * interface given by the {@code type} parameter.
     *
     * @param type The interface's type.
     * @param <T>  The interface name.
     * @return A (proxy) instance implementing the given interface.
     */
    public <T> T createProxy(Class<T> type) {
        assertPythonRuns();
        return (T) createProxy(PyLib.CallableKind.METHOD, type);
    }

    /**
     * Create a Java proxy instance of this Python object (or module) which contains compatible methods
     * (or functions) to the ones provided in the interfaces given by all the {@code type} parameters.
     *
     * @param callableKind The kind of calls to be made.
     * @param types        The interface types.
     * @return A instance implementing the all the given interfaces which serves as a proxy for the given Python object (or module).
     */
    public Object createProxy(PyLib.CallableKind callableKind, Class<?>... types) {
        assertPythonRuns();
        ClassLoader classLoader = types[0].getClassLoader();
        InvocationHandler invocationHandler = new PyProxyHandler(this, callableKind);
        return Proxy.newProxyInstance(classLoader, types, invocationHandler);
    }

    /**
     * Gets a string representation of the object using the format "PyObject(pointer=<value>)".
     *
     * @return A string representation of the object.
     * @see #getPointer()
     */
    @Override
    public final String toString() {
        return String.format("%s(pointer=0x%s)", getClass().getSimpleName(), Long.toHexString(pointer));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The other object.
     * @return {@code true} if the other object is an instance of {@link org.jpy.PyObject} and if their pointers are equal, {@code false} otherwise.
     * @see #getPointer()
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PyObject)) {
            return false;
        }
        PyObject pyObject = (PyObject) o;
        return pointer == pyObject.pointer;
    }

    /**
     * Computes a hash code from this object's pointer value.
     *
     * @return A hash code value for this object.
     * @see #getPointer()
     */
    @Override
    public final int hashCode() {
        return (int) (pointer ^ (pointer >>> 32));
    }
}
