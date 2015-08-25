# XPom (XPath to Object Mapping)

XPom performs mapping of various xml documents to POJOs using simple annotations with XPaths.

It supports seamless, out-of-the-box data type conversion of:
- fields with <code>primitive</code> types: <code>byte, short, int, long, float, double, boolean, char</code>.
- fields with <code>wrapped</code> types: <code>Byte, Short, Integer, Long, Float, Double, Boolean, Character</code>.
- fields containing <code>enumerations</code>
- fields containing <code>Simple Data Objects</code> like Numbers and Strings.
- fields containing arrays of <code>primitive</code> types.
- fields containing arrays of <code>wrapped</code> types.
- fields containing arrays of <code>Simple Data Objects</code> and <code>enumerations</code>
- fields containing <code>collections</code> of <code>Simple Data Objects</code>

Exception handling strategies levels (down-to-top):
- JVM Level - can be overridden by setting a value on XPomFactory
- Class Level - can be overridden by setting a value on a particular class mapper
- Field Level - can be overridden by setting a value for a particular field on a particular class mapper (not recommended)

Default exception handling strategy - to FAIL always. Predefined strategies:
  - FAIL - Throws an exception if either value isn't present in the xml or conversion failed for mandatory fields
  - USE_DEFAULT - Uses either java default value or user's defined default value when value isn't present or conversion exception occurs
