#ifndef LIBMATCHDLL_H
#define LIBMATCHDLL_H
#ifndef IN
#define IN
#endif

#ifndef OUT
#define OUT
#endif

#ifndef ZKAPI
#define ZKAPI
#endif

#ifndef BOOL
#define BOOL int
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef TRUE
#define TRUE 1
#endif

#ifndef ZK_INTERFACE
#ifndef ZKLICSERVER_EXPORTS
#define ZK_INTERFACE __declspec(dllimport)
#else
#define ZK_INTERFACE __attribute__((visibility("default")))
#endif//ZKLICSERVER_EXPORTS
#endif//ZK_INTERFACE


#ifdef __cplusplus
extern "C"
{
#endif

ZK_INTERFACE int ZKAPI SetThreshold(int nThreshold, int nOneToOneThreshold);

ZK_INTERFACE int ZKAPI process(const char *szRegTemplate, const char *szVerTemplate);

ZK_INTERFACE int ZKAPI Verify(const char *szRegTemplate, const char *szVerTemplate);

#ifdef __cplusplus
};
#endif


#endif

