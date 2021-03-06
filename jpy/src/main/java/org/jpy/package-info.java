/**
 * Provides the classes necessary to
 * <ol>
 *     <li>create an embedded Python VM,</li>
 *     <li>to execute Python code,</li>
 *     <li>to import Python modules,</li>
 *     <li>to access Python variables, and finally</li>
 *     <li>to call Python functions and class methods.</li>
 * </ol>
 *
 * <p>
 * The entry point to the jpy Java API is the {@link org.jpy.PyLib} class which is used to either
 * detect an already running Python interpreter or to start a new one:
 *
 * <pre>
 *     if (!PyLib.isPythonRunning()) {
 *         PyLib.startPython(opt1, opt2, ...);
 *     }
 * </pre>
 *
 * jpy API clients should first call {@link org.jpy.PyLib#isPythonRunning()} in order to check if a Python interpreter is already available.
 * If not, {@link org.jpy.PyLib#startPython(String...)} must be called before any other jpy API is used.
 * <p/>
 *
 * <p>
 * Once the Python interpreter in running clients can either execute Python code directly using the {@link org.jpy.PyLib#execScript(String)} method or
 * load a Python module using {@link org.jpy.PyModule#importModule(String)}. The returned {@link org.jpy.PyModule} object
 * then is the entry point to access Python variables and invoke functions.
 * <p/>
 *
 * <p>
 * Some {@link org.jpy.PyModule} methods return {@link org.jpy.PyObject} instances. These can be used to
 * create instances of Python classes, and to access Python class members, attributes and to invoke Python object methods..
 * <p/>
 *
 * @since 0.7
 */
package org.jpy;