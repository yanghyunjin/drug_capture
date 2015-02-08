LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=STATIC
include /Users/yanghyeonjin/Desktop/sswm/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk
LOCAL_DEFAULT_CPP_EXTENSION := cpp
LOCAL_MODULE := imgeffects
LOCAL_SRC_FILES := imgeffects.cpp
LOCAL_LDLIBS += -llog -landroid -lEGL -lGLESv1_CM
LOCAL_CFLAGS := -DCONFIG_EMBEDDED -DUSE_IND_THREAD 
include $(BUILD_SHARED_LIBRARY)