#ifndef _API_H_
#define _API_H_


int Lib_ApiModemInit();
int Lib_ApiModemDialUp(unsigned char *DialNo, int mode);
int Lib_ApiModemHookOn(void);
int Lib_ApiModemHookOff(void);
int Lib_ApiModemSendString(unsigned char *SendData, int len);
int Lib_ApiModemRcvString(unsigned char *stringdata, int *length);
int Lib_ApiModemSendAtcmd(unsigned char *ATCmd, int sendLen, unsigned char *ATout, int *outLen, int timeout);
int Lib_ApiModemSetEcho(int mode);

#endif
