/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2000-2012 SuperWaba Ltda.                                      *
 *  Copyright (C) 2012-2020 TotalCross Global Mobile Platform Ltda.   
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  This file is covered by the GNU LESSER GENERAL PUBLIC LICENSE VERSION 2.1    *
 *  A copy of this license is located in file license.txt at the root of this    *
 *  SDK or can be downloaded here:                                               *
 *  http://www.gnu.org/licenses/lgpl-2.1.txt                                     *
 *                                                                               *
 *********************************************************************************/

#ifndef DEBUG_H
#define DEBUG_H

#define ERASE_DEBUG_STR "!erase debug!"
#define ALTERNATIVE_DEBUG "!alt_debug!"

#ifdef __cplusplus
 extern "C" {
#endif

bool initDebug();
void destroyDebug();
TC_API bool trace(char *s); // used to trace function calls. also prints the memory available
typedef bool (*traceFunc)(char *s);
TC_API bool debug(const char *s, ...); // this is a message that the user may see or not
typedef bool (*debugFunc)(const char *s, ...);
TC_API bool alert(char *s, ...); // this is a message that will popup to the user immediately
typedef bool (*alertFunc)(char *s, ...);
void deleteDebugFile();
bool debugStr(char *s); // debugs the string

TC_API void tcabort(char* msg, char* file, int32 line);
#define TCABORT tcabort("", __FILE__,__LINE__)

#ifdef __cplusplus
 } // __cplusplus
#endif

#endif
