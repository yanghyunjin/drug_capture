LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := imgeffects
LOCAL_SRC_FILES := imgeffects.cpp
LOCAL_LDLIBS := -ljnigraphics -llog
include $(BUILD_SHARED_LIBRARY)