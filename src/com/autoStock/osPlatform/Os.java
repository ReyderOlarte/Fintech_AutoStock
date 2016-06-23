/**
 * 
 */
package com.autoStock.osPlatform;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author Kevin Kowalewski
 *
 */
public class Os {
	public static enum OsType{
		linux,
		osx,
		windows,
		unknown
	}
	
	public static OsType identifyOS(){
		if (SystemUtils.IS_OS_LINUX){
			return OsType.linux;
		}else if (SystemUtils.IS_OS_MAC_OSX){
			return OsType.osx;
		}else if (SystemUtils.IS_OS_WINDOWS){
			return OsType.windows;
		}
		
		return OsType.unknown;
	}
}
