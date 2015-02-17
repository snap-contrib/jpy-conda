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

#ifndef JPY_JMETHOD_H
#define JPY_JMETHOD_H

#ifdef __cplusplus
extern "C" {
#endif

#include "jpy_compat.h"

/**
 * Python object representing a Java method. It's type is 'JMethod'.
 */
typedef struct
{
    PyObject_HEAD

    // Method name.
    PyObject* name;
    // Method parameter count.
    int paramCount;
    // Method is static?
    char isStatic;
    // Method parameter types. Will be NULL, if parameter_count == 0.
    JPy_ParamDescriptor* paramDescriptors;
    // Method return type. Will be NULL for constructors.
    JPy_ReturnDescriptor* returnDescriptor;
    // Method ID retrieved from JNI.
    jmethodID mid;
}
JPy_JMethod;

/**
 * The Python 'JMethod' type singleton.
 */
extern PyTypeObject JMethod_Type;

/**
 * Python object representing an overloaded Java method. It's type is 'JOverloadedMethod'.
 */
typedef struct
{
    PyObject_HEAD

    // The declaring class.
    JPy_JType* declaringClass;
    // Method name.
    PyObject* name;
    // List of method overloads (a PyList with items of type JPy_JMethod).
    PyObject* methodList;
}
JPy_JOverloadedMethod;

/**
 * The Python 'JOverloadedMethod' type singleton.
 */
extern PyTypeObject JOverloadedMethod_Type;

JPy_JMethod*           JOverloadedMethod_FindMethod(JNIEnv* jenv, JPy_JOverloadedMethod* overloadedMethod, PyObject* argTuple, jboolean visitSuperClass);
JPy_JMethod*           JOverloadedMethod_FindStaticMethod(JPy_JOverloadedMethod* overloadedMethod, PyObject* argTuple);
JPy_JOverloadedMethod* JOverloadedMethod_New(JPy_JType* declaringClass, PyObject* name, JPy_JMethod* method);
int                    JOverloadedMethod_AddMethod(JPy_JOverloadedMethod* overloadedMethod, JPy_JMethod* method);

JPy_JMethod* JMethod_New(PyObject* name,
                         int paramCount,
                         JPy_ParamDescriptor* paramDescriptors,
                         JPy_ReturnDescriptor* returnDescriptor,
                         jboolean isStatic,
                         jmethodID mid);

void JMethod_Del(JPy_JMethod* method);

int JMethod_ConvertToJavaValues(JNIEnv* jenv, JPy_JMethod* jMethod, int argCount, PyObject* argTuple, jvalue* jArgs);

int  JMethod_CreateJArgs(JNIEnv* jenv, JPy_JMethod* jMethod, PyObject* argTuple, jvalue** jValues, JPy_ArgDisposer** jDisposers);
void JMethod_DisposeJArgs(JNIEnv* jenv, int paramCount, jvalue* jValues, JPy_ArgDisposer* jDisposers);

#ifdef __cplusplus
}  /* extern "C" */
#endif

#endif /* !JPY_JMETHOD_H */