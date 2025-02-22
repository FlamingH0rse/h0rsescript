#ifndef KONAN_LIBH0_C_TYPES_H
#define KONAN_LIBH0_C_TYPES_H
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
typedef bool            libh0_c_types_KBoolean;
#else
typedef _Bool           libh0_c_types_KBoolean;
#endif
typedef unsigned short     libh0_c_types_KChar;
typedef signed char        libh0_c_types_KByte;
typedef short              libh0_c_types_KShort;
typedef int                libh0_c_types_KInt;
typedef long long          libh0_c_types_KLong;
typedef unsigned char      libh0_c_types_KUByte;
typedef unsigned short     libh0_c_types_KUShort;
typedef unsigned int       libh0_c_types_KUInt;
typedef unsigned long long libh0_c_types_KULong;
typedef float              libh0_c_types_KFloat;
typedef double             libh0_c_types_KDouble;
typedef float __attribute__ ((__vector_size__ (16))) libh0_c_types_KVector128;
typedef void*              libh0_c_types_KNativePtr;
struct libh0_c_types_KType;
typedef struct libh0_c_types_KType libh0_c_types_KType;

typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Byte;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Short;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Int;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Long;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Float;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Double;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Char;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Boolean;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Unit;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_UByte;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_UShort;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_UInt;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_ULong;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_Method;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_collections_Map;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_STR;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_NUM;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_BOOL;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_ARRAY;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_FUN;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_NULL;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_H0Type_ANY;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_collections_HashMap;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Function1;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Array;
typedef struct {
  libh0_c_types_KNativePtr pinned;
} libh0_c_types_kref_kotlin_Any;

extern libh0_c_types_kref_kotlin_collections_Map getMethods();
extern const char* getName();

typedef struct {
  /* Service functions. */
  void (*DisposeStablePointer)(libh0_c_types_KNativePtr ptr);
  void (*DisposeString)(const char* string);
  libh0_c_types_KBoolean (*IsInstance)(libh0_c_types_KNativePtr ref, const libh0_c_types_KType* type);
  libh0_c_types_kref_kotlin_Byte (*createNullableByte)(libh0_c_types_KByte);
  libh0_c_types_KByte (*getNonNullValueOfByte)(libh0_c_types_kref_kotlin_Byte);
  libh0_c_types_kref_kotlin_Short (*createNullableShort)(libh0_c_types_KShort);
  libh0_c_types_KShort (*getNonNullValueOfShort)(libh0_c_types_kref_kotlin_Short);
  libh0_c_types_kref_kotlin_Int (*createNullableInt)(libh0_c_types_KInt);
  libh0_c_types_KInt (*getNonNullValueOfInt)(libh0_c_types_kref_kotlin_Int);
  libh0_c_types_kref_kotlin_Long (*createNullableLong)(libh0_c_types_KLong);
  libh0_c_types_KLong (*getNonNullValueOfLong)(libh0_c_types_kref_kotlin_Long);
  libh0_c_types_kref_kotlin_Float (*createNullableFloat)(libh0_c_types_KFloat);
  libh0_c_types_KFloat (*getNonNullValueOfFloat)(libh0_c_types_kref_kotlin_Float);
  libh0_c_types_kref_kotlin_Double (*createNullableDouble)(libh0_c_types_KDouble);
  libh0_c_types_KDouble (*getNonNullValueOfDouble)(libh0_c_types_kref_kotlin_Double);
  libh0_c_types_kref_kotlin_Char (*createNullableChar)(libh0_c_types_KChar);
  libh0_c_types_KChar (*getNonNullValueOfChar)(libh0_c_types_kref_kotlin_Char);
  libh0_c_types_kref_kotlin_Boolean (*createNullableBoolean)(libh0_c_types_KBoolean);
  libh0_c_types_KBoolean (*getNonNullValueOfBoolean)(libh0_c_types_kref_kotlin_Boolean);
  libh0_c_types_kref_kotlin_Unit (*createNullableUnit)(void);
  libh0_c_types_kref_kotlin_UByte (*createNullableUByte)(libh0_c_types_KUByte);
  libh0_c_types_KUByte (*getNonNullValueOfUByte)(libh0_c_types_kref_kotlin_UByte);
  libh0_c_types_kref_kotlin_UShort (*createNullableUShort)(libh0_c_types_KUShort);
  libh0_c_types_KUShort (*getNonNullValueOfUShort)(libh0_c_types_kref_kotlin_UShort);
  libh0_c_types_kref_kotlin_UInt (*createNullableUInt)(libh0_c_types_KUInt);
  libh0_c_types_KUInt (*getNonNullValueOfUInt)(libh0_c_types_kref_kotlin_UInt);
  libh0_c_types_kref_kotlin_ULong (*createNullableULong)(libh0_c_types_KULong);
  libh0_c_types_KULong (*getNonNullValueOfULong)(libh0_c_types_kref_kotlin_ULong);

  /* User functions. */
  struct {
    struct {
      struct {
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for STR. */
        } STR;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for NUM. */
        } NUM;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for BOOL. */
        } BOOL;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for ARRAY. */
        } ARRAY;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for FUN. */
        } FUN;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for NULL. */
        } NULL;
        struct {
          libh0_c_types_kref_H0Type (*get)(); /* enum entry for ANY. */
        } ANY;
        libh0_c_types_KType* (*_type)(void);
      } H0Type;
      struct {
        libh0_c_types_KType* (*_type)(void);
        libh0_c_types_kref_Method (*Method)(libh0_c_types_kref_kotlin_collections_HashMap parameters, libh0_c_types_kref_H0Type returnType, libh0_c_types_kref_kotlin_Function1 runnable);
        libh0_c_types_kref_kotlin_collections_HashMap (*get_parameters)(libh0_c_types_kref_Method thiz);
        libh0_c_types_kref_H0Type (*get_returnType)(libh0_c_types_kref_Method thiz);
        libh0_c_types_kref_kotlin_Function1 (*get_runnable)(libh0_c_types_kref_Method thiz);
        libh0_c_types_kref_kotlin_Any (*execute)(libh0_c_types_kref_Method thiz, libh0_c_types_kref_kotlin_Array arguments);
      } Method;
      libh0_c_types_kref_Method (*get_add)();
      libh0_c_types_kref_Method (*get_subtract)();
      libh0_c_types_kref_kotlin_collections_Map (*getMethods_)();
      const char* (*getName_)();
    } root;
  } kotlin;
} libh0_c_types_ExportedSymbols;
extern libh0_c_types_ExportedSymbols* libh0_c_types_symbols(void);
#ifdef __cplusplus
}  /* extern "C" */
#endif
#endif  /* KONAN_LIBH0_C_TYPES_H */
