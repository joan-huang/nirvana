
#ifdef ANDROID
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "ffmpeg_nirvana", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "ffmpeg_nirvana", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf("ffmpeg_nirvana" format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf("ffmpeg_nirvana" format "\n", ##__VA_ARGS__)
#endif
